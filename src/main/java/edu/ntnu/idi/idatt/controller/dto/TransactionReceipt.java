package edu.ntnu.idi.idatt.controller.dto;

import edu.ntnu.idi.idatt.model.transaction.Transaction;
import edu.ntnu.idi.idatt.model.transaction.TransactionType;
import java.math.BigDecimal;

public record TransactionReceipt(
    TransactionType type,
    String symbol,
    String company,
    int week,
    BigDecimal quantity,
    BigDecimal gross,
    BigDecimal commission,
    BigDecimal tax,
    BigDecimal total
) {
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
        transaction.getTotalCost()
    );
  }
}
