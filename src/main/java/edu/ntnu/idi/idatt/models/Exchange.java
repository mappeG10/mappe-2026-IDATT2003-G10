package edu.ntnu.idi.idatt.models;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Exchange {

  private String name;
  private int week;
  private Map<String, Stock> stockMap;
  private Random random;

  public Exchange(String name, List<Stock> stocks) {
    this.name = name;
    this.week = 1;
    for (Stock stock : stocks) {
      stockMap.put(stock.getSymbol(), stock);
    }
    this.random = new Random();

  }

  public String getName() {
    return name;
  }

  public int getWeek() {
    return week;
  }

  public boolean hasStock(String symbol) {
    return stockMap.containsKey(symbol);
  }

  public Stock getStock(String symbol) {
    return stockMap.get(symbol);
  }

  public List<Stock> findStock(String searchTerm) {
    return stockMap.values().stream()
        .filter(stock -> stock.getSymbol().equalsIgnoreCase(searchTerm.toLowerCase()) ||
            stock.getCompany().toLowerCase().contains(searchTerm.toLowerCase()))
        .toList();
  }

  public Transaction buy(String symbol, BigDecimal quantity, Player player) {
    return null; // TODO: implement buy functionalities
  }

  public Transaction sell(Share share, Player player) {
    return null; // TODO: implement sell functionalities
  }

  public void advance() {
    week ++;

    // TODO: implement price updating mechanics
  }
}
