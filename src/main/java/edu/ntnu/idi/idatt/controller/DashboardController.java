package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.model.Exchange;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Share;
import edu.ntnu.idi.idatt.model.Stock;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller providing data and actions for the dashboard view.
 *
 * <p>The dashboard is the primary overview screen, displaying the player's current financial
 * position, portfolio composition, top market movers, and controls to advance the game by one week.
 */
public class DashboardController extends BaseController {

  /**
   * Constructs a new {@code DashboardController} for the given exchange and player.
   *
   * @param exchange the stock exchange for this game session; must not be {@code null}
   * @param player the player for this game session; must not be {@code null}
   */
  public DashboardController(Exchange exchange, Player player) {
    super(exchange, player);
  }

  /**
   * Retrieves the player's current cash balance.
   *
   * @return the available cash; never {@code null}
   */
  public BigDecimal getPlayerMoney() {
    return player.getMoney();
  }

  /**
   * Retrieves the player's total net worth, including both cash and portfolio market value.
   *
   * @return the total net worth; never {@code null}
   */
  public BigDecimal getNetWorth() {
    return player.getNetWorth();
  }

  /**
   * Retrieves the cash balance the player started the game with.
   *
   * @return the starting capital; never {@code null}
   */
  public BigDecimal getStartingCapital() {
    return player.getStartingMoney();
  }

  /**
   * Retrieves the current total market value of all positions in the player's portfolio.
   *
   * @return the portfolio market value; never {@code null}
   */
  public BigDecimal getPortfolioValue() {
    return player.getPortfolio().getNetWorth();
  }

  /**
   * Retrieves all share positions currently held in the player's portfolio.
   *
   * @return an unmodifiable list of shares; never {@code null}, but may be empty
   */
  public List<Share> getAllSharesFromPortfolio() {
    return player.getPortfolio().getShares();
  }

  /**
   * Retrieves the top-performing stocks on the exchange for the current week.
   *
   * @param limit the maximum number of stocks to return; must be at least 1
   * @return a list of up to {@code limit} stocks sorted by their latest percentage gain
   * @throws IllegalArgumentException if {@code limit} is less than 1
   */
  public List<Stock> getGainers(int limit) {
    return exchange.getGainers(limit);
  }

  /**
   * Retrieves the worst-performing stocks on the exchange for the current week.
   *
   * @param limit the maximum number of stocks to return; must be at least 1
   * @return a list of up to {@code limit} stocks sorted by their latest percentage decline
   * @throws IllegalArgumentException if {@code limit} is less than 1
   */
  public List<Stock> getLosers(int limit) {
    return exchange.getLosers(limit);
  }

  /**
   * Retrieves the absolute gain or loss of the player relative to their starting capital.
   *
   * @return the total gain (positive) or loss (negative); never {@code null}
   */
  public BigDecimal getTotalGainLoss() {
    return player.getTotalGainLoss();
  }

  /**
   * Retrieves the percentage gain or loss of the player relative to their starting capital.
   *
   * @return the percentage gain (positive) or loss (negative); never {@code null}
   */
  public BigDecimal getTotalGainLossPercent() {
    return player.getTotalGainLossPercent();
  }

  /**
   * Advances the game by one week, randomising stock prices and updating the player's status.
   *
   * <p>Calling this method triggers observer notifications on both the exchange and the player.
   */
  public void advanceWeek() {
    exchange.advance();
    player.updateStatus();
  }
}
