package edu.ntnu.idi.idatt.controllers.contentcontroller;

import edu.ntnu.idi.idatt.controllers.BaseController;
import edu.ntnu.idi.idatt.models.Exchange;
import edu.ntnu.idi.idatt.models.Player;
import edu.ntnu.idi.idatt.models.transaction.Transaction;

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
