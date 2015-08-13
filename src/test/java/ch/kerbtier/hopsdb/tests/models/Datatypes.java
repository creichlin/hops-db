package ch.kerbtier.hopsdb.tests.models;

import java.util.Date;

import ch.kerbtier.hopsdb.model.annotations.Column;
import ch.kerbtier.hopsdb.model.annotations.Table;

@Table
public class Datatypes {

  @Column(key = true)
  private int id;

  @Column
  private Date date;

  @Column
  private Date time;

  @Column
  private Date dateTime;

  @Column
  private Integer intInt;

  @Column
  private Integer intSmall;

  @Column
  private long longBig;

  @Column
  private long longInt;

  @Column
  private long longSmall;

  @Column
  private float floatReal;

  @Column
  private double doubleDouble;
  
  @Column
  private byte[] byteBlob;

  @Column
  private boolean booleanBoolean;

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public Integer getIntInt() {
    return intInt;
  }

  public Integer getIntSmall() {
    return intSmall;
  }

  public void setIntSmall(Integer intSmall) {
    this.intSmall = intSmall;
  }

  public long getLongBig() {
    return longBig;
  }

  public void setLongBig(long longBig) {
    this.longBig = longBig;
  }

  public long getLongInt() {
    return longInt;
  }

  public void setLongInt(long longInt) {
    this.longInt = longInt;
  }

  public long getLongSmall() {
    return longSmall;
  }

  public void setLongSmall(long longSmall) {
    this.longSmall = longSmall;
  }

  public float getFloatReal() {
    return floatReal;
  }

  public void setFloatReal(float floatReal) {
    this.floatReal = floatReal;
  }

  public double getDoubleDouble() {
    return doubleDouble;
  }

  public void setDoubleDouble(double doubleDouble) {
    this.doubleDouble = doubleDouble;
  }

  public void setIntInt(Integer intInt) {
    this.intInt = intInt;
  }

  public byte[] getByteBlob() {
    return byteBlob;
  }

  public void setByteBlob(byte[] byteBlob) {
    this.byteBlob = byteBlob;
  }

  public boolean isBooleanBoolean() {
    return booleanBoolean;
  }

  public void setBooleanBoolean(boolean booleanBoolean) {
    this.booleanBoolean = booleanBoolean;
  }
}
