package edu.ntnu.idi.idatt.model.exception;

/**
 * Thrown when an attempt is made to commit a transaction that has already been committed.
 *
 * <p>Each {@link edu.ntnu.idi.idatt.model.transaction.Transaction} is single-use; calling
 * {@code commit} a second time on the same instance violates the transaction contract and
 * triggers this exception.</p>
 */
public class TransactionAlreadyCommittedException extends RuntimeException {

  /**
   * Constructs a new {@code TransactionAlreadyCommittedException} with the specified detail
   * message.
   *
   * @param message a description of the duplicate-commit attempt
   */
  public TransactionAlreadyCommittedException(String message) {
    super(message);
  }
}
