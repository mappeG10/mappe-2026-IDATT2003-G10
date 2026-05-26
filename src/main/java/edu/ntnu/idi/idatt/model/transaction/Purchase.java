package edu.ntnu.idi.idatt.model.transaction;

import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Share;
import edu.ntnu.idi.idatt.model.exception.InsufficientFundsException;
import edu.ntnu.idi.idatt.model.exception.TransactionAlreadyCommittedException;

/**
 * Represents a stock-purchase transaction executed by a player.
 *
 * <p>When committed, this transaction deducts the total cost (gross + commission) from the player's
 * cash balance, adds the purchased share to the player's portfolio, archives the transaction, and
 * triggers a player-status re-evaluation.
 */
public class Purchase extends Transaction {

  /**
   * Constructs a new purchase transaction for the given share and game week.
   *
   * @param share the share to be purchased; must not be {@code null}
   * @param week the game week in which the purchase takes place; must be at least 1
   * @throws IllegalArgumentException if {@code share} is {@code null} or {@code week} is less than
   *     1
   */
  public Purchase(Share share, int week) {
    super(share, week, new PurchaseCalculator(share));
  }

  /**
   * Commits this purchase against the given player's account.
   *
   * <p>The following steps are performed atomically:
   *
   * <ol>
   *   <li>Verifies the transaction has not already been committed.
   *   <li>Verifies the player's cash balance covers the total cost.
   *   <li>Withdraws the total cost from the player's balance.
   *   <li>Adds the share to the player's portfolio.
   *   <li>Archives this transaction in the player's transaction history.
   *   <li>Re-evaluates the player's status based on updated activity.
   * </ol>
   *
   * @param player the player executing the purchase; must not be {@code null}
   * @throws TransactionAlreadyCommittedException if this purchase has already been committed
   * @throws InsufficientFundsException if the player's balance is lower than the total purchase
   *     price
   */
  @Override
  public void commit(Player player) {

    if (isCommitted()) {
      throw new TransactionAlreadyCommittedException("Purchase has already been committed");
    }

    if (player.getMoney().compareTo(getCalculator().calculateTotal()) < 0) {
      throw new InsufficientFundsException(
          "Insufficient funds. Required: "
              + getCalculator().calculateTotal()
              + ", available: "
              + player.getMoney());
    }

    player.withdrawMoney(getCalculator().calculateTotal());
    player.addShareToPortfolio(getShare());
    player.archiveTransaction(this);
    player.updateStatus();

    this.setCommitted();
  }

  /**
   * Retrieves the transaction type identifier for a purchase.
   *
   * @return {@link TransactionType#PURCHASE}
   */
  @Override
  public TransactionType getTransactionType() {
    return TransactionType.PURCHASE;
  }
}
