package edu.ntnu.idi.idatt.dal;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.idatt.dal.dto.GameStateDto;
import edu.ntnu.idi.idatt.model.Stock;
import java.util.List;
import org.junit.jupiter.api.Test;

class DataReaderFactoryTest {

  @Test
  void testGetStockReaderReturnsCsvReaderForCsvFile() {
    DataReader<List<Stock>> reader = DataReaderFactory.getStockReader("stocks.csv");

    assertNotNull(reader, "Reader should not be null");
    assertInstanceOf(
        CsvStockReader.class, reader, "Should return an instance of CsvStockReader for .csv files");
  }

  @Test
  void testGetStockReaderIsCaseInsensitiveForExtension() {
    DataReader<List<Stock>> reader = DataReaderFactory.getStockReader("STOCKS.CSV");
    assertInstanceOf(CsvStockReader.class, reader, "Should handle uppercase extensions correctly");
  }

  @Test
  void testGetStockReaderThrowsExceptionForNullSource() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          DataReaderFactory.getStockReader(null);
        },
        "Should throw IllegalArgumentException for null source");
  }

  @Test
  void testGetStockReaderThrowsExceptionForBlankSource() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          DataReaderFactory.getStockReader("   ");
        },
        "Should throw IllegalArgumentException for blank source");
  }

  @Test
  void testGetStockReaderThrowsExceptionForUnsupportedFormat() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          DataReaderFactory.getStockReader("data.xml");
        },
        "Should throw IllegalArgumentException for unsupported formats like .xml");
  }

  @Test
  void testGetGameReaderReturnsJsonReaderForMillionsFile() {
    DataReader<GameStateDto> reader = DataReaderFactory.getGameReader("save.millions");

    assertNotNull(reader, "Reader should not be null");
    assertInstanceOf(
        JsonGameReader.class,
        reader,
        "Should return an instance of JsonGameReader for .millions files");
  }

  @Test
  void testGetGameReaderThrowsExceptionForUnsupportedFormat() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          DataReaderFactory.getGameReader("data.xml");
        },
        "Should throw IllegalArgumentException for unsupported formats like .xml");
  }
}
