package edu.ntnu.idi.idatt.model.transaction;

import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Share;
import edu.ntnu.idi.idatt.model.exception.InsufficientSharesException;
import edu.ntnu.idi.idatt.model.exception.TransactionAlreadyCommittedException;

/**
 * Represents a stock-sale transaction executed by a player.
 *
 * <p>When committed, this transaction reduces the sold share position in the player's portfolio,
 * credits the net proceeds (gross - commission - tax) to the player's cash balance, archives the
 * transaction, and triggers a player-status re-evaluation.
 */
public class Sale extends Transaction {

  /**
   * Constructs a new sale transaction for the given share and game week.
   *
   * @param share the share to be sold; must not be {@code null}
   * @param week the game week in which the sale takes place; must be at least 1
   * @throws IllegalArgumentException if {@code share} is {@code null} or {@code week} is less than
   *     1
   */
  public Sale(Share share, int week) {
    super(share, week, new SaleCalculator(share));
  }

  /**
   * Commits this sale against the given player's account.
   *
   * <p>The following steps are performed atomically:
   *
   * <ol>
   *   <li>Verifies the transaction has not already been committed.
   *   <li>Reduces the share quantity in the player's portfolio by the sold amount.
   *   <li>Credits the net proceeds to the player's cash balance.
   *   <li>Archives this transaction in the player's transaction history.
   *   <li>Re-evaluates the player's status based on updated activity.
   * </ol>
   *
   * @param player the player executing the sale; must not be {@code null}
   * @throws TransactionAlreadyCommittedException if this sale has already been committed
   * @throws InsufficientSharesException if the player's portfolio does not contain enough shares to
   *     satisfy the sale quantity
   */
  @Override
  public void commit(Player player) {

    if (isCommitted()) {
      throw new TransactionAlreadyCommittedException("Sale has already been committed");
    }

    if (!player.reduceShareInPortfolio(getShare(), getShare().getQuantity())) {
      throw new InsufficientSharesException("Portfolio does not contain the required shares");
    }

    player.addMoney(getCalculator().calculateTotal());
    player.archiveTransaction(this);
    player.updateStatus();

    this.setCommitted();
  }

  /**
   * Retrieves the transaction type identifier for a sale.
   *
   * @return {@link TransactionType#SALE}
   */
  @Override
  public TransactionType getTransactionType() {
    return TransactionType.SALE;
  }
}
