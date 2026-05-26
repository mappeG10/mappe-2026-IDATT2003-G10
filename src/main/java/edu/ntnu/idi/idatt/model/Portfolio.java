package edu.ntnu.idi.idatt.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Manages the collection of share positions held by a player.
 *
 * <p>The portfolio enforces the constraint that at most one position per stock symbol may
 * exist at any given time. When a share for a symbol that is already present in the portfolio
 * is added, the positions are merged using a quantity-weighted average purchase price. When a
 * position is fully sold, its entry is removed automatically.</p>
 */
public class Portfolio {

  private final List<Share> shares;

  /**
   * Constructs a new, empty portfolio.
   */
  public Portfolio() {
    shares = new ArrayList<>();
  }

  /**
   * Adds a share position to this portfolio.
   *
   * <p>If a position for the same stock symbol already exists, the two positions are merged
   * into a single entry using a quantity-weighted average purchase price. The original
   * {@link Share} object instance must not already be present in the portfolio.</p>
   *
   * @param share the share position to add; must not be {@code null} and must not be the
   *              exact same object instance as an existing entry
   * @return {@code true} if the portfolio was modified as a result of this call
   * @throws IllegalArgumentException if {@code share} is {@code null} or if the exact
   *                                  same object reference is already in the portfolio
   */
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

  /**
   * Merges two positions of the same stock into a single position with a weighted-average price.
   *
   * @param existing the position already in the portfolio
   * @param added    the new position being added
   * @return a new {@link Share} representing the combined position
   */
  private Share merge(Share existing, Share added) {
    BigDecimal totalQuantity = existing.getQuantity().add(added.getQuantity());

    BigDecimal totalCost = existing.getPurchasePrice().multiply(existing.getQuantity())
        .add(added.getPurchasePrice().multiply(added.getQuantity()));

    BigDecimal averagePrice = totalCost.divide(totalQuantity, MathContext.DECIMAL128);

    return new Share(existing.getStock(), totalQuantity, averagePrice);
  }

  /**
   * Reduces the quantity of a share position by the specified amount.
   *
   * <p>The position is looked up by stock symbol. If the resulting quantity would be exactly
   * zero, the position is removed entirely from the portfolio. If it would be negative,
   * no change is made and {@code false} is returned.</p>
   *
   * @param share  the share whose position should be reduced; matched by stock symbol
   * @param amount the quantity to subtract from the existing position; must be non-negative
   * @return {@code true} if the portfolio was modified; {@code false} if no matching
   *         position was found or the amount exceeds the held quantity
   */
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

  /**
   * Removes a specific share object from this portfolio by reference equality.
   *
   * @param share the share object to remove
   * @return {@code true} if the portfolio contained the specified share and it was removed;
   *         {@code false} otherwise
   */
  public boolean removeShare(Share share) {
    return shares.remove(share);
  }

  /**
   * Retrieves an unmodifiable view of all share positions currently held in this portfolio.
   *
   * @return an unmodifiable list of shares; never {@code null}, but may be empty
   */
  public List<Share> getShares() {
    return Collections.unmodifiableList(shares);
  }

  /**
   * Retrieves all share positions in this portfolio for a specific stock symbol.
   *
   * @param symbol the ticker symbol to filter by
   * @return a list of matching share positions; empty if none are found
   */
  public List<Share> getShares(String symbol) {
    return this.shares.stream().filter(
        share -> share.getStock().getSymbol().equals(symbol)
    ).toList();
  }

  /**
   * Indicates whether this portfolio contains the specified share object by reference equality.
   *
   * @param share the share to check for
   * @return {@code true} if the exact share object is present; {@code false} otherwise
   */
  public boolean contains(Share share) {
    return shares.contains(share);
  }

  /**
   * Calculates the total current market value of all positions in this portfolio.
   *
   * <p>Computed as the sum of {@code currentPrice × quantity} for each held position.</p>
   *
   * @return the total market value; {@link BigDecimal#ZERO} if the portfolio is empty
   */
  public BigDecimal getNetWorth() {
    return shares.stream()
        .map(share -> share.getStock().getSalesPrice().multiply(share.getQuantity()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Calculates the total original cost of all positions currently held in this portfolio.
   *
   * <p>Computed as the sum of {@code purchasePrice × quantity} for each position.</p>
   *
   * @return the total amount invested; {@link BigDecimal#ZERO} if the portfolio is empty
   */
  public BigDecimal getTotalInvested() {
    return shares.stream()
        .map(share -> share.getPurchasePrice().multiply(share.getQuantity()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Calculates the total unrealised profit or loss across all positions in this portfolio.
   *
   * <p>Computed as {@code netWorth - totalInvested}.</p>
   *
   * @return the unrealised gain (positive) or loss (negative);
   *         {@link BigDecimal#ZERO} if the portfolio is empty
   */
  public BigDecimal getUnrealisedPnL() {
    return getNetWorth().subtract(getTotalInvested());
  }
}
