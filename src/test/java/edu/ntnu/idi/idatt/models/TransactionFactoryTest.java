package edu.ntnu.idi.idatt.models;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.idatt.models.TransactionFactory.TransactionType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionFactoryTest {

  private Share share;

  @BeforeEach
  void setUp() {
    Stock stock = new Stock("AAPL",
        "Apple",
        new ArrayList<>(List.of(
            new BigDecimal("182.50"),
            new BigDecimal("183.75"),
            new BigDecimal("181.20"))));
    share = new Share(stock, new BigDecimal("5"), stock.getSalesPrice());
  }

  @Test
  void testCreatePurchase() {
    Transaction transaction = TransactionFactory.createTransaction(TransactionType.PURCHASE, share, 1);

    assertInstanceOf(Purchase.class, transaction,
        "The transaction returned by TransactionFactory when creating purchase should be a Purchase object");
    assertEquals(share, transaction.getShare());
    assertEquals(1, transaction.getWeek());
    assertFalse(transaction.isCommitted(), "A newly created transaction should not be committed");
  }

  @Test
  void testCreateSale() {
    Transaction transaction = TransactionFactory.createTransaction(TransactionType.SALE, share, 2);

    assertInstanceOf(Sale.class, transaction,
        "The transaction returned by TransactionFactory when creating sale should be a Sale object");
    assertEquals(share, transaction.getShare(),
        "The share should be the same that is saved in the transaction object");
    assertEquals(2, transaction.getWeek());
    assertFalse(transaction.isCommitted(), "A newly created transaction should not be committed");
  }

  @Test
  void testCreateTransactionWithNullTypeThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> TransactionFactory.createTransaction(null, share, 1),
        "TransactionFactory should throw exception when type is null");
  }
}
