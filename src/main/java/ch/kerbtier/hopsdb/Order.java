package ch.kerbtier.hopsdb;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

public class Order {
  
  private List<String> orders = new ArrayList<>();
  
  public void add(String order) {
    orders.add(order);
  }
  
  public String asSql() {
    return " order by " + Joiner.on(", ").join(orders);
  }

}
