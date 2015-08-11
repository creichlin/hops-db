package ch.kerbtier.hopsdb;

import java.util.List;

public interface Where {
  
  String build(List<Object> parameters);
}

