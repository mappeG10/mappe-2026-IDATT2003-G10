package edu.ntnu.idi.idatt.models;

import edu.ntnu.idi.idatt.models.exceptions.StockNotFoundException;
import edu.ntnu.idi.idatt.view.GameObserver;

import java.math.BigDecimal;
import java.util.*;

public class Exchange implements GameSubject {

  private final String name;
  private int week;
  private final Map<String, Stock> stockMap;
  private final Random random;

  private final List<GameObserver> observers = new ArrayList<>();

  @Override
  public void register(GameObserver observer) {
    if (!observers.contains(observer)) {
      observers.add(observer);
    }
    //TODO: Unit test or integration test this function
  }

  @Override
  public void unregister(GameObserver observer) {
    observers.remove(observer);
    //TODO: Unit test or integration test this function
  }

  private void notifyObservers() {
    observers.forEach(GameObserver::update);
    //TODO: Unit test or integration test this function
  }

  public Exchange(String name, List<Stock> stocks) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be null or blank");
    }
    if (stocks == null || stocks.isEmpty()) {
      throw new IllegalArgumentException("Stocks cannot be null or empty");
    }
    this.name = name;
    this.week = 1;
    this.stockMap = new HashMap<>();
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
    Stock stock = stockMap.get(symbol);
    if (stock == null) {
      throw new StockNotFoundException("No stock with symbol: " + symbol);
    }
    return stock;
  }

  public List<Stock> getAllStocks() {
    return new ArrayList<>(stockMap.values());
  }

  public List<Stock> findStock(String searchTerm) {
    return stockMap.values().stream()
        .filter(stock -> stock.getSymbol().equalsIgnoreCase(searchTerm.toLowerCase()) ||
            stock.getCompany().toLowerCase().contains(searchTerm.toLowerCase()))
        .toList();
  }

  public Transaction buy(String symbol, BigDecimal quantity, Player player) {
    if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }

    Stock stock = getStock(symbol);

    Share share = new Share(stock, quantity, stock.getSalesPrice());

    Purchase purchase = new Purchase(share, week);

    purchase.commit(player);

    if (purchase.isCommitted()) {
      notifyObservers();
    }

    return purchase;

  }

  public Transaction sell(Share share, BigDecimal quantity, Player player) {
    if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
    if (quantity.compareTo(share.getQuantity()) > 0) {
      throw new IllegalArgumentException("Cannot sell more than owned quantity");
    }

    Share saleShare = new Share(share.getStock(), quantity, share.getPurchasePrice());
    Sale sale = new Sale(saleShare, week);
    sale.commit(player);

    if (sale.isCommitted()) {
      notifyObservers();
    }

    return sale;
  }

  public void advance() {
    week ++;

    for (Stock stock : stockMap.values()) {
      BigDecimal oldStockPrice = stock.getSalesPrice();

      // Should make a random value between 7.5% and -7.5%
      BigDecimal randomPriceChangeConstant = BigDecimal.valueOf((random.nextDouble() * 0.15) - 0.075);


      BigDecimal newStockPrice = oldStockPrice.add(oldStockPrice.multiply(randomPriceChangeConstant));
      // TODO: depending on the format of the prices we might need to round the decimals of the new price

      stock.addNewSalesPrice(newStockPrice);

    }
    notifyObservers();
  }

  public List<Stock> getGainers(int limit) {
    if (limit < 1) {
      throw new IllegalArgumentException("The limit can not be less than 1");
    }
    return stockMap.values().stream()
        .filter(Stock::hasPriceHistory)
        .sorted(Comparator.comparing(Stock::getLatestPriceChangePercent).reversed())
        .limit(limit)
        .toList();

    // TODO: Make unit tests to make sure this works

  }

  public List<Stock> getLosers(int limit) {
    if (limit < 1) {
      throw new IllegalArgumentException("The limit can not be less than 1");
    }
    return stockMap.values().stream()
        .filter(Stock::hasPriceHistory)
        .sorted(Comparator.comparing(Stock::getLatestPriceChangePercent))
        .limit(limit)
        .toList();
  }

}
