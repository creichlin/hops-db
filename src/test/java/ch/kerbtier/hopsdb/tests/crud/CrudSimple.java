package ch.kerbtier.hopsdb.tests.crud;

import java.sql.SQLException;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;
import ch.kerbtier.hopsdb.Db;
import ch.kerbtier.hopsdb.tests.TestBase;
import ch.kerbtier.hopsdb.tests.crud.models.Simple;
import ch.kerbtier.hopsdb.tests.util.Util;

public class CrudSimple extends TestBase implements CrudInterface {

  @Override
  @Test
  public void createInstanceAndSelectById() throws SQLException {
    Simple dt = new Simple("value");
    db.create(dt);
    db.commit();

    Simple dt2 = db.select(Simple.class, dt.getId());

    assertEquals(dt.getValue1(), dt2.getValue1());
    assertEquals(1, db.select(Simple.class).count());
    db.commit();
  }

  @Override
  @Test()
  public void createInstanceAndDelete() throws SQLException {
    Simple dt = new Simple("value");
    db.create(dt);
    db.commit();

    assertEquals(1, db.select(Simple.class).count());

    db.delete(dt);

    assertEquals(0, db.select(Simple.class).count());
    db.commit();
  }

  @Override
  @Test()
  public void createAndUpdateSameObject() throws SQLException {
    Simple dt = new Simple("value");
    db.create(dt);
    db.commit();
    
    dt.setValue1("rhino");
    db.update(dt);
    db.commit();
    assertEquals("rhino", db.select(Simple.class, dt.getId()).getValue1());
    db.commit();
  }

  @Override
  @Test()
  public void createAndUpdateDifferentObject() throws SQLException {
    Simple dt = new Simple("value");
    db.create(dt);
    db.commit();
    
    Simple other = db.select(Simple.class, dt.getId());
    other.setValue1("rhino");
    db.update(other);
    db.commit();
    assertEquals("rhino", db.select(Simple.class, dt.getId()).getValue1());
    db.commit();
  }

  @Override
  @Test()
  public void createAndSelectAll() throws SQLException {
    db.create(new Simple("valueA"));
    db.create(new Simple("valueB"));
    db.create(new Simple("valueC"));
    db.create(new Simple("valueD"));
    db.commit();
    
    List<Simple> all = db.select(Simple.class).listAll();
    assertEquals(4, all.size());
    db.commit();
  }
}
