package edu.ntnu.idi.idatt.dal;

import edu.ntnu.idi.idatt.models.Stock;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StockParser {

  public static List<Stock> parseStocks(String fileName) {
    List<Stock> stocks = new ArrayList<>();
    String line;

    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      while ((line = br.readLine()) != null) {
        if (isSkippable(line)) {
          continue;
        }
        parseLineToStock(line).ifPresent(stocks::add);
      }
      return stocks;
    } catch (IOException e) {
      throw new RuntimeException(e); // TODO: use custom exceptions later
    }
  }

  private static boolean isSkippable(String line) {
    return line.startsWith("#") || line.trim().isBlank();
  }

  private static Optional<Stock> parseLineToStock(String line) {
    try {
      String[] data = line.split(",");

      if (data.length < 3) {
        return Optional.empty();
      }

      String symbol = data[0].trim();
      String name = data[1].trim();
      BigDecimal price = new BigDecimal(data[2].trim());

      return Optional.of(
          new Stock(symbol, name, new ArrayList<>(List.of(price))));

    } catch (IllegalArgumentException e) {
      System.err.println(e);
      System.err.println("Skipping malformed stock row: " + line);
      return Optional.empty();
    }
  }

}
