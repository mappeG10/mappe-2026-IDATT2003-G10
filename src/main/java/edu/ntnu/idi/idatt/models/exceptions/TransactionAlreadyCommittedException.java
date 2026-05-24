package edu.ntnu.idi.idatt.models.exceptions;

public class TransactionAlreadyCommittedException extends RuntimeException {
  public TransactionAlreadyCommittedException(String message) {
    super(message);
  }
}
