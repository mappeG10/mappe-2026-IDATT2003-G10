package edu.ntnu.idi.idatt.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransactionArchiveTest {


  private Stock stock;
  private Player player;
  private BigDecimal startingMoney;
  private Share share;
  private TransactionArchive transactionArchive;



  @BeforeEach
  void setUp() {

    stock = new Stock("AAPL",
        "Apple",
        new ArrayList<>(List.of(
            new BigDecimal("182.50"),
            new BigDecimal("183.75"),
            new BigDecimal("181.20"))));

    startingMoney = BigDecimal.valueOf(10000);
    player = new Player("Player test", startingMoney);

    BigDecimal quantity = new BigDecimal("10");
    BigDecimal purchasePrice = stock.getSalesPrice();

    share = new Share(stock, quantity, purchasePrice);

    transactionArchive = player.getTransactionArchive();

  }

  @Test
  void testTransactionArchiveConstructor() {
    assertTrue(transactionArchive.isEmpty(), "Should be empty on creation");
  }


  @Test
  void testValidSaleAdd() {
    player.getPortfolio().addShare(share);

    Sale sale = new Sale(share, 1);
    sale.commit(player);


    assertTrue(player.getTransactionArchive().getTransactions(1).contains(sale));
    assertEquals(1, player.getTransactionArchive().getTransactions(1).size());

  }

  @Test
  void testValidPurchaseAdd() {
    Purchase purchase = new Purchase(share, 1);

    purchase.commit(player);


    assertTrue(player.getTransactionArchive().getTransactions(1).contains(purchase));
    assertEquals(1, player.getTransactionArchive().getTransactions(1).size());


  }


  @Test
  void testGetTransactions() {
    Purchase purchase = new Purchase(share, 1);
    Sale sale = new Sale(share, 1);

    purchase.commit(player);
    sale.commit(player);

    List<Transaction> transactions = new ArrayList<>(List.of(purchase, sale));

    assertEquals(transactions, player.getTransactionArchive().getTransactions(1));

  }

  @Test
  void testGetPurchases() {
    Purchase purchase = new Purchase(share, 1);
    Sale sale = new Sale(share, 1);

    purchase.commit(player);
    sale.commit(player);

    List<Transaction> purchases = new ArrayList<>(List.of(purchase));

    assertEquals(purchases, player.getTransactionArchive().getPurchases(1));
  }

  @Test
  void testGetSales() {
    Purchase purchase = new Purchase(share, 1);
    Sale sale = new Sale(share, 1);

    purchase.commit(player);
    sale.commit(player);

    List<Transaction> sales = new ArrayList<>(List.of(sale));

    assertEquals(sales, player.getTransactionArchive().getSales(1));

  }

  @Test
  void testCountDistinctWeeks() {

    Purchase firstWeekFirstPurchase = new Purchase(share, 1);
    Purchase secondWeekFirstPurchase = new Purchase(share, 2);
    Purchase thirdWeekFirstPurchase = new Purchase(share, 3);
    Purchase thirdWeekSecondPurchase = new Purchase(share, 3);


    firstWeekFirstPurchase.commit(player);
    secondWeekFirstPurchase.commit(player);
    thirdWeekFirstPurchase.commit(player);
    thirdWeekSecondPurchase.commit(player);


    assertEquals(3, transactionArchive.countDistinctWeeks(),
        "Should be 3 distinct weeks in the transaction archive");

  }


  @Test
  void testIsEmpty() {
    assertTrue(transactionArchive.isEmpty(), "Transaction archive should be empty when constructed");
  }

  @Test
  void testIsNotEmpty() {
    Purchase purchase = new Purchase(share, 1);

    purchase.commit(player);

    assertFalse(transactionArchive.isEmpty(), "Transaction archive should not be empty after a commit");
  }

}
