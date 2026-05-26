package edu.ntnu.idi.idatt.model.transaction;

import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Share;
import edu.ntnu.idi.idatt.model.Stock;
import edu.ntnu.idi.idatt.model.exception.InsufficientSharesException;
import edu.ntnu.idi.idatt.model.exception.TransactionAlreadyCommittedException;
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

    assertFalse(player.getPortfolio().contains(share),
        "Player should not own share after selling it");

    assertTrue(player.getTransactionArchive().getTransactions(1).contains(sale),
        "The transaction archive should contain the sale after selling it");

    assertTrue(sale.isCommitted(),
        "isCommited flag should be set to true after commiting");

  }

  @Test
  void testCommittingTwoTimes() {
    sale.commit(player);
    assertThrows(TransactionAlreadyCommittedException.class, () -> sale.commit(player));
  }

  @Test
  void testCommitingWhenNotOwningShare() {
    Stock differentStock = new Stock("MSFT", "Microsoft", new ArrayList<>(List.of(new BigDecimal("300.00"))));
    Share shareNotOwned = new Share(differentStock, new BigDecimal(10), new BigDecimal(99));

    Sale saleThatIsIllegal = new Sale(shareNotOwned, 1);

    assertThrows(InsufficientSharesException.class, () -> saleThatIsIllegal.commit(player));
  }

  @Test
  void testPartialCommit() {
    BigDecimal partialQuantity = new BigDecimal("40");
    Share partialShare = new Share(stock, partialQuantity, share.getPurchasePrice());
    Sale partialSale = new Sale(partialShare, 1);

    partialSale.commit(player);

    assertTrue(partialSale.isCommitted(), "Partial sale should be committed");

    BigDecimal remaining = player.getPortfolio().getShares(stock.getSymbol()).getFirst().getQuantity();
    assertEquals(0, new BigDecimal("60").compareTo(remaining),
        "Portfolio should have 60 shares remaining after selling 40");
  }

  @Test
  void testGetTransactionTypeReturnsCorrectType() {
    assertEquals(TransactionType.SALE, sale.getTransactionType());
  }

  @Test
  void testDelegateMethods() {
    assertEquals(share.getSymbol(), sale.getSymbol(),
        "getSymbol() should delegate to the inner Share");
    assertEquals(share.getCompany(), sale.getCompany(),
        "getCompany() should delegate to the inner Share");
    assertEquals(0, share.getQuantity().compareTo(sale.getQuantity()),
        "getQuantity() should delegate to the inner Share");
    assertEquals(0, sale.getCalculator().calculateCommission().compareTo(sale.getCommission()),
        "getCommission() should delegate to the calculator");
    assertEquals(0, sale.getCalculator().calculateTax().compareTo(sale.getTax()),
        "getTax() should delegate to the calculator");
    assertEquals(0, sale.getCalculator().calculateTotal().compareTo(sale.getTotalCost()),
        "getTotalCost() should delegate to the calculator");
    assertEquals(0, sale.getCalculator().calculateGross().compareTo(sale.getGross()),
        "getGross() should delegate to the calculator");
    assertEquals(0, share.getPurchasePrice().compareTo(sale.getPurchasePrice()),
        "getPurchasePrice() should delegate to the inner Share");
  }

}
