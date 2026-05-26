package edu.ntnu.idi.idatt.dal.exception;

/**
 * Checked exception representing a failure in the data access layer.
 *
 * <p>Thrown when a read or write operation cannot be completed due to an I/O error, a missing file,
 * or a malformed data source. Callers that catch this exception should either surface the error to
 * the user or attempt a recovery action such as prompting for a different file path.
 */
public class DataAccessException extends Exception {

  /**
   * Constructs a new {@code DataAccessException} with the specified detail message.
   *
   * @param message a human-readable description of the failure
   */
  public DataAccessException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@code DataAccessException} with the specified detail message and cause.
   *
   * @param message a human-readable description of the failure
   * @param cause the underlying exception that triggered this failure
   */
  public DataAccessException(String message, Throwable cause) {
    super(message, cause);
  }
}
