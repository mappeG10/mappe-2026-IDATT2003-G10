package edu.ntnu.idi.idatt.dal;

import edu.ntnu.idi.idatt.dal.exceptions.StockParsingException;
import edu.ntnu.idi.idatt.models.Stock;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class CsvStockReader implements DataReader<List<Stock>> {

  private static final Logger LOGGER = Logger.getLogger(CsvStockReader.class.getName());

  public CsvStockReader() {
  }

  @Override
  public List<Stock> read(String source) throws IOException, StockParsingException {
    List<Stock> stocks = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(source))) {
      String line;
      int lineNumber = 0;
      while ((line = br.readLine()) != null) {
        lineNumber++;
        if (isSkippable(line)) {
          continue;
        }
        try {
          parseLineToStock(line).ifPresent(stocks::add);
        } catch (StockParsingException e) {
          LOGGER.warning("Line " + lineNumber + ": " + e.getMessage());
        }

      }
      if (stocks.isEmpty()) {
        throw new StockParsingException("Source file was empty or contained no valid stocks.");
      }
    }
    return stocks;
  }

  private static boolean isSkippable(String line) {
    return line.startsWith("#") || line.trim().isBlank();
  }

  private static Optional<Stock> parseLineToStock(String line) throws StockParsingException {
    String[] data = line.split(",");

    if (data.length != 3) {
      throw new StockParsingException("Malformed stock row (expected 3 columns): " + line);
    }

    try {
      String symbol = data[0].trim();
      String company = data[1].trim();
      BigDecimal price = new BigDecimal(data[2].trim());

      return Optional.of(new Stock(symbol, company, new ArrayList<>(List.of(price))));
    } catch (Exception e) {
      throw new StockParsingException("Error parsing stock values in line: " + line, e);
    }
  }

}
