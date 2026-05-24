package edu.ntnu.idi.idatt.models.transaction;

import edu.ntnu.idi.idatt.models.Player;
import edu.ntnu.idi.idatt.models.Share;
import edu.ntnu.idi.idatt.models.exceptions.InsufficientSharesException;
import edu.ntnu.idi.idatt.models.exceptions.TransactionAlreadyCommittedException;

public class Sale extends Transaction {

  public Sale (Share share, int week) {
    super(share, week, new SaleCalculator(share));
  }

  @Override
  public void commit(Player player) {

    if (isCommitted()) {
      throw new TransactionAlreadyCommittedException("Sale has already been committed");
    }

    if (!player.getPortfolio().reduceShare(getShare(), getShare().getQuantity())) {
      throw new InsufficientSharesException("Portfolio does not contain the required shares");
    }

    player.addMoney(getCalculator().calculateTotal());
    player.getTransactionArchive().add(this);
    player.updateStatus();

    this.setCommitted();

  }

  @Override
  public TransactionType getTransactionType() {
    return TransactionType.SALE;
  }

}
