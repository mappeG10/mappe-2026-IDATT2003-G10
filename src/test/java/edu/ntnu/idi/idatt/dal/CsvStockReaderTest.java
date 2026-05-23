package edu.ntnu.idi.idatt.dal;

import edu.ntnu.idi.idatt.models.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvStockReaderTest {

  @TempDir
  Path tempDir;

  private CsvStockReader stockReader;

  @BeforeEach
  void setup() {
    stockReader = new CsvStockReader();
  }

  @Test
  void testReadValidData() throws IOException, DataAccessException {
    // Create a temporary CSV file
    Path filePath = tempDir.resolve("test_stocks.csv");
    String content = """
                # Ticker,Name,Price
                NVDA, Nvidia, 191.27
                
                AAPL, Apple Inc., 276.43
                """;
    Files.writeString(filePath, content);

    List<Stock> stocks = stockReader.read(filePath.toString());

    assertEquals(2, stocks.size(), "Should parse exactly two stocks, skipping comments and blank lines");
    assertEquals("NVDA", stocks.get(0).getSymbol());
    assertEquals("Apple Inc.", stocks.get(1).getCompany());
  }

  @Test
  void testReadInvalidDataThrowsDataAccessException() throws IOException{
    Path filePath = tempDir.resolve("test_stocks.csv");
    String content = """
                # Ticker,Name,Price
                NVDA, nvm, nv, 2019
                
                129, 129, hello
                """;

    Files.writeString(filePath, content);

    assertThrows(DataAccessException.class, () -> stockReader.read(filePath.toString()));

  }

  @Test
  void testParseNonExistentFileThrowsIOException() {
    Path filePath = tempDir.resolve("test_stocks.csv");
    assertThrows(IOException.class, () -> stockReader.read(filePath.toString()));

  }


}