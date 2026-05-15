package edu.ntnu.idi.idatt.controllers;

import edu.ntnu.idi.idatt.models.Exchange;
import edu.ntnu.idi.idatt.models.Player;
import edu.ntnu.idi.idatt.models.Share;
import edu.ntnu.idi.idatt.models.Stock;
import edu.ntnu.idi.idatt.view.GameObserver;
import java.math.BigDecimal;
import java.util.List;

public class DashboardController {

  private final Exchange exchange;
  private final Player player;


  public DashboardController(Exchange exchange, Player player) {
    this.exchange = exchange;
    this.player = player;
  }

  public void registerObserver(GameObserver observer) {
    exchange.register(observer);
    player.register(observer);
  }

  public void unregisterObserver(GameObserver observer) {
    exchange.unregister(observer);
    player.unregister(observer);
  }



  public BigDecimal getPlayerMoney() {
    return player.getMoney();
  }

  public BigDecimal getNetWorth() {
    return player.getNetWorth();
  }

  public BigDecimal getStartingCapital() {
    return player.getStartingMoney();
  }

  public BigDecimal getPortfolioValue() {
    return player.getPortfolio().getNetWorth();
  }

  public List<Share> getAllSharesFromPortfolio() {
    return player.getPortfolio().getShares();
  }

  public List<Stock> getGainers(int limit) {
    return exchange.getGainers(limit);
  }

  public List<Stock> getLosers(int limit) {
    return exchange.getLosers(limit);
  }

  public void advanceWeek() {
    exchange.advance();
    player.updateStatus();
  }
}
