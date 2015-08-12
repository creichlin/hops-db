package ch.kerbtier.hopsdb.impl.converters;

import java.sql.Timestamp;
import java.util.Date;

import ch.kerbtier.hopsdb.impl.Converter;

public class DateTime implements Converter<Timestamp, Date> {
  
  @Override
  public Class<Date> to() {
    return Date.class;
  }

  @Override
  public Class<Timestamp> from() {
    return Timestamp.class;
  }

  @Override
  public Date convert(Timestamp subject) {
    return new Date(subject.getTime());
  }

}
