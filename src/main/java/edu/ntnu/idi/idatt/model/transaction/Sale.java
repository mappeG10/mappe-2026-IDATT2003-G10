package edu.ntnu.idi.idatt.model.transaction;

import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Share;
import edu.ntnu.idi.idatt.model.exception.InsufficientSharesException;
import edu.ntnu.idi.idatt.model.exception.TransactionAlreadyCommittedException;

public class Sale extends Transaction {

  public Sale (Share share, int week) {
    super(share, week, new SaleCalculator(share));
  }

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

  @Override
  public TransactionType getTransactionType() {
    return TransactionType.SALE;
  }

}
