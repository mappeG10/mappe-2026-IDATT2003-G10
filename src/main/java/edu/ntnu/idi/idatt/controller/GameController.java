package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.model.Exchange;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.observer.GameObserver;
import java.math.BigDecimal;

public class GameController {

  private final Exchange exchange;
  private final Player player;
  private final DashboardController dashboardController;
  private final MarketController marketController;
  private final TransactionHistoryController transactionHistoryController;
  private final PortfolioController portfolioController;

  public GameController(Exchange exchange, Player player) {
    this.exchange = exchange;
    this.player = player;
    this.dashboardController = new DashboardController(exchange, player);
    this.marketController =  new MarketController(exchange, player);
    this.transactionHistoryController = new TransactionHistoryController(exchange, player);
    this.portfolioController = new PortfolioController(exchange, player);

  }

  public void registerObserver(GameObserver observer) {
    exchange.register(observer);
    player.register(observer);
  }

  public void unregisterObserver(GameObserver observer) {
    exchange.unregister(observer);
    player.unregister(observer);
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
