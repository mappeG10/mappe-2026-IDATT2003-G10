package edu.ntnu.idi.idatt.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a publicly traded stock with a full price history.
 *
 * <p>A stock is identified by its ticker {@code symbol} and the name of the issuing
 * {@code company}. Prices are stored in chronological order; the most recently appended
 * price is treated as the current market (sales) price. The price history is used to derive
 * performance metrics such as the latest absolute change and percentage change.</p>
 */
public class Stock {

  private final String symbol;
  private final String company;
  private final List<BigDecimal> prices;

  /**
   * Constructs a new stock with the given symbol, company name, and initial price history.
   *
   * @param symbol  the ticker symbol identifying this stock; must not be {@code null} or blank
   * @param company the full name of the issuing company; must not be {@code null} or blank
   * @param prices  the initial list of historical prices in chronological order; must not be
   *                {@code null}, but may be empty
   * @throws IllegalArgumentException if {@code symbol} or {@code company} is {@code null} or
   *                                  blank, or if {@code prices} is {@code null}
   */
  public Stock(String symbol, String company, List<BigDecimal> prices) {
    if (symbol == null || symbol.isBlank()) {
      throw new IllegalArgumentException("Symbol cannot be null or blank");
    }
    if (company == null || company.isBlank()) {
      throw new IllegalArgumentException("Company cannot be null or blank");
    }
    if (prices == null) {
      throw new IllegalArgumentException("Prices cannot be null");
    }

    this.symbol = symbol;
    this.company = company;
    this.prices = new ArrayList<>(prices);
  }

  /**
   * Retrieves the ticker symbol that uniquely identifies this stock on the exchange.
   *
   * @return the ticker symbol; never {@code null} or blank
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * Retrieves the full name of the company that issued this stock.
   *
   * @return the company name; never {@code null} or blank
   */
  public String getCompany() {
    return company;
  }

  /**
   * Retrieves the current market price, defined as the most recently recorded price.
   *
   * @return the latest price in the price history
   * @throws IllegalStateException if no prices have been recorded for this stock
   */
  public BigDecimal getSalesPrice() {
    if (prices.isEmpty()) {
      throw new IllegalStateException("The stock " + symbol + " has no prices registered");
    }
    return prices.getLast();
  }

  /**
   * Appends a new market price to this stock's price history.
   *
   * <p>The appended price becomes the new current sales price returned by
   * {@link #getSalesPrice()}.</p>
   *
   * @param price the new price to record; must not be {@code null}
   * @throws IllegalArgumentException if {@code price} is {@code null}
   */
  public void addNewSalesPrice(BigDecimal price) {
    if (price == null) {
      throw new IllegalArgumentException("Price cannot be null");
    }
    prices.add(price);
  }

  /**
   * Retrieves an unmodifiable view of the complete price history in chronological order.
   *
   * @return an unmodifiable list of all recorded prices; never {@code null}
   */
  public List<BigDecimal> getHistoricalPrices() {
    return Collections.unmodifiableList(prices);
  }

  /**
   * Retrieves the highest price ever recorded in this stock's price history.
   *
   * @return the maximum price across all recorded entries
   * @throws IllegalStateException if the price history is empty
   */
  public BigDecimal getHighestPrice() {
    if (prices.isEmpty()) {
      throw new IllegalStateException("Prices can not be empty");
    }
    return Collections.max(prices);
  }

  /**
   * Retrieves the lowest price ever recorded in this stock's price history.
   *
   * @return the minimum price across all recorded entries
   * @throws IllegalStateException if the price history is empty
   */
  public BigDecimal getLowestPrice() {
    if (prices.isEmpty()) {
      throw new IllegalStateException("Prices can not be empty");
    }
    return Collections.min(prices);
  }

  /**
   * Calculates the absolute price change between the two most recent prices.
   *
   * <p>Returns {@link BigDecimal#ZERO} if only one price has been recorded, as no
   * prior period exists for comparison.</p>
   *
   * @return the difference between the latest and the second-to-last recorded price
   * @throws IllegalStateException if the price history is empty
   */
  public BigDecimal getLatestPriceChange() {
    if (prices.isEmpty()) {
      throw new IllegalStateException("Stock can not have empty price list");
    }
    if (prices.size() == 1) {
      return BigDecimal.ZERO;
    }
    return prices.getLast().subtract(prices.get(prices.size() - 2));
  }

  /**
   * Calculates the percentage price change between the two most recent prices.
   *
   * <p>Returns {@link BigDecimal#ZERO} if only one price has been recorded. The result
   * is scaled to four decimal places using {@link RoundingMode#HALF_UP}.</p>
   *
   * @return the percentage change relative to the previous price
   * @throws IllegalStateException if the price history is empty
   */
  public BigDecimal getLatestPriceChangePercent() {
    if (prices.isEmpty()) {
      throw new IllegalStateException("Stock can not have empty price list");
    }
    if (prices.size() == 1) {
      return BigDecimal.ZERO;
    }
    BigDecimal previousPrice = prices.get(prices.size() - 2);
    return getLatestPriceChange()
        .divide(previousPrice, 4, RoundingMode.HALF_UP)
        .multiply(new BigDecimal("100"));
  }

  /**
   * Indicates whether this stock has at least two recorded prices, enabling trend calculations.
   *
   * @return {@code true} if the price history contains more than one entry; {@code false}
   *         otherwise
   */
  public boolean hasPriceHistory() {
    return prices.size() > 1;
  }

}
