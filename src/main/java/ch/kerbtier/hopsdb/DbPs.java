package ch.kerbtier.hopsdb;

import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ch.kerbtier.hopsdb.exceptions.DbSQLException;
import ch.kerbtier.hopsdb.model.ColumnModel;
import ch.kerbtier.hopsdb.model.TableModel;

import com.google.common.collect.Iterables;

public class DbPs {
  private PreparedStatement ps;
  private String sql;
  private Db db;

  public DbPs(Db db, PreparedStatement ps, String sql) {
    this.ps = ps;
    this.sql = sql;
    this.db = db;
  }

  public int executeUpdate() throws SQLException {
    db.print(sql);
    return ps.executeUpdate();
  }

  public DbRs executeQuery() throws SQLException {
    db.print(sql);
    return new DbRs(db, ps.executeQuery());
  }

  public <T> T getGeneratedKey(Class<T> type) throws SQLException {
    ResultSet rs = ps.getGeneratedKeys();
    if (rs.next()) {
      return rs.getObject(1, type);
    }
    throw new DbSQLException("problem fetching generated key");
  }

  public ResultSet getGeneratedKeys() throws SQLException {
    return ps.getGeneratedKeys();
  }

  public void close() throws SQLException {
    ps.close();
  }

  /**
   * Ads all non key values as parameters to this prepared statement. starting
   * with first index.
   * 
   * @param object
   * @return last used index + 1
   * @throws SQLException
   */
  public int setEntityColumns(Object object) throws SQLException {
    return setEntityColumns(object, 1);
  }

  /**
   * Ads all non key values as parameters to this prepared statement. starting
   * with given index.
   * 
   * @param object
   * @return last used index + 1
   * @throws SQLException
   */
  public int setEntityColumns(Object object, int index) throws SQLException {
    TableModel<Object> tm = (TableModel<Object>) db.getModels().getModel(object.getClass());
    return setColumns(object, tm.columns(), index);
  }

  /**
   * Ads all columns or keys, defined by cols... as parameters. starting with
   * given index.
   * 
   * @param object
   * @return last used index + 1
   * @throws SQLException
   */
  public int setEntityForColumns(Object object, int index, String... cols) throws SQLException {
    TableModel<Object> tm = (TableModel<Object>) db.getModels().getModel(object.getClass());
    List<ColumnModel<Object>> models = new ArrayList<>();

    for (String field : cols) {
      models.add(tm.getColumn(field));
    }

    return setColumns(object, models, index);
  }

  /**
   * Adds all keys as parameters to this prepared statement. Starting with index
   * 1.
   * 
   * @return last used index + 1
   */
  public int setEntityKeys(Object object) throws SQLException {
    return setEntityKeys(object, 1);
  }

  /**
   * Adds all keys as parameters to this prepared statement. Starting with given
   * index.
   * 
   * @return last used index + 1
   */
  public int setEntityKeys(Object object, int index) throws SQLException {
    TableModel<Object> tm = (TableModel<Object>) db.getModels().getModel(object.getClass());
    return setColumns(object, tm.keys(), index);
  }

  /**
   * Adds all keys and columns as parameters to this prepared statement.
   * Starting with index 1.
   * 
   * @return last used index + 1
   */
  public int setEntity(Object object) throws SQLException {
    return setEntity(object, 1);
  }

  /**
   * Adds all keys and columns as parameters to this prepared statement.
   * Starting with given index.
   * 
   * @return last used index + 1
   */
  public int setEntity(Object object, int index) throws SQLException {
    TableModel<Object> tm = (TableModel<Object>) db.getModels().getModel(object.getClass());
    Iterable<ColumnModel<Object>> cols = Iterables.concat(tm.columns(), tm.keys());

    return setColumns(object, cols, index);
  }

  /**
   * Sets all values of this prepared statements as parameters defined by the
   * cols parameter using object as source.
   * 
   * @param object
   * @param cols
   * @param index
   * @return last used index + 1
   * @throws SQLException
   */
  private int setColumns(Object object, Iterable<ColumnModel<Object>> cols, int index) throws SQLException {
    for (ColumnModel<Object> cm : cols) {
      Object value = cm.get(object);

      setParameter(index++, value);
    }
    return index;
  }

  /**
   * sets a list of parameters, starting with index 1
   * 
   * @param parameters
   * @throws SQLException
   */
  public void setParameters(List<Object> parameters) throws SQLException {
    for (int cnt = 0; cnt < parameters.size(); cnt++) {
      setParameter(cnt + 1, parameters.get(cnt));
    }
  }

  /**
   * sets a single parameter by index
   * 
   * @param index
   * @param value
   * @throws SQLException
   */
  public void setParameter(int index, Object value) throws SQLException {
    ps.setObject(index, value);
  }
}
