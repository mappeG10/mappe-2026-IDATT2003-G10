package edu.ntnu.idi.idatt.model.exceptions;

public class StockNotFoundException extends RuntimeException {
  public StockNotFoundException(String message) {
    super(message);
  }
}
