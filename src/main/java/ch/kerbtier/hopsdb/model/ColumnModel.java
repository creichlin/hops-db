package ch.kerbtier.hopsdb.model;

public interface ColumnModel<T> {

  String getName();
  void set(T instance, Object value);
  <R> R get(T instance, Class<R> pt);
  Object get(T instance);

  boolean is(Class<?> type);
  Class<?> getType();
}
