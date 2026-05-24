package edu.ntnu.idi.idatt.model.exception;

public class StockNotFoundException extends RuntimeException {
  public StockNotFoundException(String message) {
    super(message);
  }
}
