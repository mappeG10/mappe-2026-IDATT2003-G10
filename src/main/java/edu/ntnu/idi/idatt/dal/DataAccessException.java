package edu.ntnu.idi.idatt.dal;

public class DataAccessException extends Exception {
  public DataAccessException(String message) {
    super(message);
  }

  public DataAccessException(String message, Throwable cause) {
    super(message, cause);
  }
}
