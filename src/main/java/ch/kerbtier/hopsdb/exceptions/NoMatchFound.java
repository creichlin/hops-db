package ch.kerbtier.hopsdb.exceptions;

public class NoMatchFound extends RuntimeException {
  public NoMatchFound(String desc) {
    super(desc);
  }
}
