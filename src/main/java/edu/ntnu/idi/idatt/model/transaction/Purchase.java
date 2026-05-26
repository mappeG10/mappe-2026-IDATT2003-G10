package edu.ntnu.idi.idatt.model.transaction;

import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Share;
import edu.ntnu.idi.idatt.model.exception.InsufficientFundsException;
import edu.ntnu.idi.idatt.model.exception.TransactionAlreadyCommittedException;

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
    player.addShareToPortfolio(getShare());
    player.archiveTransaction(this);
    player.updateStatus();

    this.setCommitted();


  }

  @Override
  public TransactionType getTransactionType() {
    return TransactionType.PURCHASE;
  }

}
