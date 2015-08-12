package ch.kerbtier.hopsdb.impl.converters;

import ch.kerbtier.hopsdb.impl.Converter;

public class NumberToInteger implements Converter<Number, Integer> {
  
  @Override
  public Class<Integer> to() {
    return Integer.class;
  }

  @Override
  public Class<Number> from() {
    return Number.class;
  }

  @Override
  public Integer convert(Number subject) {
    return subject.intValue();
  }

}
