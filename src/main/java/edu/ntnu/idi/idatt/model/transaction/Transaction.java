package edu.ntnu.idi.idatt.model.transaction;

import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Share;
import java.math.BigDecimal;

/**
 * Represents an immutable, single-use stock transaction in the game.
 *
 * <p>A transaction encapsulates a {@link Share}, the trading week in which it occurs,
 * and a {@link TransactionCalculator} that determines fees and taxes. Concrete
 * sub-classes ({@link Purchase} and {@link Sale}) implement {@link #commit(Player)},
 * which applies the financial effects to the player's balance and portfolio.</p>
 *
 * <p>Each instance may be committed exactly once; a second call to {@code commit} raises
 * a {@link edu.ntnu.idi.idatt.model.exception.TransactionAlreadyCommittedException}.</p>
 */
public abstract class Transaction {

  private final Share share;
  private final int week;
  private final TransactionCalculator calculator;
  private boolean committed;

  /**
   * Constructs a new transaction with the given share, week, and financial calculator.
   *
   * @param share      the share position involved in this transaction; must not be {@code null}
   * @param week       the game week in which this transaction takes place; must be at least 1
   * @param calculator the financial calculator used to derive costs and fees; must not be
   *                   {@code null}
   * @throws IllegalArgumentException if {@code share} or {@code calculator} is {@code null},
   *                                  or if {@code week} is less than 1
   */
  protected Transaction(Share share, int week, TransactionCalculator calculator) {
    if (share == null) {
      throw new IllegalArgumentException("share cannot be null");
    }
    if (week <= 0) {
      throw new IllegalArgumentException("week cannot be less than one");
    }
    if (calculator == null) {
      throw new IllegalArgumentException("calculator cannot be null");
    }
    this.share = share;
    this.week = week;
    this.calculator = calculator;
    this.committed = false;
  }

  /**
   * Retrieves the share position involved in this transaction.
   *
   * @return the {@link Share} associated with this transaction
   */
  public Share getShare() {
    return share;
  }

  /**
   * Retrieves the game week in which this transaction was created.
   *
   * @return the week number; always at least 1
   */
  public int getWeek() {
    return week;
  }

  /**
   * Retrieves the financial calculator responsible for fee and tax computations.
   *
   * @return the {@link TransactionCalculator} for this transaction
   */
  public TransactionCalculator getCalculator() {
    return calculator;
  }

  /**
   * Retrieves the ticker symbol of the stock involved in this transaction.
   *
   * @return the stock symbol, as reported by the underlying {@link Share}
   */
  public String getSymbol() {
    return share.getSymbol();
  }

  /**
   * Retrieves the company name of the stock involved in this transaction.
   *
   * @return the company name, as reported by the underlying {@link Share}
   */
  public String getCompany() {
    return share.getCompany();
  }

  /**
   * Retrieves the quantity of shares involved in this transaction.
   *
   * @return the number of shares
   */
  public BigDecimal getQuantity() {
    return share.getQuantity();
  }

  /**
   * Retrieves the purchase price per share recorded at the time the share was acquired.
   *
   * @return the per-share purchase price
   */
  public BigDecimal getPurchasePrice() {
    return share.getPurchasePrice();
  }

  /**
   * Retrieves the gross value of this transaction before fees and taxes.
   *
   * @return the gross amount, delegated to the underlying {@link TransactionCalculator}
   */
  public BigDecimal getGross() {
    return calculator.calculateGross();
  }

  /**
   * Retrieves the final net amount settled by this transaction, inclusive of all fees and taxes.
   *
   * @return the total cost (purchase) or total proceeds (sale)
   */
  public BigDecimal getTotalCost() {
    return calculator.calculateTotal();
  }

  /**
   * Retrieves the broker commission charged for this transaction.
   *
   * @return the commission amount; never negative
   */
  public BigDecimal getCommission() {
    return calculator.calculateCommission();
  }

  /**
   * Retrieves the tax amount applied to any realised profit in this transaction.
   *
   * @return the tax amount; {@link BigDecimal#ZERO} if no profit was realised
   */
  public BigDecimal getTax() {
    return calculator.calculateTax();
  }

  /**
   * Indicates whether this transaction has already been committed to a player's account.
   *
   * @return {@code true} if {@link #commit(Player)} has been successfully invoked;
   *         {@code false} otherwise
   */
  public boolean isCommitted() {
    return committed;
  }

  /**
   * Marks this transaction as committed.
   *
   * <p>Called internally by concrete sub-classes at the end of a successful
   * {@link #commit(Player)} execution to prevent double-commitment.</p>
   */
  public void setCommitted() {
    this.committed = true;
  }

  /**
   * Applies the financial effects of this transaction to the given player.
   *
   * <p>Concrete sub-classes must validate preconditions (sufficient funds or shares),
   * update the player's balance and portfolio, archive the transaction, and call
   * {@link #setCommitted()} before returning.</p>
   *
   * @param player the player against whose account this transaction is executed;
   *               must not be {@code null}
   * @throws edu.ntnu.idi.idatt.model.exception.TransactionAlreadyCommittedException
   *         if this transaction has already been committed
   */
  public abstract void commit(Player player);

  /**
   * Retrieves the type of this transaction, indicating whether it is a purchase or a sale.
   *
   * @return the {@link TransactionType} constant for this transaction
   */
  public abstract TransactionType getTransactionType();
}
