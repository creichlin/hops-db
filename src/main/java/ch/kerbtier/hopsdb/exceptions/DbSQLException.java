package ch.kerbtier.hopsdb.exceptions;

import java.sql.SQLException;

public class DbSQLException extends RuntimeException {

  public DbSQLException(SQLException e) {
    super(e);
  }

}
