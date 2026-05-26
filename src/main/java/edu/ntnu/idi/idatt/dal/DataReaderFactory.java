package edu.ntnu.idi.idatt.dal;

import edu.ntnu.idi.idatt.dal.dto.GameStateDto;
import edu.ntnu.idi.idatt.model.Stock;
import java.util.List;

/**
 * Factory for obtaining the appropriate {@link DataReader} implementation for a given source.
 *
 * <p>Reader selection is determined by the file extension of the {@code source} path.
 * Currently supported formats:
 * <ul>
 *   <li>{@code .csv} — stock data, read by {@link CsvStockReader}</li>
 *   <li>{@code .millions} — saved game state, read by {@link JsonGameReader}</li>
 * </ul>
 *
 * <p>This class is not instantiable; all methods are static.</p>
 */
public class DataReaderFactory {

  private DataReaderFactory() {}

  /**
   * Returns a {@link DataReader} capable of reading a list of stocks from the given source.
   *
   * @param source the file path of the stock data source; the extension determines the reader
   *               implementation selected
   * @return a {@link DataReader} that produces a {@code List<Stock>}
   * @throws IllegalArgumentException if {@code source} is {@code null} or blank, or if no
   *                                  suitable reader exists for the given file extension
   */
  public static DataReader<List<Stock>> getStockReader(String source) {
    validateSource(source);
    if (source.toLowerCase().endsWith(".csv")) {
      return new CsvStockReader();
    }

    throw new IllegalArgumentException("No suitable stock reader found for: " + source);
  }

  /**
   * Returns a {@link DataReader} capable of reading a saved game state from the given source.
   *
   * @param source the file path of the saved game file; the extension determines the reader
   *               implementation selected
   * @return a {@link DataReader} that produces a {@link GameStateDto}
   * @throws IllegalArgumentException if {@code source} is {@code null} or blank, or if no
   *                                  suitable reader exists for the given file extension
   */
  public static DataReader<GameStateDto> getGameReader(String source) {
    validateSource(source);
    if (source.toLowerCase().endsWith(".millions")) {
      return new JsonGameReader();
    }

    throw new IllegalArgumentException("No suitable game reader found for: " + source);
  }

  /**
   * Validates that the given source string is neither {@code null} nor blank.
   *
   * @param source the source path to validate
   * @throws IllegalArgumentException if {@code source} is {@code null} or blank
   */
  private static void validateSource(String source) {
    if (source == null || source.isBlank()) {
      throw new IllegalArgumentException("Source cannot be null or empty.");
    }
  }

}
