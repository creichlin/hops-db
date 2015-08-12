package ch.kerbtier.hopsdb.model.annotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.kerbtier.hopsdb.exceptions.NoSuchColumn;
import ch.kerbtier.hopsdb.model.ColumnModel;
import ch.kerbtier.hopsdb.model.TableModel;

public class AnnotationTableModel<T> implements TableModel<T>{
  private List<AnnotationColumnModel<T>> all = new ArrayList<>();
  private List<AnnotationColumnModel<T>> keys = new ArrayList<>();
  private List<AnnotationColumnModel<T>> columns = new ArrayList<>();
  private Map<String, AnnotationColumnModel<T>> allByName = new HashMap<>();
  private String name;
  private Class<T> type;
  
  public AnnotationTableModel(Class<T> type) {
    name = AnnotationModelProvider.getTableName(type);
    this.type = type;
    Class<?> current = type;
    
    while (current != null) {
      for (Field field : current.getDeclaredFields()) {
        Column a = field.getAnnotation(Column.class);
        if (a != null) {
          field.setAccessible(true);
          AnnotationColumnModel<T> cm = new AnnotationColumnModel<>(field, a.key(), AnnotationModelProvider.getColumnName(field));
          if (field.getType().equals(Integer.TYPE)) {
            cm.setType(Integer.class);
          } else if (field.getType().equals(Long.TYPE)) {
            cm.setType(Long.class);
          } else if (field.getType().equals(Short.TYPE)) {
            cm.setType(Short.class);
          } else if (field.getType().equals(Boolean.TYPE)) {
            cm.setType(Boolean.class);
          } else if (field.getType().equals(Float.TYPE)) {
            cm.setType(Float.class);
          } else if (field.getType().equals(Double.TYPE)) {
            cm.setType(Double.class);
          } else {
            cm.setType(field.getType());
          }
          
          all.add(cm);
          allByName.put(cm.getName(), cm);
          if(a.key()) {
            keys.add(cm);
          } else {
            columns.add(cm);
          }
        }
      }
      current = current.getSuperclass();
    }
  }

  @Override
  public Iterator<ColumnModel<T>> iterator() {
    return (Iterator<ColumnModel<T>>)(Object)all.iterator();
  }

  public String getName() {
    return name;
  }

  public Iterable<ColumnModel<T>> columns() {
    return (Iterable<ColumnModel<T>>)(Object)columns;
  }

  public Iterable<ColumnModel<T>> keys() {
    return (Iterable<ColumnModel<T>>)(Object)keys;
  }

  @Override
  public int keysCount() {
    return keys.size();
  }

  public int columnsCount() {
    return columns.size();
  }

  @Override
  public AnnotationColumnModel<T> getColumn(String field) {
    AnnotationColumnModel<T> column = allByName.get(field);
    if(column == null) {
      throw new NoSuchColumn(field);
    }
    return column;
  }

  @Override
  public Class<T> getType() {
    return type;
  }
}
