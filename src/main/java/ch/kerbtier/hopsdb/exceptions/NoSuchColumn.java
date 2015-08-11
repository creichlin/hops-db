package ch.kerbtier.hopsdb.exceptions;

public class NoSuchColumn extends RuntimeException {
  public NoSuchColumn(String desc) {
    super(desc);
  }
}
