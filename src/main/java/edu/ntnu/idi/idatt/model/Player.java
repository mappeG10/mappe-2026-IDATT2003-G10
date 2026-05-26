package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.transaction.Transaction;
import edu.ntnu.idi.idatt.model.transaction.TransactionArchive;
import edu.ntnu.idi.idatt.observer.GameObserver;
import edu.ntnu.idi.idatt.observer.GameSubject;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player participating in the stock-market simulation game.
 *
 * <p>A player holds a cash balance, a {@link Portfolio} of share positions, and a
 * {@link TransactionArchive} that records every trade. The player's activity level is
 * summarised by a {@link Status} that is re-evaluated after each committed transaction,
 * based on how many distinct weeks they have traded and how much profit they have earned.</p>
 *
 * <p>The player implements {@link GameSubject}, so UI components can register as
 * {@link GameObserver}s and refresh automatically whenever the player's state changes.</p>
 */
public class Player implements GameSubject {

  /**
   * Describes the player's level of market activity and profitability.
   *
   * <ul>
   *   <li>{@link #NOVICE} — the default starting status.</li>
   *   <li>{@link #INVESTOR} — awarded after trading for at least 10 distinct weeks with
   *       a profit of at least 20%.</li>
   *   <li>{@link #SPECULATOR} — awarded after trading for at least 20 distinct weeks with
   *       a profit of at least 100%.</li>
   * </ul>
   */
  public enum Status {
    /** Default status assigned to new players with limited trading history or profit. */
    NOVICE,
    /** Awarded to players who have traded actively and achieved moderate profitability. */
    INVESTOR,
    /** Awarded to players with an extensive trading history and exceptional profitability. */
    SPECULATOR
  }

  private final String name;
  private final BigDecimal startingMoney;
  private BigDecimal money;
  private final Portfolio portfolio;
  private final TransactionArchive transactionArchive;

  private Status status;

  private final List<GameObserver> observers = new ArrayList<>();

  /**
   * {@inheritDoc}
   *
   * <p>Duplicate registrations are silently ignored.</p>
   */
  @Override
  public void register(GameObserver observer) {
    if (!observers.contains(observer)) {
      observers.add(observer);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void unregister(GameObserver observer) {
    observers.remove(observer);
  }

  /**
   * Notifies all registered observers that this player's state has changed.
   */
  private void notifyObservers() {
    observers.forEach(GameObserver::update);
  }

  /**
   * Constructs a new player with the given display name and starting cash balance.
   *
   * <p>The player's portfolio and transaction archive are initialised as empty, and their
   * initial status is set to {@link Status#NOVICE}.</p>
   *
   * @param name          the display name of the player; must not be {@code null} or blank
   * @param startingMoney the initial cash balance; must not be {@code null} or negative
   * @throws IllegalArgumentException if {@code name} is {@code null} or blank, or if
   *                                  {@code startingMoney} is {@code null} or negative
   */
  public Player(String name, BigDecimal startingMoney) {

    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Player name cannot be null or blank");
    }
    if (startingMoney == null || startingMoney.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Start money cannot be negative");
    }
    this.name = name;
    this.startingMoney = startingMoney;
    this.money = startingMoney;
    this.portfolio = new Portfolio();
    this.transactionArchive = new TransactionArchive();

    this.status = Status.NOVICE;
  }

  /**
   * Retrieves the display name of this player.
   *
   * @return the player's name; never {@code null} or blank
   */
  public String getName() {
    return name;
  }

  /**
   * Retrieves the player's current cash balance.
   *
   * @return the available cash; never {@code null}
   */
  public BigDecimal getMoney() {
    return money;
  }

  /**
   * Retrieves the cash balance this player started the game with.
   *
   * @return the starting balance; never {@code null}
   */
  public BigDecimal getStartingMoney() {
    return startingMoney;
  }

  /**
   * Retrieves the player's current activity status.
   *
   * @return the current {@link Status}; never {@code null}
   */
  public Status getStatus() {
    return status;
  }

  /**
   * Increases the player's cash balance by the specified amount.
   *
   * @param amount the amount to add; must be positive and not {@code null}
   * @throws IllegalArgumentException if {@code amount} is {@code null}, zero, or negative
   */
  public void addMoney(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("You cannot add negative money or zero");
    }
    money = money.add(amount);

  }

  /**
   * Decreases the player's cash balance by the specified amount.
   *
   * <p>This method does not enforce a minimum balance; the caller is responsible for
   * verifying that the player has sufficient funds before invoking it.</p>
   *
   * @param amount the amount to subtract; must be positive and not {@code null}
   * @throws IllegalArgumentException if {@code amount} is {@code null}, zero, or negative
   */
  public void withdrawMoney(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("You cannot withdraw negative money or zero");
    }
    money = money.subtract(amount);

  }

  /**
   * Replaces the player's current cash balance with the specified value.
   *
   * <p>Intended for use during game-state restoration (e.g., loading a saved game).</p>
   *
   * @param money the new balance; must not be {@code null} or negative
   * @throws IllegalArgumentException if {@code money} is {@code null} or negative
   */
  public void setMoney(BigDecimal money) {
    if (money == null || money.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Money cannot be negative");
    }
    this.money = money;
  }

  /**
   * Overrides the player's current status with the specified value.
   *
   * <p>Intended for use during game-state restoration. Under normal gameplay, status is
   * managed automatically by {@link #updateStatus()}.</p>
   *
   * @param status the status to assign; must not be {@code null}
   * @throws IllegalArgumentException if {@code status} is {@code null}
   */
  public void setStatus(Status status) {
    if (status == null) {
      throw new IllegalArgumentException("Status cannot be null");
    }
    this.status = status;
  }

  /**
   * Re-evaluates and updates the player's {@link Status} based on current trading history
   * and profitability.
   *
   * <p>The promotion rules are:
   * <ul>
   *   <li>{@link Status#SPECULATOR} — at least 20 distinct trading weeks and a profit of
   *       at least 100%.</li>
   *   <li>{@link Status#INVESTOR} — at least 10 distinct trading weeks and a profit of
   *       at least 20%.</li>
   *   <li>{@link Status#NOVICE} — all other cases.</li>
   * </ul>
   * If the starting balance is zero, status is not updated. All registered observers are
   * notified after a status change.</p>
   */
  public void updateStatus() {
    if (startingMoney.compareTo(BigDecimal.ZERO) <= 0) {
      return;
    }

    int weeks = this.transactionArchive.countDistinctWeeks();

    BigDecimal profitPercent = money.subtract(startingMoney)
        .divide(startingMoney, MathContext.DECIMAL128)
        .multiply(new BigDecimal(100))
        .setScale(2, RoundingMode.HALF_UP);

    if (weeks >= 20 && profitPercent
        .compareTo(new BigDecimal("100")) >= 0) {
      status = Status.SPECULATOR;
    } else if (weeks >= 10 && profitPercent
        .compareTo(new BigDecimal("20")) >= 0) {
      status = Status.INVESTOR;
    } else {
      status = Status.NOVICE;
    }

    notifyObservers();
  }

  /**
   * Adds a share position to this player's portfolio.
   *
   * @param share the share to add; must not be {@code null}
   */
  public void addShareToPortfolio(Share share) {
    portfolio.addShare(share);
  }

  /**
   * Reduces a share position in this player's portfolio by the specified quantity.
   *
   * @param share  the share position to reduce; matched by stock symbol
   * @param amount the quantity to subtract
   * @return {@code true} if the reduction was successful; {@code false} if the position
   *         was not found or the amount exceeded the held quantity
   */
  public boolean reduceShareInPortfolio(Share share, BigDecimal amount) {
    return portfolio.reduceShare(share, amount);
  }

  /**
   * Records a committed transaction in this player's transaction archive.
   *
   * @param transaction the transaction to archive; must not be {@code null}
   */
  public void archiveTransaction(Transaction transaction) {
    transactionArchive.add(transaction);
  }

  /**
   * Retrieves this player's portfolio of share positions.
   *
   * @return the {@link Portfolio}; never {@code null}
   */
  public Portfolio getPortfolio() {
    return portfolio;
  }

  /**
   * Retrieves this player's complete transaction history.
   *
   * @return the {@link TransactionArchive}; never {@code null}
   */
  public TransactionArchive getTransactionArchive() {
    return transactionArchive;
  }

  /**
   * Calculates the player's total net worth as cash plus the current market value of
   * all portfolio positions.
   *
   * @return the sum of cash balance and portfolio market value
   */
  public BigDecimal getNetWorth() {
    return money.add(portfolio.getNetWorth());
  }

  /**
   * Calculates the absolute gain or loss relative to the player's starting balance.
   *
   * <p>Computed as {@code netWorth - startingMoney}. A positive value indicates overall
   * profit; a negative value indicates a loss.</p>
   *
   * @return the total gain (positive) or loss (negative) since the game started
   */
  public BigDecimal getTotalGainLoss() {
    return getNetWorth().subtract(getStartingMoney());
  }

  /**
   * Calculates the percentage gain or loss relative to the player's starting balance.
   *
   * <p>Returns {@link BigDecimal#ZERO} if the starting balance is zero to prevent
   * division by zero. The result is scaled to four decimal places.</p>
   *
   * @return the percentage gain (positive) or loss (negative) relative to the starting
   *         balance; {@link BigDecimal#ZERO} if the starting balance is zero
   */
  public BigDecimal getTotalGainLossPercent() {
    if (startingMoney.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }
    return getNetWorth().subtract(startingMoney)
        .divide(startingMoney, 4, RoundingMode.HALF_UP)
        .multiply(new BigDecimal("100"));
  }
}
