package ch.kerbtier.hopsdb;

import java.sql.ResultSet;
import java.sql.SQLException;

import ch.kerbtier.hopsdb.impl.Converters;
import ch.kerbtier.hopsdb.model.ColumnModel;
import ch.kerbtier.hopsdb.model.TableModel;

public class DbRs {
  private ResultSet rs;
  private Db db;

  public DbRs(Db db, ResultSet rs) {
    this.rs = rs;
    this.db = db;
  }

  public <T> T populate(Class<T> type) {
    try {
      T obj = type.newInstance();

      TableModel<T> model = db.getModels().getModel(type);
      for (ColumnModel<T> cool : model) {
        cool.set(obj, get(cool.getName(), cool.getType()));
      }

      return obj;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public boolean next() throws SQLException {
    return rs.next();
  }

  public boolean isFirst() throws SQLException {
    return rs.isFirst();
  }

  public <T> T get(String name, Class<T> type) throws SQLException {
    Object value = rs.getObject(name);

    return getWithValue(type, value);
  }

  public <T> T get(int index, Class<T> type) throws SQLException {
    Object value = rs.getObject(index);

    return getWithValue(type, value);
  }

  private final <T> T getWithValue(Class<T> type, Object value) {
    if (value == null) {
      return null;
    } else {
      Class<?> expected = type;

      // expected is exactly what we have
      if (value.getClass().equals(type)) {
        return (T) value;
      }

      // lets delegate task to converter
      return (T) Converters.convert(value, expected);
    }
  }
}
