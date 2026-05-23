package edu.ntnu.idi.idatt.dal;

import edu.ntnu.idi.idatt.models.Stock;

import javax.swing.text.html.Option;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CsvStockReader implements DataReader<List<Stock>> {

  public CsvStockReader() {

  }

  @Override
  public List<Stock> read(String source) throws IOException, DataAccessException {
    List<Stock> stocks = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(source))) {
      String line;
      int lineNumber = 0;
      while ((line = br.readLine()) != null) {
        lineNumber++;
        if (isSkippable(line)) {
          continue;
        }
        Optional<Stock> stock = parseLineToStock(line);
        if (stock.isPresent()) {
          stocks.add(stock.get());
        } else {
          throw new DataAccessException("Malformed data at line: "
              + lineNumber + ": " + line);
        }

      }
      if  (stocks.isEmpty()) {
        throw new DataAccessException("Source file was empty or contained no valid stocks.");
      }
    }
    return stocks;
  }

  private static boolean isSkippable(String line) {
    return line.startsWith("#") || line.trim().isBlank();
  }

  private static Optional<Stock> parseLineToStock(String line) {

    String[] data = line.split(",");

    if (data.length != 3) {
      return Optional.empty();
    }

    String symbol = data[0].trim();
    String name = data[1].trim();
    BigDecimal price = new BigDecimal(data[2].trim());

    return Optional.of(
        new Stock(symbol, name, new ArrayList<>(List.of(price))));


  }

}
