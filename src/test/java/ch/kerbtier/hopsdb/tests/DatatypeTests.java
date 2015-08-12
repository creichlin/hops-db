package ch.kerbtier.hopsdb.tests;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

import ch.kerbtier.hopsdb.tests.models.Datatypes;

public class DatatypeTests extends TestBase {

  private DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
  Datatypes entity;

  private void saveAndReload() throws SQLException {
    db.update(entity);
    db.commit();
    entity = db.select(Datatypes.class).first();
  }

  @Override
  @BeforeMethod
  public void setup() throws SQLException {
    super.setup();
    entity = new Datatypes();
    db.create(entity);
    db.commit();
  }

  @Test
  public void dateTimeWriteAndRead() throws ParseException, SQLException {
    entity.setDateTime(format.parse("06/10/2023 08:45"));
    saveAndReload();
    assertEquals(entity.getDateTime(), format.parse("06/10/2023 08:45"));
  }

  @Test
  public void dateWriteAndRead() throws ParseException, SQLException {
    entity.setDate(format.parse("06/10/2023 08:45"));
    saveAndReload();
    assertEquals(entity.getDate(), format.parse("06/10/2023 00:00"));
  }

  @Test
  public void timeWriteAndRead() throws ParseException, SQLException {
    entity.setTime(format.parse("06/10/2023 08:45"));
    saveAndReload();
    assertEquals(entity.getTime(), format.parse("01/01/1970 08:45"));
  }

  @Test
  public void testIntegerMax() throws SQLException {
    entity.setIntInt(Integer.MAX_VALUE);
    saveAndReload();
    assertEquals(entity.getIntInt(), (Integer)Integer.MAX_VALUE);
  }

  @Test
  public void testIntegerMin() throws SQLException {
    entity.setIntInt(Integer.MIN_VALUE);
    saveAndReload();
    assertEquals(entity.getIntInt(), (Integer)Integer.MIN_VALUE);
  }

  @Test
  public void testSmallIntMax() throws SQLException {
    entity.setIntSmall((int)Short.MAX_VALUE);
    saveAndReload();
    assertEquals(entity.getIntSmall(), (Integer)(int)Short.MAX_VALUE);
  }

  @Test
  public void testSmallIntMin() throws SQLException {
    entity.setIntSmall((int)Short.MIN_VALUE);
    saveAndReload();
    assertEquals(entity.getIntSmall(), (Integer)(int)Short.MIN_VALUE);
  }

  @Test
  public void testLongMax() throws SQLException {
    entity.setLongBig(Long.MAX_VALUE);
    saveAndReload();
    assertEquals(entity.getLongBig(), Long.MAX_VALUE);
  }

  @Test
  public void testLongMin() throws SQLException {
    entity.setLongBig(Long.MIN_VALUE);
    saveAndReload();
    assertEquals(entity.getLongBig(), Long.MIN_VALUE);
  }

  @Test
  public void testLongIntMax() throws SQLException {
    entity.setLongInt(Integer.MAX_VALUE);
    saveAndReload();
    assertEquals(entity.getLongInt(), Integer.MAX_VALUE);
  }

  @Test
  public void testLongIntMin() throws SQLException {
    entity.setLongInt(Integer.MIN_VALUE);
    saveAndReload();
    assertEquals(entity.getLongInt(), Integer.MIN_VALUE);
  }

  @Test
  public void testLongSmallMax() throws SQLException {
    entity.setLongSmall(Short.MAX_VALUE);
    saveAndReload();
    assertEquals(entity.getLongSmall(), Short.MAX_VALUE);
  }

  @Test
  public void testLongSmallMin() throws SQLException {
    entity.setLongSmall(Short.MIN_VALUE);
    saveAndReload();
    assertEquals(entity.getLongSmall(), Short.MIN_VALUE);
  }

  @Test
  public void testFloat() throws SQLException {
    entity.setFloatReal(1.23456f);
    saveAndReload();
    assertEquals(entity.getFloatReal(), 1.23456f);
  }

  @Test
  public void testDouble() throws SQLException {
    entity.setDoubleDouble(Math.PI);
    saveAndReload();
    assertEquals(entity.getDoubleDouble(), Math.PI);
  }

}
