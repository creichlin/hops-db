package ch.kerbtier.hopsdb.impl;

import java.util.List;

import ch.kerbtier.hopsdb.Where;

public class QueryWhere implements Where {
  private String query;
  private Object[] parameters;
  
  public QueryWhere(String query, Object[] parameters) {
    this.query = query;
    this.parameters = parameters;
  }

  @Override
  public String build(List<Object> params) {
    for(Object o: parameters) {
      params.add(o);
    }
    return query;
  }

}
