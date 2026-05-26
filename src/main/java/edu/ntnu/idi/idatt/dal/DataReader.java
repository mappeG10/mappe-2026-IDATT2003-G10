package edu.ntnu.idi.idatt.dal;

import edu.ntnu.idi.idatt.dal.exception.DataAccessException;
import java.io.IOException;

/**
 * Generic contract for reading structured data from a source location.
 *
 * <p>Implementations are responsible for locating, opening, and parsing a data source
 * identified by the {@code source} string (typically a file path), and returning a fully
 * constructed domain object of type {@code T}.</p>
 *
 * @param <T> the type of the object produced by reading the data source
 */
public interface DataReader<T> {

  /**
   * Reads and parses the data source at the given location.
   *
   * @param source the location of the data source (e.g., an absolute or relative file path);
   *               must not be {@code null} or blank
   * @return a fully constructed object representing the parsed data; never {@code null}
   * @throws IOException           if the source cannot be opened or read due to an I/O error
   * @throws DataAccessException   if the source exists but its contents cannot be parsed
   *                               into the expected format
   */
  T read(String source) throws IOException, DataAccessException;
}
