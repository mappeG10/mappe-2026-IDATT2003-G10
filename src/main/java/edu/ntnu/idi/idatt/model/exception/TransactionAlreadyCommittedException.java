package edu.ntnu.idi.idatt.model.exception;

public class TransactionAlreadyCommittedException extends RuntimeException {
  public TransactionAlreadyCommittedException(String message) {
    super(message);
  }
}
