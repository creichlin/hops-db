package ch.kerbtier.hopsdb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

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

  public boolean hasTables(String... tables) throws SQLException {
    Set<String> required = new HashSet<>();
    Set<String> found = getTableNames();

    for (String req : tables) {
      required.add(req.toUpperCase());
    }

    return found.containsAll(required);
  }

  /**
   * get table names, always as uppercase
   */
  public Set<String> getTableNames() throws SQLException {
    DatabaseMetaData dbmd = getConnection().getMetaData();

    ResultSet rs = dbmd.getTables(null, null, "%", null);
    Set<String> foundTables = new HashSet<>();
    while (rs.next()) {
      foundTables.add(rs.getString(3).toUpperCase());
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

  public DbPs prepareStatement(String sql) throws SQLException {
    return prepareStatement(sql, false);
  }

  public DbPs prepareStatement(String sql, boolean returnKeys) throws SQLException {
    statements++;
    PreparedStatement ps;
      ps = getConnection().prepareStatement(sql,
          returnKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
    return new DbPs(this, ps, sql);
  }

  /**
   * selects one single row, keys is given as parameter, only one key supported
   * because order of keys is unknown.
   * 
   * is a shortcut for return select(type).byPk(id).first();
   */
  public <X> X select(Class<X> type, int id) throws SQLException, NoMatchFound {
    return select(type).byPk(id).first();
  }

  public <X> Query<X> select(Class<X> type) {
    TableModel<X> tModel = models.getModel(type);
    
    Query<X> query = new Query<>(this, tModel);
    
    return query;
  }

  private DbPs prepareDelete(Class<?> type) throws SQLException {
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
   * @throws SQLException 
   */
  private DbPs prepareUpdate(Class<?> type) throws SQLException {
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
   * @throws SQLException 
   */
  private DbPs prepareCreate(Class<?> type, String... explicitKeys) throws SQLException {
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
