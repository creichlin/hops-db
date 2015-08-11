package ch.kerbtier.hopsdb.model.annotations;

import java.lang.reflect.Field;

import ch.kerbtier.hopsdb.model.ColumnModel;

public class AnnotationColumnModel<T> implements ColumnModel<T> {
  private Field field;
  private Class<?> type;
  private String name;
  private boolean key;
  
  public AnnotationColumnModel(Field field, boolean key, String name) {
    this.field = field;
    this.key = key;
    this.name = name;
  }

  public void setType(Class<?> type) {
    this.type = type;
  }
  
  public boolean isKey() {
    return key;
  }
  
  @Override
  public <R> R get(T instance, Class<R> pt) {
    if(is(pt)) {
      try {
        return (R)field.get(instance);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    throw new RuntimeException("wrong type");
  }
  
  @Override
  public Object get(T instance) {
    try {
      return field.get(instance);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void set(T instance, Object value) {
    if(value == null || is(value.getClass())) {
      try {
        field.set(instance, value);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else {
      throw new RuntimeException("wrong type: " + value.getClass() + " for " + type);
    }
  }
  
  @Override
  public boolean is(Class<?> t) {
    return type.isAssignableFrom(t);
  }

  @Override
  public String getName() {
    return name;
  }
}
