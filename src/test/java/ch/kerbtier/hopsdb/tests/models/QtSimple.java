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


  @Override
  public int hashCode() {
    return 432423 + id;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    QtSimple other = (QtSimple) obj;
    if (id != other.id)
      return false;
    return true;
  }
}
