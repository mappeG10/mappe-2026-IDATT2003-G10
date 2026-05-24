package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.model.Exchange;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Share;
import edu.ntnu.idi.idatt.model.Stock;
import java.math.BigDecimal;
import java.util.List;

public class DashboardController extends BaseController {

  public DashboardController(Exchange exchange, Player player) {
    super(exchange, player);
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

  public BigDecimal getTotalGainLoss() {
    return player.getNetWorth().subtract(player.getStartingMoney());
  }

  public BigDecimal getTotalGainLossPercent() {
    return player.getTotalGainLossPercent();
  }

  public void advanceWeek() {
    exchange.advance();
    player.updateStatus();
  }
}
