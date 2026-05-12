package edu.ntnu.idi.idatt.controllers;

import edu.ntnu.idi.idatt.models.Exchange;
import edu.ntnu.idi.idatt.models.Player;
import edu.ntnu.idi.idatt.view.GameObserver;
import java.math.BigDecimal;

public class GameController {

  private final Exchange exchange;
  private final Player player;
  private final MarketController marketController;
  private final TransactionController transactionController;
  private final PortfolioController portfolioController;

  public GameController(Exchange exchange, Player player) {
    this.exchange = exchange;
    this.player = player;
    this.marketController =  new MarketController(exchange, player);
    this.transactionController = new TransactionController(exchange, player);
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

  public MarketController getMarketController() {
    return marketController;
  }

  public TransactionController getTransactionController() {
    return transactionController;
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

  public BigDecimal getNetWorth() {
    return player.getNetWorth();
  }

  public Player.Status getStatus() {
    return player.getStatus();
  }

  public void advanceWeek() {
    exchange.advance();
    player.updateStatus();
  }
}
