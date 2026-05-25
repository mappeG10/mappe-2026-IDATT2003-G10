package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.model.Exchange;
import edu.ntnu.idi.idatt.model.Player;
import java.math.BigDecimal;

public class GameController extends BaseController{

  private final DashboardController dashboardController;
  private final MarketController marketController;
  private final TransactionHistoryController transactionHistoryController;
  private final PortfolioController portfolioController;
  private final SummaryController summaryController;

  public GameController(Exchange exchange, Player player) {
    super(exchange, player);
    this.dashboardController = new DashboardController(exchange, player);
    this.marketController =  new MarketController(exchange, player);
    this.transactionHistoryController = new TransactionHistoryController(exchange, player);
    this.portfolioController = new PortfolioController(exchange, player);
    this.summaryController = new SummaryController(exchange, player);
  }

  public DashboardController getDashboardController() {
    return dashboardController;
  }

  public MarketController getMarketController() {
    return marketController;
  }

  public TransactionHistoryController getTransactionHistoryController() {
    return transactionHistoryController;
  }

  public PortfolioController getPortfolioController() {
    return portfolioController;
  }

  public SummaryController getSummaryController() {
    return summaryController;
  }

  public int getCurrentWeek() {
    return exchange.getWeek();
  }

  public String getPlayerName() {
    return player.getName();
  }

  public BigDecimal getPlayerMoney() {
    return player.getMoney();
  }

  public String getPlayerStatus() {
    return player.getStatus().name();
  }

}
