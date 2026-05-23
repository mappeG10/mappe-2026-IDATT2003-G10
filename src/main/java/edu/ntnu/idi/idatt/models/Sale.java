package edu.ntnu.idi.idatt.models;

public class Sale extends Transaction {

  public Sale (Share share, int week) {
    super(share, week, new SaleCalculator(share));
  }

  @Override
  public void commit(Player player) {

    if (isCommitted()) {
      return; // TODO: add custom exception here?
    }

    if (!player.getPortfolio().reduceShare(getShare(), getShare().getQuantity())) {
      return; // TODO: add custom exceptions here?
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
