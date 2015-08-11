package ch.kerbtier.hopsdb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ch.kerbtier.hopsdb.exceptions.InvalidQueryException;
import ch.kerbtier.hopsdb.exceptions.NoMatchFound;
import ch.kerbtier.hopsdb.exceptions.WrongNumberOfKeys;
import ch.kerbtier.hopsdb.impl.QueryWhere;
import ch.kerbtier.hopsdb.model.ColumnModel;
import ch.kerbtier.hopsdb.model.TableModel;

public class Query<T> {
  private Db db;
  private TableModel<T> select;
  
  private Long pk = null;
  private Where where = null;
  
  public Query(Db db, TableModel<T> select) {
    this.select = select;
    this.db = db;
  }
  
  public String createQuery(Type type, List<Object> parameters) {
    String query = "";
    
    
    if(type == Type.SELECT || type == Type.SELECT_FIRST) {
      query += "select * from `" + select.getName() + "`";
    } else if (type == Type.COUNT) {
      query += "select count(*) from `" + select.getName() + "`";
    }
    
    if(pk != null) {
      query += " where ";
      
      for(ColumnModel<T> c : select.keys()) {
        query += "`" + c.getName() + "` = ?, ";
        parameters.add(pk);
      }
      
      query = query.substring(0, query.length() - 2);
    } else if(where != null) {
      query += " where " + where.build(parameters);
    }
    
    if(type == Type.SELECT_FIRST) {
      query += " limit 0, 1";
    }
    
    return query;
  }

  public List<T> listAll() throws SQLException {
    List<Object> parameters = new ArrayList<>();
    DbPs ps = db.prepareStatement(createQuery(Type.SELECT, parameters));
    ps.setParameters(parameters);
    DbRs rs = ps.executeQuery();

    List<T> result = new ArrayList<>();
    while (rs.next()) {
      result.add(rs.populate(select.getType()));
    }
    
    return result;
  }

  public T first() throws SQLException {
    List<Object> parameters = new ArrayList<>();
    DbPs ps = db.prepareStatement(createQuery(Type.SELECT_FIRST, parameters));
    ps.setParameters(parameters);
    DbRs rs = ps.executeQuery();

    if (rs.next()) {
      return rs.populate(select.getType());
    } else {
      throw new NoMatchFound("");
    }
  }

  public int count() throws SQLException {
    List<Object> parameters = new ArrayList<>();
    DbPs ps = db.prepareStatement(createQuery(Type.COUNT, parameters));
    ps.setParameters(parameters);
    DbRs rs = ps.executeQuery();

    rs.next();
    return rs.getInt(1);
  }

  public Query<T> byPk(long pk_) {
    if(select.keysCount() != 1) {
      throw new WrongNumberOfKeys("model must have exactly one key to select by id but has " + select.keysCount());
    }
    if(where != null) {
      throw new InvalidQueryException("cannot select by pk if where exists");
    }
    this.pk = pk_;
    return this;
  }

  public Query<T> where(String whereQuery, Object... parameters) {
    if(pk != null) {
      throw new InvalidQueryException("cannot select by where if pk exists");
    }
    where = new QueryWhere(whereQuery, parameters);
    return this;
  }
}

enum Type {
  SELECT_FIRST, SELECT, COUNT
}
