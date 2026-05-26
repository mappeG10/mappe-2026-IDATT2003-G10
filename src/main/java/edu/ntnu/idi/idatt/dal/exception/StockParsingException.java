package edu.ntnu.idi.idatt.dal.exception;

/**
 * Thrown when a stock data source cannot be parsed into a valid {@link
 * edu.ntnu.idi.idatt.model.Stock} instance.
 *
 * <p>This exception is a specialisation of {@link DataAccessException} used exclusively by readers
 * that parse structured stock data (e.g., CSV files). It is raised when a row contains the wrong
 * number of columns, a blank symbol or company name, or an invalid price value.
 */
public class StockParsingException extends DataAccessException {

  /**
   * Constructs a new {@code StockParsingException} with the specified detail message.
   *
   * @param message a description of the malformed input, typically including the offending line
   *     content
   */
  public StockParsingException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@code StockParsingException} with the specified detail message and cause.
   *
   * @param message a description of the malformed input
   * @param cause the underlying exception that triggered the parsing failure
   */
  public StockParsingException(String message, Throwable cause) {
    super(message, cause);
  }
}
