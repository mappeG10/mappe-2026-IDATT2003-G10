package edu.ntnu.idi.idatt.controllers;

import edu.ntnu.idi.idatt.models.Exchange;
import edu.ntnu.idi.idatt.models.Player;
import edu.ntnu.idi.idatt.models.Share;
import edu.ntnu.idi.idatt.models.Transaction;
import edu.ntnu.idi.idatt.models.TransactionCalculator;
import edu.ntnu.idi.idatt.models.TransactionFactory;
import edu.ntnu.idi.idatt.models.TransactionType;
import edu.ntnu.idi.idatt.view.GameObserver;
import java.math.BigDecimal;
import java.util.List;

public class PortfolioController {

  private final Exchange exchange;
  private final Player player;

  public PortfolioController(Exchange exchange, Player player) {
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

  public List<Share> getAllShares() {
    return player.getPortfolio().getShares();
  }

  public BigDecimal getNetWorth() {
    return player.getPortfolio().getNetWorth();
  }

  public BigDecimal getTotalInvested() {
    return player.getPortfolio().getTotalInvested();
  }

  public BigDecimal getUnrealisedPnL() {
    return player.getPortfolio().getNetWorth().subtract(
        player.getPortfolio().getTotalInvested()
    );
  }

  public int getPositionsCount() {
    return player.getPortfolio().getShares().size();
  }

  public TransactionPreview previewSell(Share share) {
    Transaction previewSell = TransactionFactory.createTransaction(
        TransactionType.SALE, share, exchange.getWeek());
    TransactionCalculator previewCalculator = previewSell.getCalculator();
    return new TransactionPreview(previewCalculator.calculateGross(),
        previewCalculator.calculateCommission(), previewCalculator.calculateTax(),
        previewCalculator.calculateTotal());
  }

  public Transaction executeSell(Share share) {
    Transaction sale = exchange.sell(share, player);
    player.updateStatus();
    return sale;
  }



}
