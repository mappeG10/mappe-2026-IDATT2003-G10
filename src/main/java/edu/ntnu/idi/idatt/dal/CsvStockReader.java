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

public class CsvStockReader implements DataReader<List<Stock>> {

  private static final Logger LOGGER = Logger.getLogger(CsvStockReader.class.getName());
  private static final int EXPECTED_COLUMN_COUNT = 3;

  public CsvStockReader() {
  }

  @Override
  public List<Stock> read(String source) throws IOException, StockParsingException {
    Path path = Path.of(source);

    if (!Files.exists(path)) {
      throw new IOException("File not found: " + source);
    }
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

  private static boolean isSkippable(String line) {
    return line.startsWith("#") || line.trim().isBlank();
  }

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
      throw new StockParsingException("Price must be positive, got '" + price + "' in line: " + line);
    }

    return new Stock(symbol, company, new ArrayList<>(List.of(price)));
  }

}
