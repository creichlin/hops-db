package ch.kerbtier.hopsdb.tests;

import java.sql.SQLException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import ch.kerbtier.hopsdb.Db;
import ch.kerbtier.hopsdb.tests.util.Util;

public class TestBase {
  protected Db db;
  
  @BeforeMethod
  public void setup() throws SQLException {
    db = Util.create(this);
  }

  @AfterMethod
  public void tearDown() {
    db.destroy();
  }
}
