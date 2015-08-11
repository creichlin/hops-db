package ch.kerbtier.hopsdb;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.sql.DataSource;

import ch.kerbtier.hopsdb.exceptions.DbSQLException;
import ch.kerbtier.hopsdb.exceptions.NoMatchFound;
import ch.kerbtier.hopsdb.exceptions.WrongNumberOfKeys;
import ch.kerbtier.hopsdb.model.ColumnModel;
import ch.kerbtier.hopsdb.model.ModelProvider;
import ch.kerbtier.hopsdb.model.TableModel;

import com.mchange.v2.c3p0.DataSources;

public class Db {
  // private static Logger logger = Logger.getLogger(Db.class.getName());
  private DataSource ds;
  private ThreadLocal<Connection> connection = new ThreadLocal<>();
  private int commits = 0;
  private int rollbacks = 0;
  private int statements = 0;
  
  private ModelProvider models;

  public Db(String url, ModelProvider models) {
    this.models = models;
    boolean usePool = true;
    if (usePool) {

      Map<String, String> cpc = new HashMap<>();
      cpc.put("maxIdleTime", "60");
      cpc.put("maxConnectionAge", "600");
      cpc.put("testConnectionOnCheckout", "true");

      cpc.put("debugUnreturnedConnectionStackTraces", "true");

      cpc.put("unreturnedConnectionTimeout", "10");

      try {
        ds = DataSources.pooledDataSource(DataSources.unpooledDataSource(url), cpc);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {

      try {
        ds = DataSources.unpooledDataSource(url);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  private Connection getConnection() {
    if (connection.get() == null) {
      try {
        connection.set(ds.getConnection());

        if (connection.get().getAutoCommit() == true) {
          connection.get().setAutoCommit(false);
        }
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    return connection.get();
  }

  public boolean hasTables(boolean caseSensitive, String... tables) throws SQLException {
    Set<String> required = new HashSet<>();
    Set<String> found = new HashSet<>();

    for (String req : tables) {
      if (caseSensitive) {
        required.add(req);
      } else {
        required.add(req.toUpperCase());
      }
    }

    for (String fo : getTableNames()) {
      if (caseSensitive) {
        found.add(fo);
      } else {
        found.add(fo.toUpperCase());
      }
    }

    return found.containsAll(required);
  }

  public boolean hasTables(String... tables) throws SQLException {
    return hasTables(false, tables);
  }

  /**
   * get table names with no case change
   */
  public Set<String> getTableNames() throws SQLException {
    DatabaseMetaData dbmd = getConnection().getMetaData();

    ResultSet rs = dbmd.getTables(null, null, "%", null);
    Set<String> foundTables = new HashSet<>();
    while (rs.next()) {
      foundTables.add(rs.getString(3));
    }
    return foundTables;
  }

  public void commit() {
    try {
      Connection con = connection.get();
      if (con != null) {
        connection.set(null);
        con.commit();
        con.close();
        commits++;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void rollback() {
    try {
      Connection con = connection.get();
      if (con != null) {
        connection.set(null);
        con.rollback();
        con.close();
        rollbacks++;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public DbPs prepareStatement(String sql) {
    return prepareStatement(sql, false);
  }

  public DbPs prepareStatement(String sql, boolean returnKeys) {
      statements++;
      PreparedStatement ps;
      try {
        ps = getConnection().prepareStatement(sql,
            returnKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
      } catch (SQLException e) {
        throw new DbSQLException(e);
      }
      return new DbPs(this, ps, sql);
  }

  /**
   * selects one single row, keys is given as parameter, only one key supported
   * because order is undefined.
   */
  public <X> X select(Class<X> type, int id) throws SQLException, NoMatchFound {
    TableModel<?> tModel = models.getModel(type);

    if (tModel.keysCount() != 1) {
      throw new WrongNumberOfKeys("model must have exact one key to select by id but has " + tModel.keysCount());
    }

    DbPs ps = prepareStatement("select * from `" + tModel.getName() + "` where `"
        + tModel.keys().iterator().next().getName() + "` = ?");
    ps.setInt(1, id);

    DbRs rs = ps.executeQuery();
    if (rs.next()) {
      return rs.populate(type);
    } else {
      throw new NoMatchFound("no instance found for key " + id);
    }
  }

  public <X> List<X> select(Class<X> type) throws SQLException {
    TableModel<?> tModel = models.getModel(type);

    DbPs ps = prepareStatement("select * from `" + tModel.getName() + "`");
    DbRs rs = ps.executeQuery();

    List<X> result = new ArrayList<>();
    while (rs.next()) {
      result.add(rs.populate(type));
    }

    return result;
  }

  /**
   * selects one single row, identified by where and params
   */
  public <X> X selectFirst(Class<X> type, String where, Object... params) throws SQLException, NoMatchFound {
    TableModel<?> tModel = models.getModel(type);

    DbPs ps = prepareStatement("select * from `" + tModel.getName() + "` where " + where);
    int pos = 1;
    for (Object p : params) {
      if (p instanceof String) {
        ps.setString(pos++, (String) p);
      } else if (p instanceof Date) {
        ps.setDate(pos++, (Date) p);
      } else if (p instanceof Integer) {
        ps.setInt(pos++, (Integer) p);
      } else {
        throw new RuntimeException("invalid param " + p);
      }
    }

    DbRs rs = ps.executeQuery();
    if (rs.next()) {
      return rs.populate(type);
    } else {
      throw new NoMatchFound("no instance found for " + where + " : " + params);
    }
  }

  /**
   * selects all found elements, identified by where and params
   */
  public <X> List<X> select(Class<X> type, String where, Object... params) throws SQLException {
    TableModel<?> tModel = models.getModel(type);
    List<X> result = new ArrayList<>();
    DbPs ps = prepareStatement("select * from `" + tModel.getName() + "` where " + where);
    int pos = 1;
    for (Object p : params) {
      if (p instanceof String) {
        ps.setString(pos++, (String) p);
      } else if (p instanceof Date) {
        ps.setDate(pos++, (Date) p);
      } else if (p instanceof Integer) {
        ps.setInt(pos++, (Integer) p);
      } else {
        throw new RuntimeException("invalid param " + p);
      }
    }

    DbRs rs = ps.executeQuery();
    while (rs.next()) {
      result.add(rs.populate(type));
    }
    return result;
  }

  public DbPs prepareDelete(Class<?> type) {
    TableModel<?> tModel = models.getModel(type);

    if (!tModel.keys().iterator().hasNext()) {
      throw new WrongNumberOfKeys("to delete entitiy it needs at least one key");
    }

    StringBuilder statemenet = new StringBuilder("DELETE FROM " + tModel.getName() + " WHERE ");

    for (ColumnModel<?> cModel : tModel.keys()) {
      statemenet.append("`" + cModel.getName() + "` = ? AND ");
    }
    statemenet.setLength(statemenet.length() - 5);

    return prepareStatement(statemenet.toString());
  }

  public void delete(Object instance) throws SQLException {
    DbPs ps = prepareDelete(instance.getClass());
    ps.setEntityKeys(instance);
    ps.executeUpdate();
  }

  /**
   * creates an update statement, using all key columns to identify instance
   * 
   * @param type
   * @return
   */
  public DbPs prepareUpdate(Class<?> type) {
    TableModel<?> tModel = models.getModel(type);

    if (!tModel.keys().iterator().hasNext()) {
      throw new RuntimeException("to update entitiy it needs at least one key");
    }

    StringBuilder statemenet = new StringBuilder("UPDATE " + tModel.getName() + " SET ");

    for (ColumnModel<?> cModel : tModel.columns()) {
      statemenet.append("`" + cModel.getName() + "` = ?, ");
    }
    statemenet.setLength(statemenet.length() - 2);
    statemenet.append(" WHERE ");

    for (ColumnModel<?> cModel : tModel.keys()) {
      statemenet.append("`" + cModel.getName() + "` = ? AND ");
    }
    statemenet.setLength(statemenet.length() - 5);

    return prepareStatement(statemenet.toString());
  }

  public void update(Object instance) throws SQLException {
    DbPs ps = prepareUpdate(instance.getClass());
    ps.setEntity(instance);
    ps.executeUpdate();
  }

  /**
   * creates a create statement for all non key columns. if a key column needs
   * to be set too it needs to be added in explicitKeys
   * 
   * @param type
   * @param explicitKeys
   * @return
   */
  public DbPs prepareCreate(Class<?> type, String... explicitKeys) {
    TableModel<?> tModel = models.getModel(type);

    StringBuilder statemenet = new StringBuilder("INSERT INTO " + tModel.getName() + " (");

    List<ColumnModel<?>> columns = new ArrayList<>();
    for (String field : explicitKeys) {
      columns.add(tModel.getColumn(field));
    }
    for (ColumnModel<?> cm : tModel.columns()) {
      columns.add(cm);
    }

    for (ColumnModel<?> cModel : columns) {
      statemenet.append("`" + cModel.getName() + "`, ");
    }
    if (columns.size() > 0) {
      statemenet.setLength(statemenet.length() - 2);
    }
    statemenet.append(") VALUES(");

    for (@SuppressWarnings("unused")
    ColumnModel<?> cModel : columns) {
      statemenet.append("?, ");
    }
    if (columns.size() > 0) {
      statemenet.setLength(statemenet.length() - 2);
    }
    statemenet.append(")");
    return prepareStatement(statemenet.toString(), true);
  }

  public int count(Class<?> type) throws SQLException {
    TableModel<?> tModel = models.getModel(type);
    String sql = "SELECT COUNT(*) FROM " + tModel.getName();
    DbPs ps = prepareStatement(sql);
    DbRs rs = ps.executeQuery();
    rs.next();

    return rs.getInt(1);
  }

  /**
   * creates an instance of this object and populates generated keys
   * 
   * it is assumed that all columns of type key are generated, if not so, those
   * table names have to given as explicit keys
   * 
   * @param instance
   * @param explicitKeys
   * @throws SQLException
   */
  public <T extends Object> void create(T instance, String... explicitKeys) throws SQLException {
    DbPs ps = prepareCreate(instance.getClass(), explicitKeys);
    int index = ps.setEntityForColumns(instance, 1, explicitKeys);
    ps.setEntityColumns(instance, index);
    ps.executeUpdate();

    // update ids in instance
    TableModel<T> tModel = (TableModel<T>) models.getModel(instance.getClass());

    ResultSet rs = ps.getGeneratedKeys();

    Set<String> explicitKeySet = new HashSet<>();
    explicitKeySet.addAll(Arrays.asList(explicitKeys));

    for (ColumnModel<T> key : tModel.keys()) {
      if (!explicitKeySet.contains(key.getName())) {
        if (rs.next()) {
          key.set(instance, rs.getInt(1));
        } else {
          throw new RuntimeException("invalid amount of generated keys");
        }
      }
    }

    if (rs.next()) {
      throw new RuntimeException("invalid amount of generated keys");
    }
  }

  public String printInfo() {
    return "JdbcInfos:statements = " + statements + ", commits = " + commits + ", rollbacks = " + rollbacks + "]";
  }

  public void destroy() {
    try {
      DataSources.destroy(ds);
      Logger logger = Logger.getLogger(Db.class.getName());
      try {
        Class<?> cls = Class.forName("com.mysql.jdbc.AbandonedConnectionCleanupThread");
        Method mth = (cls == null ? null : cls.getMethod("shutdown"));
        if (mth != null) {
          logger.info("MySQL connection cleanup thread shutdown");
          mth.invoke(null);
          logger.info("MySQL connection cleanup thread shutdown successful");
        }
      } catch (Throwable thr) {
        logger.warning("Failed to shutdown SQL connection cleanup thread (might cause memory leak): "
            + thr.getMessage());
        thr.printStackTrace();
      }

      // Now deregister JDBC drivers in this context's ClassLoader:
      // Get the webapp's ClassLoader
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      // Loop through all drivers
      Enumeration<Driver> drivers = DriverManager.getDrivers();
      while (drivers.hasMoreElements()) {
        Driver driver = drivers.nextElement();
        if (driver.getClass().getClassLoader() == cl) {
          // This driver was registered by the webapp's ClassLoader, so
          // deregister it:
          try {
            logger.info("Deregistering JDBC driver " + driver);
            DriverManager.deregisterDriver(driver);
          } catch (SQLException ex) {
            logger.warning("Error deregistering JDBC driver " + driver + " " + ex.getMessage());
          }
        } else {
          // driver was not registered by the webapp's ClassLoader and may be in
          // use elsewhere
          logger
              .fine("Not deregistering JDBC driver " + driver + " as it does not belong to this webapp's ClassLoader");
        }
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean hasOpenConnection() {
    return connection.get() != null;
  }
  
  public ModelProvider getModels() {
    return models;
  }
}
