package edu.ntnu.idi.idatt.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Portfolio {
  private final List<Share> shares;

  public Portfolio() {
    shares = new ArrayList<>();
  }

  public boolean addShare(Share share) {
    if (share == null) {
      throw new IllegalArgumentException("Share cannot be null");
    }

    if (shares.contains(share)) {
      throw new IllegalArgumentException("Cannot add duplicate share object");
    }

    Optional<Share> existingOpt = shares.stream()
        .filter(s -> s.getStock().getSymbol().equals(share.getStock().getSymbol()))
        .findFirst();

    if (existingOpt.isPresent()) {
      Share existing = existingOpt.get();
      Share mergedPosition = merge(existing, share);
      shares.remove(existing);
      return shares.add(mergedPosition);
    }

    return shares.add(share);
  }

  private Share merge(Share existing, Share added) {
    BigDecimal totalQuantity = existing.getQuantity().add(added.getQuantity());

    BigDecimal totalCost = existing.getPurchasePrice().multiply(existing.getQuantity())
        .add(added.getPurchasePrice().multiply(added.getQuantity()));

    BigDecimal averagePrice = totalCost.divide(totalQuantity, MathContext.DECIMAL128);

    return new Share(existing.getStock(), totalQuantity, averagePrice);
  }

  public boolean reduceShare(Share share, BigDecimal amount) {
    Share found = shares.stream()
        .filter(s -> s.getStock().getSymbol().equals(share.getStock().getSymbol()))
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
      shares.set(shares.indexOf(found),
          new Share(found.getStock(), remaining, found.getPurchasePrice()));
    }
    return true;
  }

  public boolean removeShare(Share share) {
    return shares.remove(share);
  }

  public List<Share> getShares() {
    return Collections.unmodifiableList(shares);
  }
  public List<Share> getShares(String symbol){
    return this.shares.stream().filter(
        share -> share.getStock().getSymbol().equals(symbol)
    ).toList();
  }
  public boolean contains(Share share) {
    return shares.contains(share);
  }


  public BigDecimal getNetWorth() {
    return shares.stream()
        .map(share -> share.getStock().getSalesPrice().multiply(share.getQuantity()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public BigDecimal getTotalInvested() {
    return shares.stream()
        .map(share -> share.getPurchasePrice().multiply(share.getQuantity()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public BigDecimal getUnrealisedPnL() {
    return getNetWorth().subtract(getTotalInvested());
  }


}
