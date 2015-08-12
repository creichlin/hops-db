package ch.kerbtier.hopsdb.impl;

public interface  Converter<FROM extends Object, TO extends Object> {
  Class<FROM> from();
  Class<TO> to();
  
  TO convert(FROM from) throws Exception;
}
