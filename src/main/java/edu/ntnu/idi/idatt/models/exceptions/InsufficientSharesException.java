package edu.ntnu.idi.idatt.models.exceptions;

public class InsufficientSharesException extends RuntimeException {
  public InsufficientSharesException(String message) {
    super(message);
  }
}
