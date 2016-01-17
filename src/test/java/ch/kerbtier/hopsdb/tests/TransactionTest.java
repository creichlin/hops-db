package ch.kerbtier.hopsdb.tests;

import java.sql.SQLException;

import org.testng.annotations.Test;

import ch.kerbtier.hopsdb.DbRs;
import ch.kerbtier.hopsdb.tests.models.QtSimple;

public class TransactionTest extends TestBase {

  @Test
  public void repeatRead() throws SQLException {
    QtSimple s1 = new QtSimple("AAA");
    db.create(s1);
    DbRs rs = db.select(QtSimple.class).resultSet();
  }

}
