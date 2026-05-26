package edu.ntnu.idi.idatt.model.exception;

/**
 * Thrown to indicate that a player does not have sufficient funds to complete a purchase.
 *
 * <p>This unchecked exception is raised during transaction commitment when the player's
 * available balance is lower than the total cost of the requested transaction.</p>
 */
public class InsufficientFundsException extends RuntimeException {

  /**
   * Constructs a new {@code InsufficientFundsException} with the specified detail message.
   *
   * @param message a human-readable description of the shortfall, typically including
   *                the required and available amounts
   */
  public InsufficientFundsException(String message) {
    super(message);
  }
}
