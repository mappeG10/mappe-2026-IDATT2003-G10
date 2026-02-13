package edu.ntnu.idi.idatt.models;

public class Purchase extends Transaction {

  public Purchase(Share share, int week) {
    super(share, week, new PurchaseCalculator(share));
  }

  @Override
  public void commit(Player player) {
    // TODO: implement commit logic
  }

}
