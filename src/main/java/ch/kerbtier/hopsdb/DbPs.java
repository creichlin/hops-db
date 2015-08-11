package ch.kerbtier.hopsdb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import ch.kerbtier.hopsdb.exceptions.NoMatchFound;
import ch.kerbtier.hopsdb.model.ColumnModel;
import ch.kerbtier.hopsdb.model.TableModel;

import com.google.common.collect.Iterables;

public class DbPs {
  private PreparedStatement ps;
  @SuppressWarnings("unused")
  private String sql;
  private Db db;

  public DbPs(Db db, PreparedStatement ps, String sql) {
    this.ps = ps;
    this.sql = sql;
    this.db = db;
  }

  public void setString(int i, String string) throws SQLException {
    ps.setString(i, string);
  }

  public void setDate(int i, Date date) throws SQLException {
    if (date != null) {
      ps.setDate(i, new java.sql.Date(date.getTime()));
    } else {
      ps.setDate(i, null);
    }
  }

  public void setDateTime(int i, Date date) throws SQLException {
    if (date != null) {
      ps.setTimestamp(i, new java.sql.Timestamp(date.getTime()));
    } else {
      ps.setTimestamp(i, null);
    }
  }

  public int executeUpdate() throws SQLException {
    return ps.executeUpdate();
  }

  public long getGeneratedKey() throws SQLException {
    ResultSet rs = ps.getGeneratedKeys();
    rs.next();
    return rs.getLong(1);
  }

  public ResultSet getGeneratedKeys() throws SQLException {
    return ps.getGeneratedKeys();
  }

  public void setLong(int i, Long l) throws SQLException {
    if (l == null) {
      ps.setNull(i, Types.BIGINT);
    } else {
      ps.setLong(i, l);
    }
  }

  public DbRs executeQuery() throws SQLException {
    return new DbRs(db, ps.executeQuery());
  }

  public void setInt(int i, Integer in) throws SQLException {
    if (in == null) {
      ps.setNull(i, Types.BIGINT);
    } else {
      ps.setLong(i, in);
    }
  }

  public void setBigDecimal(int i, BigDecimal bd) throws SQLException {
    if (bd == null) {
      ps.setNull(i, Types.DECIMAL);
    } else {
      ps.setBigDecimal(i, bd);
    }
  }

  public void setBoolean(int i, boolean value) throws SQLException {
    ps.setBoolean(i, value);
  }

  public void setDecimal(int i, BigDecimal value) throws SQLException {
    ps.setBigDecimal(i, value);
  }

  public void close() throws SQLException {
    ps.close();
  }

  public int getGeneratedIntKey() throws SQLException {
    ResultSet rs = ps.getGeneratedKeys();
    rs.next();
    return rs.getInt(1);
  }

  public <T> T selectFirst(Class<T> type) throws SQLException {
    List<T> list = select(type);

    if (list.size() == 0) {
      throw new NoMatchFound("no match found for " + ps);
    }

    return list.get(0);
  }

  public <T> List<T> select(Class<T> type) throws SQLException {
    List<T> list = new ArrayList<T>();

    DbRs result = executeQuery();

    while (result.next()) {
      T element = result.populate(type);
      list.add(element);
    }
    return list;
  }

  /**
   * calls for each id in ids the select method which executes the query. id is
   * set as the first slot/?
   * 
   * @param type
   * @param ids
   * @return
   * @throws SQLException
   */
  public <T> List<T> selectEach(Class<T> type, List<Integer> ids) throws SQLException {
    List<T> list = new ArrayList<>();

    for (Integer i : ids) {
      setInt(1, i);
      list.addAll(select(type));
    }

    return list;
  }

  public int setEntityColumns(Object object) throws SQLException {
    return setEntityColumns(object, 1);
  }

  public int setEntityColumns(Object object, int index) throws SQLException {
    TableModel<Object> tm = (TableModel<Object>) db.getModels().getModel(object.getClass());
    return setColumns(object, tm.columns(), index);
  }

  public int setEntityForColumns(Object object, int index, String... cols) throws SQLException {
    TableModel<Object> tm = (TableModel<Object>) db.getModels().getModel(object.getClass());
    List<ColumnModel<Object>> models = new ArrayList<>();

    for (String field : cols) {
      models.add(tm.getColumn(field));
    }

    return setColumns(object, models, index);
  }

  public int setEntityKeys(Object object) throws SQLException {
    return setEntityKeys(object, 1);
  }

  public int setEntityKeys(Object object, int index) throws SQLException {
    TableModel<Object> tm = (TableModel<Object>) db.getModels().getModel(object.getClass());
    return setColumns(object, tm.keys(), index);
  }

  public int setEntity(Object object) throws SQLException {
    return setEntity(object, 1);
  }

  public int setEntity(Object object, int index) throws SQLException {
    TableModel<Object> tm = (TableModel<Object>) db.getModels().getModel(object.getClass());
    Iterable<ColumnModel<Object>> cols = Iterables.concat(tm.columns(), tm.keys());

    return setColumns(object, cols, index);
  }

  private int setColumns(Object object, Iterable<ColumnModel<Object>> cols, int start) throws SQLException {
    int index = start;
    for (ColumnModel<Object> cm : cols) {
      Object value = cm.get(object);

      setParameter(index++, value);
    }
    return index;
  }

  public void setParameters(List<Object> parameters) throws SQLException {
    for (int cnt = 0; cnt < parameters.size(); cnt++) {
      setParameter(cnt + 1, parameters.get(cnt));
    }
  }

  public void setParameter(int index, Object value) throws SQLException {
    if (value instanceof String) {
      setString(index, (String) value);
    } else if (value instanceof Long) {
      setLong(index, (Long) value);
    } else if (value instanceof Integer) {
      setInt(index, (Integer) value);
    } else if (value instanceof Date) {
      setDateTime(index, (Date) value);
    } else if (value instanceof BigDecimal) {
      setBigDecimal(index, (BigDecimal) value);
    }
  }
}
