package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.controller.dto.TransactionPreview;
import edu.ntnu.idi.idatt.controller.dto.TransactionReceipt;
import edu.ntnu.idi.idatt.model.Exchange;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Share;
import edu.ntnu.idi.idatt.model.transaction.Transaction;
import edu.ntnu.idi.idatt.model.transaction.TransactionCalculator;
import edu.ntnu.idi.idatt.model.transaction.TransactionFactory;
import edu.ntnu.idi.idatt.model.transaction.TransactionType;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller providing data and actions for the portfolio view.
 *
 * <p>Exposes the player's current holdings and related financial metrics, and provides
 * the ability to preview the proceeds of a potential sale and execute sell orders.</p>
 */
public class PortfolioController extends BaseController {

  /**
   * Constructs a new {@code PortfolioController} for the given exchange and player.
   *
   * @param exchange the stock exchange for this game session; must not be {@code null}
   * @param player   the player for this game session; must not be {@code null}
   */
  public PortfolioController(Exchange exchange, Player player) {
    super(exchange, player);
  }

  /**
   * Retrieves all share positions currently held in the player's portfolio.
   *
   * @return an unmodifiable list of shares; never {@code null}, but may be empty
   */
  public List<Share> getAllShares() {
    return player.getPortfolio().getShares();
  }

  /**
   * Retrieves the total current market value of all positions in the portfolio.
   *
   * @return the portfolio market value; never {@code null}
   */
  public BigDecimal getNetWorth() {
    return player.getPortfolio().getNetWorth();
  }

  /**
   * Retrieves the total original cost of all positions currently held in the portfolio.
   *
   * @return the total amount invested; never {@code null}
   */
  public BigDecimal getTotalInvested() {
    return player.getPortfolio().getTotalInvested();
  }

  /**
   * Retrieves the total unrealised profit or loss across all positions in the portfolio.
   *
   * @return the unrealised gain (positive) or loss (negative); never {@code null}
   */
  public BigDecimal getUnrealisedPnL() {
    return player.getPortfolio().getUnrealisedPnL();
  }

  /**
   * Retrieves the number of distinct stock positions currently held in the portfolio.
   *
   * @return the number of open positions; zero if the portfolio is empty
   */
  public int getPositionsCount() {
    return player.getPortfolio().getShares().size();
  }

  /**
   * Calculates a proceeds breakdown for a hypothetical sale without committing the transaction.
   *
   * <p>The preview is computed using the stock's current market price and includes the gross
   * proceeds, broker commission, capital-gains tax (if applicable), and net total.</p>
   *
   * @param share    the portfolio share position from which to simulate the sale
   * @param quantity the number of shares to include in the preview; must be positive and
   *                 not greater than the quantity held
   * @return a {@link TransactionPreview} containing the proceeds breakdown
   * @throws IllegalArgumentException if {@code share} or {@code quantity} is {@code null},
   *                                  or if {@code quantity} is not positive
   */
  public TransactionPreview previewSell(Share share, BigDecimal quantity) {
    Share previewShare = new Share(share.getStock(), quantity, share.getPurchasePrice());
    Transaction previewSell = TransactionFactory.createTransaction(
        TransactionType.SALE, previewShare, exchange.getWeek());
    TransactionCalculator previewCalculator = previewSell.getCalculator();
    return new TransactionPreview(
        previewCalculator.calculateGross(),
        previewCalculator.calculateCommission(),
        previewCalculator.calculateTax(),
        previewCalculator.calculateTotal());
  }

  /**
   * Executes a sell order for the specified share position and quantity on behalf of the player.
   *
   * @param share    the portfolio share position to sell from
   * @param quantity the number of shares to sell; must be positive and not greater than
   *                 the quantity held
   * @return a {@link TransactionReceipt} summarising the committed sale
   * @throws edu.ntnu.idi.idatt.model.exception.InsufficientSharesException if the portfolio
   *         does not contain enough shares to satisfy the requested quantity
   */
  public TransactionReceipt executeSell(Share share, BigDecimal quantity) {
    return TransactionReceipt.from(exchange.sell(share, quantity, player));
  }
}
