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
      throw new IllegalArgumentException("Share cannot be null");
    }
    return shares.add(share);
  }

  public boolean reduceShare(Share share, BigDecimal amount) {
    Share found = shares.stream()
        .filter(stock -> stock.getStock() == share.getStock() &&
            stock.getPurchasePrice().compareTo(share.getPurchasePrice()) == 0)
        .findFirst()
        .orElse(null);
    if (found == null) {
      return false;
    }

    BigDecimal remaining = found.getQuantity().subtract(amount);
    if (remaining.compareTo(BigDecimal.ZERO) < 0) {
      return false;
    }

    if (remaining.compareTo(BigDecimal.ZERO) == 0) {
      shares.remove(found);
    } else {
      shares.set(shares.indexOf(found), new Share(found.getStock(), remaining, found.getPurchasePrice()));
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
