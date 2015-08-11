package ch.kerbtier.hopsdb;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

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
      for(ColumnModel<T> cool: model) {
        if(cool.is(String.class)) {
          cool.set(obj, getString(cool.getName()));
        } else if(cool.is(Integer.class)) {
          cool.set(obj, getInt(cool.getName()));
        } else if(cool.is(Long.class)) {
          cool.set(obj, getLong(cool.getName()));
        } else if(cool.is(Date.class)) {
          cool.set(obj, getDateTime(cool.getName()));
        } else if(cool.is(BigDecimal.class)) {
          cool.set(obj, getBigDecimal(cool.getName()));
        }
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

  public String getString(String name) throws SQLException {
    return rs.getString(name);
  }

  public Long getLong(int i) throws SQLException {
    long l = rs.getLong(i);
    if (rs.wasNull()) {
      return null;
    }
    return l;
  }

  public Integer getInt(String name) throws SQLException {
    int ii = rs.getInt(name);
    if (rs.wasNull()) {
      return null;
    }
    return ii;
  }

  public Date getDate(String name) throws SQLException {
    return rs.getDate(name);
  }

  public Date getDateTime(String name) throws SQLException {
    return rs.getTimestamp(name);
  }

  public Long getLong(String name) throws SQLException {
    long l = rs.getLong(name);
    if (rs.wasNull()) {
      return null;
    }
    return l;
  }

  public boolean getBoolean(String name) throws SQLException {
    return rs.getBoolean(name);
  }

  public Integer getInt(int i) throws SQLException {
    int ii = rs.getInt(i);
    if (rs.wasNull()) {
      return null;
    }
    return ii;
  }

  public BigDecimal getBigDecimal(String name) throws SQLException {
    return rs.getBigDecimal(name);
  }
}
