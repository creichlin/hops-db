package ch.kerbtier.hopsdb.model;

public interface ColumnModel<T> {

  String getName();
  void set(T instance, Object value);
  boolean is(Class<?> t);
  <R> R get(T instance, Class<R> pt);
}
