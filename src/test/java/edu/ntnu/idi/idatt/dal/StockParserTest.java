package edu.ntnu.idi.idatt.dal;

import edu.ntnu.idi.idatt.models.Stock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockParserTest {

  @TempDir
  Path tempDir;

  @Test
  void testParseStocksValidData() throws IOException {
    // Create a temporary CSV file
    Path filePath = tempDir.resolve("test_stocks.csv");
    String content = """
                # Ticker,Name,Price
                NVDA, Nvidia, 191.27
                
                AAPL, Apple Inc., 276.43
                """;
    Files.writeString(filePath, content);

    List<Stock> stocks = StockParser.parseStocks(filePath.toString());

    assertEquals(2, stocks.size(), "Should parse exactly two stocks, skipping comments and blank lines");
    assertEquals("NVDA", stocks.get(0).getSymbol());
    assertEquals("Apple Inc.", stocks.get(1).getCompany());
  }

  @Test
  void testParseStocksInvalidData() throws IOException {
    Path filePath = tempDir.resolve("test_stocks.csv");
    String content = """
                # Ticker,Name,Price
                NVDA, nvm, nv, 2019
                
                129, 129, hello
                """;

    Files.writeString(filePath, content);

    List<Stock> stocks = StockParser.parseStocks(filePath.toString());

    assertTrue(stocks.isEmpty());

  }

  @Test
  void testParseStockThrowsException() {
    Path filePath = tempDir.resolve("test_stocks.csv");
    assertThrows(RuntimeException.class, () -> StockParser.parseStocks(filePath.toString()));

  }


}