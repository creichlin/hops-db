package ch.kerbtier.hopsdb.exceptions;

public class InvalidCursorException extends RuntimeException {

  public InvalidCursorException(String name) {
    super(name);
  }

}
