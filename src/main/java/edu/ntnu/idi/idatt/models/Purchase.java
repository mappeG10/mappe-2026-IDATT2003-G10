package edu.ntnu.idi.idatt.models;

public class Purchase extends Transaction {

  public Purchase(Share share, int week) {
    super(share, week, new PurchaseCalculator(share));
  }

  @Override
  public void commit(Player player) {

    if (isCommitted()) {
      return; // TODO: add custom exceptions here?
    }

    if (player.getMoney().compareTo(getCalculator().calculateTotal()) < 0) {
      return; // TODO: add custom exceptions here?
    }

    player.withdrawMoney(getCalculator().calculateTotal());
    player.getPortfolio().addShare(getShare());
    player.getTransactionArchive().add(this);

    // TODO: change committed flag to true if purchase is completed


  }

}
