package ch.kerbtier.hopsdb.tests.crud.models;

import ch.kerbtier.hopsdb.model.annotations.Column;
import ch.kerbtier.hopsdb.model.annotations.Table;


@Table
public class CompositeKey {
  @Column(key = true)
  private int id1 = -1;
  @Column(key = true)
  private int id2 = -1;
  @Column
  private String value1;
  @Column
  private String value2;
  @Column
  private String value3;
  
  public CompositeKey() {
    
  }
  
  public CompositeKey(int id1, String value) {
    this.id1 = id1;
    value1 = value + ":1";
    value2 = value + ":2";
    value3 = value + ":3";
  }
  

  public int getId1() {
    return id1;
  }

  public int getId2() {
    return id2;
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
