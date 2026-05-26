package edu.ntnu.idi.idatt.model.exception;

/**
 * Thrown to indicate that a player's portfolio does not hold enough shares to fulfill a sale.
 *
 * <p>This unchecked exception is raised during sale commitment when the portfolio contains fewer
 * shares of the target stock than the quantity requested for the transaction.
 */
public class InsufficientSharesException extends RuntimeException {

  /**
   * Constructs a new {@code InsufficientSharesException} with the specified detail message.
   *
   * @param message a human-readable description of the deficit
   */
  public InsufficientSharesException(String message) {
    super(message);
  }
}
