package edu.ntnu.idi.idatt.models.exceptions;

public class StockNotFoundException extends RuntimeException {
  public StockNotFoundException(String message) {
    super(message);
  }
}
