package ch.kerbtier.hopsdb.impl.converters;

import java.sql.Time;
import java.util.Date;

import ch.kerbtier.hopsdb.impl.Converter;

public class SqlTimeToDate implements Converter<Time, Date> {
  
  @Override
  public Class<Date> to() {
    return Date.class;
  }

  @Override
  public Class<Time> from() {
    return Time.class;
  }

  @Override
  public Date convert(Time subject) {
    return new Date(subject.getTime());
  }

}
