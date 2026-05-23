package edu.ntnu.idi.idatt.dal;

import edu.ntnu.idi.idatt.models.Stock;
import yahoofinance.YahooFinance;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class YahooStockReader implements DataReader<List<Stock>> {

  @Override
  public List<Stock> read(String source) throws IOException, DataAccessException {
    String symbolsString = source.startsWith("api:") ? source.substring(4) : source;
    String[] symbols = symbolsString.split(",");

    try {
      Map<String, yahoofinance.Stock> yahooStocks = YahooFinance.get(symbols);
      List<Stock> internalStocks = new ArrayList<>();

      for (yahoofinance.Stock yStock : yahooStocks.values()) {
        if (yStock == null || yStock.getQuote().getPrice() == null) {
          continue;
        }

        String symbol = yStock.getSymbol();
        String name = yStock.getName();
        BigDecimal price = yStock.getQuote().getPrice();

        internalStocks.add(new Stock(symbol, name, new ArrayList<>(List.of(price))));
      }

      if (internalStocks.isEmpty()) {
        throw new DataAccessException("No valid stocks were found for symbols: " + source);
      }

      return internalStocks;

    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      throw new DataAccessException("An error occurred while fetching data from Yahoo Finance", e);
    }
  }
}
