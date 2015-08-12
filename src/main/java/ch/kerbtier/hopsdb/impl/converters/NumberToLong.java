package ch.kerbtier.hopsdb.impl.converters;

import ch.kerbtier.hopsdb.impl.Converter;

public class NumberToLong implements Converter<Number, Long> {
  
  @Override
  public Class<Number> from() {
    return Number.class;
  }

  @Override
  public Class<Long> to() {
    return Long.class;
  }

  @Override
  public Long convert(Number subject) {
    return subject.longValue();
  }

}
