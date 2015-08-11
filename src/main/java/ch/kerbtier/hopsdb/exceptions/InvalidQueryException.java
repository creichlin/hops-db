package ch.kerbtier.hopsdb.exceptions;

public class InvalidQueryException extends RuntimeException {

  public InvalidQueryException(String name) {
    super(name);
  }

}
