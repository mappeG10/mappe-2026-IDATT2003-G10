package edu.ntnu.idi.idatt.dal.exception;

public class StockParsingException extends DataAccessException {
  public StockParsingException(String message) {
    super(message);
  }

  public StockParsingException(String message, Throwable cause) {
    super(message, cause);
  }
}
