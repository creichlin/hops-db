package ch.kerbtier.hopsdb.tests.crud.models;

import ch.kerbtier.hopsdb.model.annotations.Column;
import ch.kerbtier.hopsdb.model.annotations.Table;


@Table
public class InheritanceChild extends InheritanceParent {
  @Column
  private String value2;

  public InheritanceChild(String value) {
    super(value);
    this.value2 = value + ":2";
  }

  public InheritanceChild() {

  }

  public String getValue2() {
    return value2;
  }

  public void setValue2(String value2) {
    this.value2 = value2;
  }
}
