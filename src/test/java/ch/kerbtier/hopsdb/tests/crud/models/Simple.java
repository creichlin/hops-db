package ch.kerbtier.hopsdb.tests.crud.models;

import ch.kerbtier.hopsdb.model.annotations.Column;
import ch.kerbtier.hopsdb.model.annotations.Table;


@Table
public class Simple {
  @Column(key = true)
  private int id = -1;
  @Column
  private String value1;
  @Column
  private String value2;
  @Column
  private String value3;
  
  public Simple() {
    
  }
  
  public Simple(String value) {
    value1 = value + ":1";
    value2 = value + ":2";
    value3 = value + ":3";
  }
  

  public int getId() {
    return id;
  }

  public String getValue1() {
    return value1;
  }

  public void setValue1(String value1) {
    this.value1 = value1;
  }

  public String getValue2() {
    return value2;
  }

  public void setValue2(String value2) {
    this.value2 = value2;
  }

  public String getValue3() {
    return value3;
  }

  public void setValue3(String value3) {
    this.value3 = value3;
  }
}
