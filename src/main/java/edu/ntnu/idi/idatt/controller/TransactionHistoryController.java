package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.model.Exchange;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.transaction.Transaction;
import java.util.List;

/**
 * Controller providing data for the transaction history view.
 *
 * <p>Exposes the player's committed transaction archive in a week-indexed format,
 * enabling the view to render transactions grouped by the game week in which they
 * occurred.</p>
 */
public class TransactionHistoryController extends BaseController {

  /**
   * Constructs a new {@code TransactionHistoryController} for the given exchange and player.
   *
   * @param exchange the stock exchange for this game session; must not be {@code null}
   * @param player   the player for this game session; must not be {@code null}
   */
  public TransactionHistoryController(Exchange exchange, Player player) {
    super(exchange, player);
  }

  /**
   * Retrieves all committed transactions that occurred in the specified game week.
   *
   * @param week the week number for which to retrieve transactions
   * @return a list of {@link Transaction}s from the given week; empty if none occurred
   */
  public List<Transaction> getTransactions(int week) {
    return player.getTransactionArchive().getTransactions(week);
  }

  /**
   * Retrieves a sorted list of distinct weeks in which at least one transaction was committed.
   *
   * @return a sorted list of week numbers with transaction activity; empty if no transactions
   *         have been committed
   */
  public List<Integer> getDistinctWeeks() {
    return player.getTransactionArchive().getDistinctWeeksAsList();
  }

}
