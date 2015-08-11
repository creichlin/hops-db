package ch.kerbtier.hopsdb.tests.crud;

import java.sql.SQLException;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;
import ch.kerbtier.hopsdb.Db;
import ch.kerbtier.hopsdb.exceptions.WrongNumberOfKeys;
import ch.kerbtier.hopsdb.tests.crud.models.CompositeKey;
import ch.kerbtier.hopsdb.tests.util.Util;

public class CrudCompositeKey implements CrudInterface {

  private Db db;

  @BeforeMethod
  public void setup() {
    db = Util.create(this);
  }

  @Test
  public void createAndGetByWhere() throws SQLException {
    CompositeKey dt = new CompositeKey(1, "value");
    db.create(dt, "id1");

    CompositeKey dt2 = new CompositeKey(1, "value2");
    db.create(dt2, "id1");
    db.commit();

    List<CompositeKey> dtr = db.select(CompositeKey.class, "id1 = ? and id2 = ?", 1, dt.getId2());

    assertEquals(1, dtr.size());
    assertEquals("value:1", dtr.get(0).getValue1());
  }

  @Override
  @Test(expectedExceptions = { WrongNumberOfKeys.class })
  public void createInstanceAndSelectById() throws SQLException {
    CompositeKey dt = new CompositeKey(1, "value");
    db.create(dt, "id1");
    db.commit();

    try {
      db.select(CompositeKey.class, dt.getId1());
    } finally {
      db.commit();
    }
  }

  @Override
  @Test
  public void createInstanceAndDelete() throws SQLException {
    CompositeKey dt = new CompositeKey(1, "value");
    db.create(dt, "id1");
    db.commit();

    assertEquals(1, db.count(CompositeKey.class));

    db.delete(dt);

    assertEquals(0, db.count(CompositeKey.class));
    db.commit();
  }

  @Override
  @Test
  public void createAndUpdateSameObject() throws SQLException {
    CompositeKey dt = new CompositeKey(1, "value");
    db.create(dt, "id1");
    db.commit();

    dt.setValue1("rhino");
    db.update(dt);
    db.commit();
    assertEquals("rhino", db.selectFirst(CompositeKey.class, "id1 = ? AND id2 = ?", dt.getId1(), dt.getId2())
        .getValue1());
    db.commit();
  }

  @Override
  @Test
  public void createAndUpdateDifferentObject() throws SQLException {
    CompositeKey dt = new CompositeKey(1, "value");
    db.create(dt, "id1");
    db.commit();

    CompositeKey other = db.selectFirst(CompositeKey.class, "id1 = ? AND id2 = ?", dt.getId1(), dt.getId2());
    other.setValue1("rhino");
    db.update(other);
    db.commit();
    assertEquals("rhino", db.selectFirst(CompositeKey.class, "id1 = ? AND id2 = ?", dt.getId1(), dt.getId2())
        .getValue1());
    db.commit();
  }

  @Override
  @Test
  public void createAndSelectAll() throws SQLException {
    db.create(new CompositeKey(1, "valueA"), "id1");
    db.create(new CompositeKey(1, "valueB"), "id1");
    db.create(new CompositeKey(1, "valueC"), "id1");
    db.create(new CompositeKey(1, "valueD"), "id1");
    db.commit();

    List<CompositeKey> all = db.select(CompositeKey.class);
    assertEquals(4, all.size());
    db.commit();
  }

}
