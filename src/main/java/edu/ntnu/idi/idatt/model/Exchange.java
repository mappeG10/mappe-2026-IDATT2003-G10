package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.exception.StockNotFoundException;
import edu.ntnu.idi.idatt.model.transaction.Transaction;
import edu.ntnu.idi.idatt.model.transaction.TransactionFactory;
import edu.ntnu.idi.idatt.model.transaction.TransactionType;
import edu.ntnu.idi.idatt.observer.GameObserver;
import edu.ntnu.idi.idatt.observer.GameSubject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Represents a stock exchange that hosts a collection of tradable stocks.
 *
 * <p>The exchange maintains the current game week, routes buy and sell orders to the
 * appropriate transaction logic, and advances the simulation by one week at a time.
 * Each call to {@link #advance()} applies a random price change of up to ±7.5% to every
 * listed stock and notifies all registered {@link GameObserver}s.</p>
 *
 * <p>The exchange implements {@link GameSubject}, so controllers and UI components can
 * register as observers and react to week advances or completed trades.</p>
 */
public class Exchange implements GameSubject {

  private final String name;
  private int week;
  private final Map<String, Stock> stockMap;
  private final Random random;
  private final List<GameObserver> observers = new ArrayList<>();

  /**
   * {@inheritDoc}
   *
   * <p>Duplicate registrations are silently ignored.</p>
   */
  @Override
  public void register(GameObserver observer) {
    if (!observers.contains(observer)) {
      observers.add(observer);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void unregister(GameObserver observer) {
    observers.remove(observer);
  }

  /**
   * Notifies all registered observers that the exchange state has changed.
   */
  private void notifyObservers() {
    observers.forEach(GameObserver::update);
  }

  /**
   * Constructs a new exchange with the given name and initial set of listed stocks.
   *
   * <p>Each stock is indexed by its ticker symbol for O(1) look-ups. The week counter
   * starts at 1.</p>
   *
   * @param name   the display name of this exchange; must not be {@code null} or blank
   * @param stocks the initial list of stocks to list on this exchange; must not be
   *               {@code null} or empty
   * @throws IllegalArgumentException if {@code name} is {@code null} or blank, or if
   *                                  {@code stocks} is {@code null} or empty
   */
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

  /**
   * Retrieves the display name of this exchange.
   *
   * @return the exchange name; never {@code null} or blank
   */
  public String getName() {
    return name;
  }

  /**
   * Retrieves the current game week on this exchange.
   *
   * @return the current week number; always at least 1
   */
  public int getWeek() {
    return week;
  }

  /**
   * Checks whether a stock with the given ticker symbol is listed on this exchange.
   *
   * @param symbol the ticker symbol to look up
   * @return {@code true} if a stock with the given symbol exists; {@code false} otherwise
   */
  public boolean hasStock(String symbol) {
    return stockMap.containsKey(symbol);
  }

  /**
   * Retrieves the stock with the given ticker symbol from this exchange.
   *
   * @param symbol the ticker symbol to look up
   * @return the matching {@link Stock}; never {@code null}
   * @throws StockNotFoundException if no stock with the specified symbol is listed
   */
  public Stock getStock(String symbol) {
    Stock stock = stockMap.get(symbol);
    if (stock == null) {
      throw new StockNotFoundException("No stock with symbol: " + symbol);
    }
    return stock;
  }

  /**
   * Retrieves all stocks currently listed on this exchange.
   *
   * @return a new mutable list containing all listed stocks; never {@code null}
   */
  public List<Stock> getAllStocks() {
    return new ArrayList<>(stockMap.values());
  }

  /**
   * Searches for stocks whose symbol or company name matches the given search term.
   *
   * <p>The symbol comparison is case-insensitive and exact; the company name comparison
   * is a case-insensitive substring match.</p>
   *
   * @param searchTerm the text to search for; matched against both symbol and company name
   * @return a list of stocks matching the search term; empty if none match
   */
  public List<Stock> findStock(String searchTerm) {
    return stockMap.values().stream()
        .filter(stock -> stock.getSymbol().equalsIgnoreCase(searchTerm.toLowerCase())
            || stock.getCompany().toLowerCase().contains(searchTerm.toLowerCase()))
        .toList();
  }

  /**
   * Executes a purchase of the specified stock on behalf of the given player.
   *
   * <p>A new {@link Share} is created at the stock's current sales price, wrapped in a
   * {@link edu.ntnu.idi.idatt.model.transaction.Purchase} transaction, and committed against
   * the player's account. All registered observers are notified if the transaction commits
   * successfully.</p>
   *
   * @param symbol   the ticker symbol of the stock to buy
   * @param quantity the number of shares to purchase; must be positive and not {@code null}
   * @param player   the player executing the purchase
   * @return the committed {@link Transaction}
   * @throws IllegalArgumentException                                                   if
   *         {@code quantity} is {@code null} or not positive
   * @throws StockNotFoundException                                                      if no
   *         stock with the given symbol is listed on this exchange
   * @throws edu.ntnu.idi.idatt.model.exception.InsufficientFundsException              if the
   *         player's balance is lower than the total purchase cost
   */
  public Transaction buy(String symbol, BigDecimal quantity, Player player) {
    if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }

    Stock stock = getStock(symbol);

    Share share = new Share(stock, quantity, stock.getSalesPrice());

    Transaction purchase = TransactionFactory
        .createTransaction(TransactionType.PURCHASE, share, week);

    purchase.commit(player);

    if (purchase.isCommitted()) {
      notifyObservers();
    }

    return purchase;

  }

  /**
   * Executes a sale of shares from an existing portfolio position on behalf of the given player.
   *
   * <p>A new {@link Share} is constructed at the original purchase price and wrapped in a
   * {@link edu.ntnu.idi.idatt.model.transaction.Sale} transaction, then committed against
   * the player's account. All registered observers are notified if the transaction commits
   * successfully.</p>
   *
   * @param share    the portfolio share position from which to sell; used to determine the
   *                 purchase price
   * @param quantity the number of shares to sell; must be positive, not {@code null}, and
   *                 not greater than the quantity held in the position
   * @param player   the player executing the sale
   * @return the committed {@link Transaction}
   * @throws IllegalArgumentException                                                      if
   *         {@code quantity} is {@code null}, not positive, or exceeds the held quantity
   * @throws edu.ntnu.idi.idatt.model.exception.InsufficientSharesException               if
   *         the player's portfolio does not contain enough shares
   */
  public Transaction sell(Share share, BigDecimal quantity, Player player) {
    if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
    if (quantity.compareTo(share.getQuantity()) > 0) {
      throw new IllegalArgumentException("Cannot sell more than owned quantity");
    }

    Share saleShare = new Share(share.getStock(), quantity, share.getPurchasePrice());
    Transaction sale = TransactionFactory.createTransaction(TransactionType.SALE, saleShare, week);
    sale.commit(player);

    if (sale.isCommitted()) {
      notifyObservers();
    }

    return sale;
  }

  /**
   * Advances the simulation by one week and randomises every stock's price.
   *
   * <p>Each stock's price is adjusted by a uniformly distributed random factor in the
   * range {@code [-7.5%, +7.5%]}. The new price is appended to the stock's price
   * history. All registered observers are notified after all prices have been updated.</p>
   */
  public void advance() {
    week++;

    for (Stock stock : stockMap.values()) {
      BigDecimal oldStockPrice = stock.getSalesPrice();

      BigDecimal randomPriceChangeConstant = BigDecimal.valueOf(
          (random.nextDouble() * 0.15) - 0.075);

      BigDecimal newStockPrice = oldStockPrice.add(
          oldStockPrice.multiply(randomPriceChangeConstant));

      stock.addNewSalesPrice(newStockPrice);

    }
    notifyObservers();
  }

  /**
   * Overrides the current week counter to the specified value.
   *
   * <p>Intended for use during game-state restoration (e.g., loading a saved game).</p>
   *
   * @param week the week number to set; must be at least 1
   * @throws IllegalArgumentException if {@code week} is less than 1
   */
  public void setWeek(int week) {
    if (week < 1) {
      throw new IllegalArgumentException("Week must be at least 1");
    }
    this.week = week;
  }

  /**
   * Retrieves the top-performing stocks ranked by their most recent percentage price gain.
   *
   * <p>Only stocks with at least two recorded prices (i.e., {@link Stock#hasPriceHistory()}
   * returns {@code true}) are included in the ranking.</p>
   *
   * @param limit the maximum number of stocks to return; must be at least 1
   * @return a list of up to {@code limit} stocks sorted in descending order by latest
   *         percentage price change
   * @throws IllegalArgumentException if {@code limit} is less than 1
   */
  public List<Stock> getGainers(int limit) {
    if (limit < 1) {
      throw new IllegalArgumentException("The limit can not be less than 1");
    }
    return stockMap.values().stream()
        .filter(Stock::hasPriceHistory)
        .sorted(Comparator.comparing(Stock::getLatestPriceChangePercent).reversed())
        .limit(limit)
        .toList();

  }

  /**
   * Retrieves the worst-performing stocks ranked by their most recent percentage price decline.
   *
   * <p>Only stocks with at least two recorded prices (i.e., {@link Stock#hasPriceHistory()}
   * returns {@code true}) are included in the ranking.</p>
   *
   * @param limit the maximum number of stocks to return; must be at least 1
   * @return a list of up to {@code limit} stocks sorted in ascending order by latest
   *         percentage price change
   * @throws IllegalArgumentException if {@code limit} is less than 1
   */
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
