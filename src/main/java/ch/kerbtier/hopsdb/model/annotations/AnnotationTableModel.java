package ch.kerbtier.hopsdb.model.annotations;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
          if (String.class.isAssignableFrom(field.getType())) {
            cm.setType(String.class);
          } else if (Integer.TYPE.isAssignableFrom(field.getType()) || Integer.class.isAssignableFrom(field.getType())) {
            cm.setType(Integer.class);
          } else if (Long.TYPE.isAssignableFrom(field.getType()) || Long.class.isAssignableFrom(field.getType())) {
            cm.setType(Long.class);
          } else if (Date.class.isAssignableFrom(field.getType())) {
            cm.setType(Date.class);
          } else if (BigDecimal.class.isAssignableFrom(field.getType())) {
            cm.setType(BigDecimal.class);
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
