package edu.ntnu.idi.idatt.dal;

import edu.ntnu.idi.idatt.dal.exception.DataAccessException;
import edu.ntnu.idi.idatt.model.Stock;
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

  @Test
  void testReadEmptyFileThrowsDataAccessException() throws IOException {
    Path filePath = tempDir.resolve("empty.csv");
    Files.writeString(filePath, "");

    assertThrows(DataAccessException.class, () -> stockReader.read(filePath.toString()),
        "Empty file should throw DataAccessException");
  }

  @Test
  void testReadOnlyCommentsAndBlankLinesThrowsDataAccessException() throws IOException {
    Path filePath = tempDir.resolve("comments_only.csv");
    String content = """
        # Ticker,Name,Price

        # Another comment
        """;
    Files.writeString(filePath, content);

    assertThrows(DataAccessException.class, () -> stockReader.read(filePath.toString()),
        "File with only comments and blank lines should throw DataAccessException");
  }

  @Test
  void testReadBlankSymbolSkipsLine() throws IOException, DataAccessException {
    Path filePath = tempDir.resolve("blank_symbol.csv");
    String content = """
        , Apple Inc., 276.43
        NVDA, Nvidia, 191.27
        """;
    Files.writeString(filePath, content);

    List<Stock> stocks = stockReader.read(filePath.toString());

    assertEquals(1, stocks.size(), "Line with blank symbol should be skipped");
    assertEquals("NVDA", stocks.getFirst().getSymbol());
  }

  @Test
  void testReadBlankCompanySkipsLine() throws IOException, DataAccessException {
    Path filePath = tempDir.resolve("blank_company.csv");
    String content = """
        AAPL, , 276.43
        NVDA, Nvidia, 191.27
        """;
    Files.writeString(filePath, content);

    List<Stock> stocks = stockReader.read(filePath.toString());

    assertEquals(1, stocks.size(), "Line with blank company should be skipped");
    assertEquals("NVDA", stocks.getFirst().getSymbol());
  }

  @Test
  void testReadZeroPriceSkipsLine() throws IOException, DataAccessException {
    Path filePath = tempDir.resolve("zero_price.csv");
    String content = """
        AAPL, Apple Inc., 0
        NVDA, Nvidia, 191.27
        """;
    Files.writeString(filePath, content);

    List<Stock> stocks = stockReader.read(filePath.toString());

    assertEquals(1, stocks.size(), "Line with zero price should be skipped");
    assertEquals("NVDA", stocks.getFirst().getSymbol());
  }

  @Test
  void testReadNegativePriceSkipsLine() throws IOException, DataAccessException {
    Path filePath = tempDir.resolve("negative_price.csv");
    String content = """
        AAPL, Apple Inc., -50.00
        NVDA, Nvidia, 191.27
        """;
    Files.writeString(filePath, content);

    List<Stock> stocks = stockReader.read(filePath.toString());

    assertEquals(1, stocks.size(), "Line with negative price should be skipped");
    assertEquals("NVDA", stocks.getFirst().getSymbol());
  }

  @Test
  void testReadInvalidLinesAreSkippedAndValidOnesReturned() throws IOException, DataAccessException {
    Path filePath = tempDir.resolve("mixed.csv");
    String content = """
        AAPL, Apple Inc., 276.43
        BAD_ROW_TOO_MANY, Extra, Columns, Here
        NVDA, Nvidia, not_a_number
        GOOG, Alphabet, 185.50
        """;
    Files.writeString(filePath, content);

    List<Stock> stocks = stockReader.read(filePath.toString());

    assertEquals(2, stocks.size(), "Only valid lines should be returned");
    assertEquals("AAPL", stocks.get(0).getSymbol());
    assertEquals("GOOG", stocks.get(1).getSymbol());
  }

}
