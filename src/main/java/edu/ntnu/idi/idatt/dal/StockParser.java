package edu.ntnu.idi.idatt.dal;

import edu.ntnu.idi.idatt.models.Stock;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StockParser {

  public static List<Stock> parseStocks(String fileName) {
    List<Stock> stocks = new ArrayList<>();
    String line;

    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      while ((line = br.readLine()) != null) {
        if (line.startsWith("#") || line.trim().isBlank()) {
          continue;
        }

        String[] data = line.split(",");

        String symbol = data[0].trim();
        String name = data[1].trim();
        BigDecimal price = new BigDecimal(data[2].trim());

        try {
          stocks.add(new Stock(symbol, name, new ArrayList<>(List.of(price))));
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        }
      }
      return stocks;
    } catch (IOException e) {
      throw new RuntimeException(e); // TODO: use custom exceptions later
    }
  }
}
