package edu.ntnu.idi.idatt.dal;

import edu.ntnu.idi.idatt.model.Stock;

import java.util.List;

public class StockReaderFactory {
  private StockReaderFactory() {}

  public static DataReader<List<Stock>> getStockReader(String source) {
    if (source == null || source.isBlank()) {
      throw new IllegalArgumentException("Source cannot be null or empty.");
    }

    if (source.toLowerCase().endsWith(".csv")){
      return new CsvStockReader();
    }

    throw  new IllegalArgumentException("No suitable reader found for: " + source);
  }

}
