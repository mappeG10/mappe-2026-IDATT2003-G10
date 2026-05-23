package edu.ntnu.idi.idatt.controllers;

import edu.ntnu.idi.idatt.models.Exchange;
import edu.ntnu.idi.idatt.models.Player;
import edu.ntnu.idi.idatt.models.Transaction;
import edu.ntnu.idi.idatt.view.GameObserver;
import java.util.List;

public class TransactionHistoryController {

  private final Exchange exchange;
  private final Player player;

  public TransactionHistoryController(Exchange exchange, Player player) {
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

  public List<Transaction> getTransactions(int week) {
    return player.getTransactionArchive().getTransactions(week);
  }

  public List<Transaction> getAllTransactions() {
    return player.getTransactionArchive().getAllTransactions();
  }










}
