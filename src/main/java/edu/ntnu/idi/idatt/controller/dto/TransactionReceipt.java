package edu.ntnu.idi.idatt.controller.dto;

import edu.ntnu.idi.idatt.model.transaction.Transaction;
import edu.ntnu.idi.idatt.model.transaction.TransactionType;
import java.math.BigDecimal;

/**
 * Data transfer object representing the confirmed details of a committed transaction.
 *
 * <p>Returned by {@link edu.ntnu.idi.idatt.controller.MarketController#executeBuy(String,
 * BigDecimal)} and {@link edu.ntnu.idi.idatt.controller.PortfolioController#executeSell(
 * edu.ntnu.idi.idatt.model.Share, BigDecimal)} after a trade has been successfully committed. Used
 * to populate the receipt widget shown to the player.
 *
 * @param type whether the transaction was a purchase or a sale
 * @param symbol the ticker symbol of the stock traded
 * @param company the full name of the issuing company
 * @param week the game week in which the transaction was committed
 * @param quantity the number of shares traded
 * @param gross the pre-fee value of the transaction
 * @param commission the broker commission charged
 * @param tax the capital-gains tax applied; zero for purchases
 * @param total the net amount debited from or credited to the player's balance
 */
public record TransactionReceipt(
    TransactionType type,
    String symbol,
    String company,
    int week,
    BigDecimal quantity,
    BigDecimal gross,
    BigDecimal commission,
    BigDecimal tax,
    BigDecimal total) {

  /**
   * Creates a {@code TransactionReceipt} by extracting all relevant fields from a committed {@link
   * Transaction}.
   *
   * @param transaction the committed transaction to summarise; must not be {@code null}
   * @return a new {@code TransactionReceipt} populated from the given transaction
   */
  public static TransactionReceipt from(Transaction transaction) {
    return new TransactionReceipt(
        transaction.getTransactionType(),
        transaction.getSymbol(),
        transaction.getCompany(),
        transaction.getWeek(),
        transaction.getQuantity(),
        transaction.getGross(),
        transaction.getCommission(),
        transaction.getTax(),
        transaction.getTotalCost());
  }
}
