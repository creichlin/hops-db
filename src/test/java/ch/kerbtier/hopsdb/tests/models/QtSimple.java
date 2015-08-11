package ch.kerbtier.hopsdb.tests.models;

import ch.kerbtier.hopsdb.model.annotations.Column;
import ch.kerbtier.hopsdb.model.annotations.Table;

@Table
public class QtSimple {
  @Column(key = true)
  private int id = -1;
  
  @Column
  private String name;

  public QtSimple() {
    
  }
  
  public QtSimple(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getId() {
    return id;
  }
}
