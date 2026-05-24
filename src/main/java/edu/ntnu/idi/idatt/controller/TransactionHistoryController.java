package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.model.Exchange;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.transaction.Transaction;

import java.util.List;

public class TransactionHistoryController extends BaseController {

  public TransactionHistoryController(Exchange exchange, Player player) {
    super(exchange, player);
  }

  public List<Transaction> getTransactions(int week) {
    return player.getTransactionArchive().getTransactions(week);
  }

  public List<Integer> getDistinctWeeks() {
    return player.getTransactionArchive().getDistinctWeeksAsList();
  }










}
