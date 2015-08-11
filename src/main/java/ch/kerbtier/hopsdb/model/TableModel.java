package ch.kerbtier.hopsdb.model;


public interface TableModel<T> extends Iterable<ColumnModel<T>> {

  String getName();

  int keysCount();

  Iterable<ColumnModel<T>> keys();

  Iterable<ColumnModel<T>> columns();

  ColumnModel<T> getColumn(String field);

}
