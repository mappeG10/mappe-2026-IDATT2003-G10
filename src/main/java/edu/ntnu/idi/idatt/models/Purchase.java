package edu.ntnu.idi.idatt.models;

import edu.ntnu.idi.idatt.models.exceptions.InsufficientFundsException;
import edu.ntnu.idi.idatt.models.exceptions.TransactionAlreadyCommittedException;

public class Purchase extends Transaction {

  public Purchase(Share share, int week) {
    super(share, week, new PurchaseCalculator(share));
  }

  @Override
  public void commit(Player player) {

    if (isCommitted()) {
      throw new TransactionAlreadyCommittedException("Purchase has already been committed");
    }

    if (player.getMoney().compareTo(getCalculator().calculateTotal()) < 0) {
      throw new InsufficientFundsException(
          "Insufficient funds. Required: " + getCalculator().calculateTotal()
              + ", available: " + player.getMoney());
    }

    player.withdrawMoney(getCalculator().calculateTotal());
    player.getPortfolio().addShare(getShare());
    player.getTransactionArchive().add(this);
    player.updateStatus();

    this.setCommitted();


  }

  @Override
  public TransactionType getTransactionType() {
    return TransactionType.PURCHASE;
  }

}
