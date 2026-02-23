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

    if (!player.getPortfolio().contatins(getShare())) {
      return; // TODO: add custom exceptions here?
    }


    player.addMoney(getCalculator().calculateTotal());

    player.getPortfolio().removeShare(getShare());

    player.getTransactionArchive().add(this);

    // TODO: change committed flag to true if sale is completed
  }

}
