package ch.kerbtier.hopsdb.exceptions;

public class WrongNumberOfKeys extends RuntimeException {
  public WrongNumberOfKeys(String desc) {
    super(desc);
  }
}
