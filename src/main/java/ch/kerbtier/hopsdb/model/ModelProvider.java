package ch.kerbtier.hopsdb.model;

public interface ModelProvider {
  <T extends Object> TableModel<T> getModel(T obj);
  <T extends Object> TableModel<T> getModel(Class<T> cls);
}
