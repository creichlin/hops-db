package ch.kerbtier.hopsdb.tests.crud;

import java.sql.SQLException;

public interface CrudInterface {
  
  void createInstanceAndSelectById() throws SQLException;
  void createInstanceAndDelete() throws SQLException;
  void createAndUpdateSameObject() throws SQLException;
  void createAndUpdateDifferentObject() throws SQLException;
  void createAndSelectAll() throws SQLException;
  
}
