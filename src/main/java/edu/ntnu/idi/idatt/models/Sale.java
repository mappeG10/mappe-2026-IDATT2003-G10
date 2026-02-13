package edu.ntnu.idi.idatt.models;

public class Sale extends Transaction {

  public Sale (Share share, int week) {
    super(share, week, new SaleCalculator(share));
  }

  @Override
  public void commit(Player player) {
    // TODO: implement commit logic
  }

}
