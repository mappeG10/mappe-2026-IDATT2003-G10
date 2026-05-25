package edu.ntnu.idi.idatt.dal;

import edu.ntnu.idi.idatt.dal.dto.GameStateDto;
import edu.ntnu.idi.idatt.model.Stock;

import java.util.List;

public class DataReaderFactory {
  private DataReaderFactory() {}

  public static DataReader<List<Stock>> getStockReader(String source) {
    validateSource(source);
    if (source.toLowerCase().endsWith(".csv")){
      return new CsvStockReader();
    }

    throw  new IllegalArgumentException("No suitable stock reader found for: " + source);
  }

  public static DataReader<GameStateDto> getGameReader(String source) {
    validateSource(source);
    if (source.toLowerCase().endsWith(".millions")) {
      return new JsonGameReader();
    }

    throw new IllegalArgumentException("No suitable game reader found for: " + source);
  }

  private static void validateSource(String source) {
    if (source == null || source.isBlank()) {
      throw new IllegalArgumentException("Source cannot be null or empty.");
    }
  }

}
