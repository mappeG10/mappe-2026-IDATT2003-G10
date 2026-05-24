package edu.ntnu.idi.idatt.model.exceptions;

public class TransactionAlreadyCommittedException extends RuntimeException {
  public TransactionAlreadyCommittedException(String message) {
    super(message);
  }
}
