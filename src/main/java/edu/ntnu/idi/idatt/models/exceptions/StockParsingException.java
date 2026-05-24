package edu.ntnu.idi.idatt.models.exceptions;


public class StockParsingException extends Exception {
  public StockParsingException(String message) {
    super(message);
  }

  public StockParsingException(String message, Throwable cause) {
    super(message, cause);
  }
}
