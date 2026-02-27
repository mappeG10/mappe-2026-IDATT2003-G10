package edu.ntnu.idi.idatt.models;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SaleTest {

  private Stock stock;
  private BigDecimal startingMoney;
  private Player player;
  private Share share;
  private Sale sale;




  @BeforeEach
  void setUp() {

    stock = new Stock("AAPL",
        "Apple",
        new ArrayList<>(List.of(
            new BigDecimal("182.50"),
            new BigDecimal("183.75"),
            new BigDecimal("181.20"))));

    startingMoney = new BigDecimal(10000);
    player = new Player("Player test", startingMoney);

    BigDecimal quantity = new BigDecimal("100");
    BigDecimal purchasePrice = stock.getSalesPrice();

    share = new Share(stock, quantity, purchasePrice);

    player.getPortfolio().addShare(share);
    sale = new Sale(share, 1);


  }

  @Test
  void testSaleConstructor() {
    assertNotNull(sale);
    assertEquals(share, sale.getShare());
    assertEquals(1, sale.getWeek());
    assertFalse(sale.isCommitted());
  }

  @Test
  void testConstructorThrowsExceptions() {
    assertThrows(IllegalArgumentException.class,
        () -> new Sale(null, 1));

    assertThrows(IllegalArgumentException.class,
        () -> new Sale(share, 0));
  }

  @Test
  void testCommitSuccessfully() {

    BigDecimal expectedMoney = startingMoney.add(sale.getCalculator().calculateTotal());


    sale.commit(player);



    assertEquals(0, player.getMoney().compareTo(expectedMoney),
        "Player should receive correct amount of money");

    assertFalse(player.getPortfolio().contatins(share),
        "Player should not own share after selling it");

    assertTrue(player.getTransactionArchive().getTransactions(1).contains(sale),
        "The transaction archive should contain the sale after selling it");

    assertTrue(sale.isCommitted(),
        "isCommited flag should be set to true after commiting");

  }

  @Test
  void testCommittingTwoTimes() {


    sale.commit(player);

    BigDecimal moneyAfterCommit = player.getMoney();


    sale.commit(player);

    assertEquals(0, player.getMoney().compareTo(moneyAfterCommit),
        "Money should be the same after first commit");

    // TODO: Should throw exception
  }

  @Test
  void testCommitingWhenNotOwningShare() {

    Share shareNotOwned = new Share(stock, new BigDecimal(10), new BigDecimal(99));

    Sale saleThatIsIllegal = new Sale(shareNotOwned, 1);

    List<Share> listOfSharesBeforeCommit = player.getPortfolio().getShares();

    saleThatIsIllegal.commit(player);

    assertEquals(player.getPortfolio().getShares(), listOfSharesBeforeCommit);

    // TODO: Should throw exception


  }


}