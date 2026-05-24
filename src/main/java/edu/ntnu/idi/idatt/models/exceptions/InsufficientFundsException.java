package edu.ntnu.idi.idatt.models.exceptions;

public class InsufficientFundsException extends RuntimeException {
  public InsufficientFundsException(String message) {
    super(message);
  }
}
