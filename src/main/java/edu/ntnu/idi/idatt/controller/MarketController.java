package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.controller.dto.TransactionPreview;
import edu.ntnu.idi.idatt.controller.dto.TransactionReceipt;
import edu.ntnu.idi.idatt.model.Exchange;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Share;
import edu.ntnu.idi.idatt.model.Stock;
import edu.ntnu.idi.idatt.model.transaction.Transaction;
import edu.ntnu.idi.idatt.model.transaction.TransactionCalculator;
import edu.ntnu.idi.idatt.model.transaction.TransactionFactory;
import edu.ntnu.idi.idatt.model.transaction.TransactionType;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller providing data and actions for the market view.
 *
 * <p>Exposes the ability to browse and search all listed stocks, preview the cost of a
 * potential purchase without committing it, and execute a buy order against the player's
 * account.</p>
 */
public class MarketController extends BaseController {

  /**
   * Constructs a new {@code MarketController} for the given exchange and player.
   *
   * @param exchange the stock exchange for this game session; must not be {@code null}
   * @param player   the player for this game session; must not be {@code null}
   */
  public MarketController(Exchange exchange, Player player) {
    super(exchange, player);
  }

  /**
   * Retrieves all stocks currently listed on the exchange.
   *
   * @return a list of all listed {@link Stock} instances; never {@code null}
   */
  public List<Stock> getAllStocks() {
    return exchange.getAllStocks();
  }

  /**
   * Searches the exchange for stocks whose symbol or company name matches the given term.
   *
   * @param searchTerm the text to search for; matched case-insensitively against both the
   *                   ticker symbol (exact) and the company name (substring)
   * @return a list of matching stocks; empty if no stocks match
   */
  public List<Stock> findStocks(String searchTerm) {
    return exchange.findStock(searchTerm);
  }

  /**
   * Calculates a cost breakdown for a hypothetical purchase without committing the transaction.
   *
   * <p>The preview is computed using the stock's current market price and includes the gross
   * value, broker commission, tax (always zero for purchases), and the total cost.</p>
   *
   * @param symbol   the ticker symbol of the stock to preview
   * @param quantity the number of shares to include in the preview; must be positive
   * @return a {@link TransactionPreview} containing the cost breakdown
   * @throws edu.ntnu.idi.idatt.model.exception.StockNotFoundException if no stock with the
   *         given symbol is listed on the exchange
   */
  public TransactionPreview previewBuy(String symbol, BigDecimal quantity) {
    Stock stock = exchange.getStock(symbol);
    Share sharePreview = new Share(stock, quantity, stock.getSalesPrice());
    Transaction previewPurchase = TransactionFactory.createTransaction(
        TransactionType.PURCHASE, sharePreview, exchange.getWeek());
    TransactionCalculator previewCalculator = previewPurchase.getCalculator();
    return new TransactionPreview(
        previewCalculator.calculateGross(),
        previewCalculator.calculateCommission(),
        previewCalculator.calculateTax(),
        previewCalculator.calculateTotal());
  }

  /**
   * Executes a buy order for the specified stock and quantity on behalf of the player.
   *
   * @param symbol   the ticker symbol of the stock to purchase
   * @param quantity the number of shares to buy; must be positive
   * @return a {@link TransactionReceipt} summarising the committed purchase
   * @throws edu.ntnu.idi.idatt.model.exception.StockNotFoundException   if no stock with
   *         the given symbol is listed on the exchange
   * @throws edu.ntnu.idi.idatt.model.exception.InsufficientFundsException if the player's
   *         balance is lower than the total purchase cost
   */
  public TransactionReceipt executeBuy(String symbol, BigDecimal quantity) {
    return TransactionReceipt.from(exchange.buy(symbol, quantity, player));
  }
}
