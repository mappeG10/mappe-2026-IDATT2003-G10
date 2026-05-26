package edu.ntnu.idi.idatt.model.exception;

/**
 * Thrown when a requested stock symbol cannot be located in the exchange.
 *
 * <p>This unchecked exception is raised by {@link edu.ntnu.idi.idatt.model.Exchange} whenever
 * a look-up by ticker symbol produces no matching
 * {@link edu.ntnu.idi.idatt.model.Stock} entry.</p>
 */
public class StockNotFoundException extends RuntimeException {

  /**
   * Constructs a new {@code StockNotFoundException} with the specified detail message.
   *
   * @param message a description that typically includes the symbol that was not found
   */
  public StockNotFoundException(String message) {
    super(message);
  }
}
