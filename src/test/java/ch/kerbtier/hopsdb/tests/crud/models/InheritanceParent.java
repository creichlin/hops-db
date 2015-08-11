package ch.kerbtier.hopsdb.tests.crud.models;

import ch.kerbtier.hopsdb.model.annotations.Column;
import ch.kerbtier.hopsdb.model.annotations.Table;


@Table
public class InheritanceParent {

  @Column(key = true)
  private int identifier = -1;

  @Column
  private String value1;


  public InheritanceParent(String value) {
    this.value1 = value + ":1";
  }

  public InheritanceParent() {

  }

  public String getValue1() {
    return value1;
  }

  public void setValue1(String value1) {
    this.value1 = value1;
  }

  public int getIdentifier() {
    return identifier;
  }

}
