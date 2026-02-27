package edu.ntnu.idi.idatt.models;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseTest {

  private Stock stock;
  private BigDecimal startingMoney;
  private Player player;
  private Share share;
  private Purchase purchase;




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

    purchase = new Purchase(share, 1);


  }

  @Test
  void testPurchaseConstructor() {
    assertNotNull(purchase);
    assertEquals(share, purchase.getShare());
    assertEquals(1, purchase.getWeek());
    assertFalse(purchase.isCommitted());
  }

  @Test
  void testConstructorThrowsExceptions() {
    assertThrows(IllegalArgumentException.class,
        () -> new Purchase(null, 1));

    assertThrows(IllegalArgumentException.class,
        () -> new Purchase(share, 0));
  }

  @Test
  void testCommitSuccessfully() {

    BigDecimal expectedMoney = startingMoney.subtract(purchase.getCalculator().calculateTotal());


    purchase.commit(player);



    assertEquals(0, player.getMoney().compareTo(expectedMoney),
        "Player should be withdrawn correct amount of money");

    assertTrue(player.getPortfolio().contatins(share),
        "Player should share after purchasing it");

    assertTrue(player.getTransactionArchive().getTransactions(1).contains(purchase),
        "The transaction archive should contain the purchase after selling it");

    assertTrue(purchase.isCommitted(),
        "isCommited flag should be set to true after commiting");

  }

  @Test
  void testCommittingTwoTimes() {


    purchase.commit(player);

    BigDecimal moneyAfterCommit = player.getMoney();


    purchase.commit(player);

    assertEquals(0, player.getMoney().compareTo(moneyAfterCommit),
        "Money should be the same after first commit");

    // TODO: Should throw exception
  }

  @Test
  void testCommitingWhenInsufficientFunds() {
    Share overPricedShare = new Share(stock, new BigDecimal("1000"), stock.getSalesPrice());
    Purchase illegalPurchase = new Purchase(overPricedShare, 1);

    illegalPurchase.commit(player);

    assertEquals(0, player.getMoney().compareTo(startingMoney));

    // TODO: Should throw exception
  }


}