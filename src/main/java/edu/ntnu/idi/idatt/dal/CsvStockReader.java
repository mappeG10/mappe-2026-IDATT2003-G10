package edu.ntnu.idi.idatt.dal;

import edu.ntnu.idi.idatt.dal.exception.StockParsingException;
import edu.ntnu.idi.idatt.model.Stock;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Reads stock data from a comma-separated values (CSV) file.
 *
 * <p>Each non-comment, non-blank line in the file must contain exactly three
 * comma-separated fields in the following order:
 * <ol>
 *   <li><strong>symbol</strong> — the ticker symbol of the stock</li>
 *   <li><strong>company</strong> — the full name of the issuing company</li>
 *   <li><strong>price</strong> — the initial market price as a decimal number</li>
 * </ol>
 * Lines beginning with {@code #} and blank lines are silently skipped. Lines that fail
 * to parse are logged as warnings and skipped; only a completely empty result (no valid
 * stocks at all) causes a {@link StockParsingException} to be thrown.</p>
 */
public class CsvStockReader implements DataReader<List<Stock>> {

  private static final Logger LOGGER = Logger.getLogger(CsvStockReader.class.getName());
  private static final int EXPECTED_COLUMN_COUNT = 3;

  /**
   * Constructs a new {@code CsvStockReader}.
   */
  public CsvStockReader() {
  }

  /**
   * Reads and parses all valid stocks from the CSV file at the given path.
   *
   * <p>Each parseable line is converted into a {@link Stock} with a single initial price.
   * Invalid lines are logged and skipped. If the file contains no valid stock entries
   * after processing, a {@link StockParsingException} is thrown.</p>
   *
   * @param source the absolute or relative path to the CSV file to read
   * @return a non-empty list of {@link Stock} instances parsed from the file
   * @throws IOException            if the file at {@code source} does not exist or is not
   *                                readable
   * @throws StockParsingException  if the file contains no valid stock entries after parsing
   */
  @Override
  public List<Stock> read(String source) throws IOException, StockParsingException {
    Path path = Path.of(source);

    if (!Files.isReadable(path)) {
      throw new IOException("File is not readable: " + source);
    }

    List<Stock> stocks = new ArrayList<>();

    try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      String line;
      int lineNumber = 0;
      while ((line = br.readLine()) != null) {
        lineNumber++;
        if (isSkippable(line)) {
          continue;
        }
        try {
          stocks.add(parseLineToStock(line));
        } catch (StockParsingException e) {
          LOGGER.warning("Skipping line " + lineNumber + ": " + e.getMessage());
        }
      }
    }

    if (stocks.isEmpty()) {
      throw new StockParsingException("File contained no valid stocks: " + source);
    }

    return stocks;
  }

  /**
   * Determines whether a line should be ignored during parsing.
   *
   * <p>A line is skippable if it starts with {@code #} (comment) or consists solely
   * of whitespace.</p>
   *
   * @param line the raw line read from the file
   * @return {@code true} if the line should be skipped; {@code false} otherwise
   */
  private static boolean isSkippable(String line) {
    return line.startsWith("#") || line.trim().isBlank();
  }

  /**
   * Parses a single CSV line into a {@link Stock} with one initial price.
   *
   * @param line the raw CSV line to parse; must contain exactly {@value #EXPECTED_COLUMN_COUNT}
   *             comma-separated fields
   * @return a {@link Stock} constructed from the parsed fields
   * @throws StockParsingException if the line has the wrong number of columns, a blank
   *                               symbol or company name, a non-numeric price, or a
   *                               non-positive price value
   */
  private static Stock parseLineToStock(String line) throws StockParsingException {
    String[] data = line.split(",");

    if (data.length != EXPECTED_COLUMN_COUNT) {
      throw new StockParsingException(
          "Expected " + EXPECTED_COLUMN_COUNT + " columns but got " + data.length + ": " + line);
    }

    String symbol = data[0].trim();
    String company = data[1].trim();
    String priceRaw = data[2].trim();

    if (symbol.isBlank()) {
      throw new StockParsingException("Symbol is blank in line: " + line);
    }
    if (company.isBlank()) {
      throw new StockParsingException("Company name is blank in line: " + line);
    }

    BigDecimal price;
    try {
      price = new BigDecimal(priceRaw);
    } catch (NumberFormatException e) {
      throw new StockParsingException("Invalid price value '" + priceRaw + "' in line: " + line, e);
    }

    if (price.compareTo(BigDecimal.ZERO) <= 0) {
      throw new StockParsingException("Price must be positive, got '"
          + price + "' in line: " + line);
    }

    return new Stock(symbol, company, new ArrayList<>(List.of(price)));
  }

}
