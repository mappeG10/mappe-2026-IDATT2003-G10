package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.dal.JsonGameWriter;
import edu.ntnu.idi.idatt.dal.dto.GameStateDto;
import edu.ntnu.idi.idatt.dal.exception.DataAccessException;
import edu.ntnu.idi.idatt.dal.mapper.GameMapper;
import edu.ntnu.idi.idatt.model.Exchange;
import edu.ntnu.idi.idatt.model.Player;
import java.math.BigDecimal;

/**
 * Top-level controller that orchestrates the entire game session.
 *
 * <p>Acts as the facade through which the view layer accesses all game functionality.
 * It owns and exposes dedicated sub-controllers for each major feature area
 * (dashboard, market, portfolio, transaction history, and end-game summary), and
 * provides cross-cutting concerns such as persisting the game state to disk.</p>
 */
public class GameController extends BaseController {

  private final DashboardController dashboardController;
  private final MarketController marketController;
  private final TransactionHistoryController transactionHistoryController;
  private final PortfolioController portfolioController;
  private final SummaryController summaryController;

  /**
   * Constructs a new {@code GameController} for the given exchange and player, and
   * initialises all sub-controllers.
   *
   * @param exchange the stock exchange for this game session; must not be {@code null}
   * @param player   the player for this game session; must not be {@code null}
   */
  public GameController(Exchange exchange, Player player) {
    super(exchange, player);
    this.dashboardController = new DashboardController(exchange, player);
    this.marketController = new MarketController(exchange, player);
    this.transactionHistoryController = new TransactionHistoryController(exchange, player);
    this.portfolioController = new PortfolioController(exchange, player);
    this.summaryController = new SummaryController(exchange, player);
  }

  /**
   * Serialises the current game state and writes it to the specified file path.
   *
   * @param path the absolute or relative destination path for the save file; must not be
   *             {@code null} or blank
   * @throws DataAccessException if the game state cannot be written to the specified path
   */
  public void save(String path) throws DataAccessException {
    GameStateDto gameStateDto = GameMapper.toDto(player, exchange);
    JsonGameWriter writer = new JsonGameWriter();
    writer.write(path, gameStateDto);
  }

  /**
   * Retrieves the sub-controller responsible for dashboard view data and actions.
   *
   * @return the {@link DashboardController}; never {@code null}
   */
  public DashboardController getDashboardController() {
    return dashboardController;
  }

  /**
   * Retrieves the sub-controller responsible for market browsing and purchase execution.
   *
   * @return the {@link MarketController}; never {@code null}
   */
  public MarketController getMarketController() {
    return marketController;
  }

  /**
   * Retrieves the sub-controller responsible for displaying the transaction history.
   *
   * @return the {@link TransactionHistoryController}; never {@code null}
   */
  public TransactionHistoryController getTransactionHistoryController() {
    return transactionHistoryController;
  }

  /**
   * Retrieves the sub-controller responsible for portfolio view data and sale execution.
   *
   * @return the {@link PortfolioController}; never {@code null}
   */
  public PortfolioController getPortfolioController() {
    return portfolioController;
  }

  /**
   * Retrieves the sub-controller responsible for finalising the game and producing a summary.
   *
   * @return the {@link SummaryController}; never {@code null}
   */
  public SummaryController getSummaryController() {
    return summaryController;
  }

  /**
   * Retrieves the current game week from the exchange.
   *
   * @return the current week number; always at least 1
   */
  public int getCurrentWeek() {
    return exchange.getWeek();
  }

  /**
   * Retrieves the display name of the player.
   *
   * @return the player's name; never {@code null} or blank
   */
  public String getPlayerName() {
    return player.getName();
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
   * Retrieves the name of the player's current activity status.
   *
   * @return the string name of the {@link edu.ntnu.idi.idatt.model.Player.Status} enum
   *         constant
   */
  public String getPlayerStatus() {
    return player.getStatus().name();
  }

}
