package ch.kerbtier.hopsdb.tests;

import java.sql.SQLException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;
import ch.kerbtier.hopsdb.DbRs;
import ch.kerbtier.hopsdb.exceptions.InvalidCursorException;
import ch.kerbtier.hopsdb.tests.models.QtSimple;

public class ResultSetTests extends TestBase {

  private QtSimple i1;
  private QtSimple i2;
  private QtSimple i3;
  private QtSimple i4;
  private QtSimple i5;

  @Override
  @BeforeMethod
  public void setup() throws SQLException {
    super.setup();
    
    i1 = new QtSimple("i1");
    i2 = new QtSimple("i2");
    i3 = new QtSimple("i3");
    i5 = new QtSimple("i5");
    i4 = new QtSimple("i4");
    
    db.create(i1);
    db.create(i2);
    db.create(i3);
    db.create(i5);
    db.create(i4);
    
    db.commit();
  }
  
  @Test
  public void selectMany() throws SQLException {
    DbRs rs = db.select(QtSimple.class).resultSet();
    
    assertEquals(rs.next(), true);
    assertEquals(rs.next(), true);
    assertEquals(rs.next(), true);
    assertEquals(rs.next(), true);
    assertEquals(rs.next(), true);
    assertEquals(rs.next(), false);
  }
  
  
  @Test
  public void getValue() throws SQLException {
    DbRs rs = db.select(QtSimple.class).byPk(i1.getId()).resultSet();
    rs.next();
    String value = rs.get("name", String.class);
    assertEquals(value, "i1");
  }
  
  @Test(expectedExceptions = {InvalidCursorException.class})
  public void testInvalidCursorBeforeFirst() throws SQLException {
    DbRs rs = db.select(QtSimple.class).byPk(i1.getId()).resultSet();
    rs.get("name", String.class);
  }
  
  @Test(expectedExceptions = {InvalidCursorException.class})
  public void testInvalidCursorAfterLast() throws SQLException {
    DbRs rs = db.select(QtSimple.class).byPk(i1.getId()).resultSet();
    rs.next();
    try {
      rs.get("name", String.class);
    } catch(Exception e) {
      throw new AssertionError();
    }
    
    rs.next();
    rs.get("name", String.class);
  }
  
}
