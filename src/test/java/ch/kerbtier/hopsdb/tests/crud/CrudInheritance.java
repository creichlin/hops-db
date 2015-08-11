package ch.kerbtier.hopsdb.tests.crud;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;
import ch.kerbtier.hopsdb.Db;
import ch.kerbtier.hopsdb.tests.TestBase;
import ch.kerbtier.hopsdb.tests.crud.models.InheritanceChild;
import ch.kerbtier.hopsdb.tests.util.Util;

public class CrudInheritance extends TestBase implements CrudInterface {

  @Override
  @Test()
  public void createInstanceAndSelectById() throws SQLException {
    InheritanceChild dt = new InheritanceChild("value");
    db.create(dt);
    db.commit();

    InheritanceChild dt2 = db.select(InheritanceChild.class, dt.getIdentifier());

    assertEquals(dt.getValue1(), dt2.getValue1());
    assertEquals(1, db.select(InheritanceChild.class).count());
    db.commit();
  }

  @Override
  @Test
  public void createInstanceAndDelete() throws SQLException {
    InheritanceChild dt = new InheritanceChild("value");
    db.create(dt);
    db.commit();

    assertEquals(1, db.select(InheritanceChild.class).count());

    db.delete(dt);

    assertEquals(0, db.select(InheritanceChild.class).count());
    db.commit();
  }

  @Override
  @Test
  public void createAndUpdateSameObject() throws SQLException {
    InheritanceChild dt = new InheritanceChild("value");
    db.create(dt);
    db.commit();
    
    dt.setValue1("rhino");
    db.update(dt);
    db.commit();
    assertEquals("rhino", db.select(InheritanceChild.class, dt.getIdentifier()).getValue1());
    db.commit();
  }

  @Override
  @Test
  public void createAndUpdateDifferentObject() throws SQLException {
    InheritanceChild dt = new InheritanceChild("value");
    db.create(dt);
    db.commit();
    
    InheritanceChild other = db.select(InheritanceChild.class, dt.getIdentifier());
    other.setValue1("rhino");
    db.update(other);
    db.commit();
    assertEquals("rhino", db.select(InheritanceChild.class, dt.getIdentifier()).getValue1());
    db.commit();
  }

  @Override
  @Test
  public void createAndSelectAll() throws SQLException {
    db.create(new InheritanceChild("valueA"));
    db.create(new InheritanceChild("valueB"));
    db.create(new InheritanceChild("valueC"));
    db.create(new InheritanceChild("valueD"));
    db.commit();
    
    List<InheritanceChild> all = db.select(InheritanceChild.class).listAll();
    assertEquals(4, all.size());
    db.commit();
  }
}
