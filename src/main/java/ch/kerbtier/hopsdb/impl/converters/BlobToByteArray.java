package ch.kerbtier.hopsdb.impl.converters;

import java.sql.Blob;

import ch.kerbtier.hopsdb.impl.Converter;

public class BlobToByteArray implements Converter<Blob, byte[]> {

  @Override
  public Class<Blob> from() {
    return Blob.class;
  }

  @Override
  public Class<byte[]> to() {
    return byte[].class;
  }

  @Override
  public byte[] convert(Blob from) throws Exception {
    if(from.length() > Integer.MAX_VALUE) {
      throw new RuntimeException("cannot read blobs larger than " + Integer.MAX_VALUE);
    }
    return from.getBytes(1, (int)from.length());
  }

}
