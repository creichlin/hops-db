package ch.kerbtier.hopsdb.impl.converters;

import java.io.BufferedReader;
import java.sql.Clob;

import ch.kerbtier.hopsdb.impl.Converter;

public class ClobToString implements Converter<Clob, String> {

  @Override
  public Class<Clob> from() {
    return Clob.class;
  }

  @Override
  public Class<String> to() {
    return String.class;
  }

  @Override
  public String convert(Clob from) throws Exception {
    StringBuilder str = new StringBuilder();
    String line;

    BufferedReader bufferRead = new BufferedReader(from.getCharacterStream());

    while ((line = bufferRead.readLine()) != null)
      str.append(line);

    return str.toString();
  }

}
