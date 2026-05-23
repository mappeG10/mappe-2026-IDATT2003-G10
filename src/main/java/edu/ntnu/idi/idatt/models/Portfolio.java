package edu.ntnu.idi.idatt.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Portfolio {
  private final List<Share> shares;

  public Portfolio() {
    shares = new ArrayList<>();
  }

  public boolean addShare(Share share) {
    if (share == null) {
      return false;
    } // TODO: Should we check if share already exists in the portfolio?
    return shares.add(share);
  }

  public boolean reduceShare(Share share) {
    int index = shares.indexOf(share);
    if (index == -1) {
      return false;
    }

    BigDecimal remaining = share.getQuantity().subtract(share.getQuantity());
    if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
      return false;
    } else  {
      shares.set(index, new Share(share.getStock(), remaining, share.getPurchasePrice()));
    }
    return true;
  }

  public boolean removeShare(Share share) {
    return shares.remove(share);
  }
  public List<Share> getShares() {
    return shares;
  }
  public List<Share> getShares(String symbol){
    return this.shares.stream().filter(
        share -> share.getStock().getSymbol().equals(symbol)
    ).toList();
  }
  public boolean contatins(Share share) {
    return shares.contains(share);
  }


  public BigDecimal getNetWorth() {
    BigDecimal netWorth = BigDecimal.ZERO;

    for (Share share : shares) {
      netWorth = netWorth.add(share.getStock().getSalesPrice().multiply(share.getQuantity()));
    }

    return netWorth;
  }

  public BigDecimal getTotalInvested() {
    return shares.stream()
        .map(share -> share.getPurchasePrice().multiply(share.getQuantity()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }


}
