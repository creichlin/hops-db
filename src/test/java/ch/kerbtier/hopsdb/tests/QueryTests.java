package ch.kerbtier.hopsdb.tests;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;
import ch.kerbtier.hopsdb.exceptions.InvalidQueryException;
import ch.kerbtier.hopsdb.tests.models.QtSimple;

public class QueryTests extends TestBase {

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
  public void selectAllQuery() throws SQLException {
    List<QtSimple> all = db.select(QtSimple.class).listAll();
    
    assertEquals(all.size(), 5);
  }
  
  @Test
  public void selectFirstQuery() throws SQLException {
    QtSimple first = db.select(QtSimple.class).first();
    
    assertNotNull(first);
  }
  
  @Test
  public void selectFirstByPK() throws SQLException {
    QtSimple first = db.select(QtSimple.class).byPk(i1.getId()).first();
    
    assertNotNull(first);
  }
  
  @Test(expectedExceptions = {InvalidQueryException.class})
  public void selectFirstByPKAndWhere() throws SQLException {
    db.select(QtSimple.class).byPk(i1.getId()).where("id = ?", i1.getId()).first();
  }
  
  @Test(expectedExceptions = {InvalidQueryException.class})
  public void selectFirstByPKAndOrderBy() throws SQLException {
    db.select(QtSimple.class).byPk(i1.getId()).orderBy("id").first();
  }
  
  @Test(expectedExceptions = {InvalidQueryException.class})
  public void selectFirstByWhereAndPk() throws SQLException {
    db.select(QtSimple.class).where("id = ?", i1.getId()).byPk(i1.getId()).first();
  }
  
  @Test
  public void selectByWhere() throws SQLException {
    List<QtSimple> all = db.select(QtSimple.class).where("name = ? or name = ?", "i2", "i4").listAll();
    assertEquals(all.size(), 2);
  }
  
  @Test
  public void countByWhere() throws SQLException {
    int all = db.select(QtSimple.class).where("name = ? or name = ?", "i2", "i4").count();
    assertEquals(all, 2);
  }
  
  @Test
  public void orderBy() throws SQLException {
    List<QtSimple> l = db.select(QtSimple.class).orderBy("name").listAll();
    assertEquals(l, Arrays.asList(i1, i2, i3, i4, i5));
  }
  
  @Test
  public void orderByDesc() throws SQLException {
    List<QtSimple> l = db.select(QtSimple.class).orderBy("name desc").listAll();
    assertEquals(l, Arrays.asList(i5, i4, i3, i2, i1));
  }
  
  public void orderByFirst() throws SQLException {
    QtSimple f = db.select(QtSimple.class).orderBy("name desc").first();
    assertEquals(f, i5);
  }
  
  @Test(expectedExceptions = {InvalidQueryException.class})
  public void orderByPK() throws SQLException {
    List<QtSimple> l = db.select(QtSimple.class).byPk(i1.getId()).orderBy("name desc").listAll();
    assertEquals(l, Arrays.asList(i5, i4, i3, i2, i1));
  }
  
}
