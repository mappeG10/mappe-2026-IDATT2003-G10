package edu.ntnu.idi.idatt.models;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
  String name = "PlayerTest";
  BigDecimal startingMoney = new BigDecimal("10000");
  Player player;

  @BeforeEach
  void setUp() {
    player = new Player(name, startingMoney);
  }

  @Test
  void testConstructorWithValidArguments() {
    assertNotNull(player);
    assertEquals(name, player.getName(), "Player names should match");
    assertEquals(startingMoney, player.getMoney(),
        "The players current money should be the starting money.");
  }

  @Test
  void testConstructorWithInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> {
      new Player("", startingMoney);
    }, "Constructor should throw IllegalArgumentException when player's name is empty.");
    assertThrows(IllegalArgumentException.class, () -> {
      new Player(null, startingMoney);
    }, "Constructor should throw IllegalArgumentException when player's name is null.");
    assertThrows(IllegalArgumentException.class, () -> {
      new Player("name", null);
    }, "Constructor should throw IllegalArgumentException when player's name is empty.");
    assertThrows(IllegalArgumentException.class, () -> {
      new Player("name", new BigDecimal(-50));
    }, "Constructor should throw IllegalArgumentException when player's money is negative.");
  }

  @Test
  void testAddMoney() {
    BigDecimal amountToAdd = new BigDecimal("100");
    player.addMoney(amountToAdd);
    BigDecimal newAmount = startingMoney.add(amountToAdd);

    assertEquals(newAmount, player.getMoney(), "The players current money should be increased.");
    assertThrows(IllegalArgumentException.class, () -> {
      player.addMoney(null);
    }, "Constructor should throw IllegalArgumentException when adding null money.");
    assertThrows(IllegalArgumentException.class, () -> {
      player.addMoney(new BigDecimal("0"));
    },  "Constructor should throw IllegalArgumentException when adding zero money.");
    assertThrows(IllegalArgumentException.class, () -> {
      player.addMoney(new BigDecimal("-1"));
    },   "Constructor should throw IllegalArgumentException when adding negative money.");
  }

  @Test
  void testWithdrawMoney() {
    BigDecimal amountToWithdraw = new BigDecimal("100");
    player.withdrawMoney(amountToWithdraw);
    BigDecimal newAmount = startingMoney.subtract(amountToWithdraw);
    assertEquals(newAmount, player.getMoney(), "The players current money should be withdrawn.");
    assertThrows(IllegalArgumentException.class, () -> {
      player.withdrawMoney(null);
    }, "Constructor should throw IllegalArgumentException when withdrawing null money.");
    assertThrows(IllegalArgumentException.class, () -> {
      player.withdrawMoney(new BigDecimal("-1"));
    },   "Constructor should throw IllegalArgumentException when withdrawing negative money.");
    assertThrows(IllegalArgumentException.class, () -> {
      player.withdrawMoney(new BigDecimal("0"));
    },  "Constructor should throw IllegalArgumentException when withdrawing zero money.");
  }

  @Test
  void testGetPortfolio() {
    assertNotNull(player.getPortfolio());
    assertTrue(player.getPortfolio().getShares().isEmpty(),
        "Portfolio should be empty for new player");
  }

  @Test
  void testGetTransactionArchive() {
    assertNotNull(player.getTransactionArchive());
  }

  @Test
  void testGetNetWorth() {
    Stock stock1 = new Stock("APPL", "Apple", new ArrayList<>(List.of(new BigDecimal("182.5"))));
    Stock stock2 = new Stock("GOOG", "Alphabet ", new ArrayList<>(List.of(new BigDecimal("310.2"))));

    Share share1 = new Share(stock1, new BigDecimal(1), new BigDecimal("182.5"));
    Share share2 = new Share(stock2, new BigDecimal(1), new BigDecimal("310.2"));

    player.getPortfolio().addShare(share1);
    player.getPortfolio().addShare(share2);

    BigDecimal expectedNetWorth = new BigDecimal("10492.7");

    assertEquals(0, expectedNetWorth.compareTo(player.getNetWorth()),
        "Player net worth should be 10492.7; starting money plus value of shares");

  }

  @Test
  void testGetEmptyPortfolioReturnsStartingMoney() {
    assertEquals(0, startingMoney.compareTo(player.getNetWorth()),
        "Player net worth should only be starting money when owning no shares");
  }

  @Test
  void testInitialStatusIsNovice(){
    assertEquals(Player.Status.NOVICE, player.getStatus());
    player.updateStatus();
    assertEquals(Player.Status.NOVICE, player.getStatus());
  }

  @Test
  void testUpdateStatusToInvestor() {
    player.addMoney(new BigDecimal("2000"));  // Update corresponding to startMoney
                                                  // (should be around 20% of startMoney)
    addDummyTransactions(10);
    player.updateStatus();
    assertEquals(Player.Status.INVESTOR, player.getStatus());
  }

  @Test
  void testUpdateStatusToSpeculator() {
    player.addMoney(new BigDecimal("10000")); // Update corresponding to startMoney (should be half the amount)
    addDummyTransactions(20);
    player.updateStatus();
    assertEquals(Player.Status.SPECULATOR, player.getStatus());
  }

  /**
   * Helper method to populate the archive with transactions across X weeks.
   */
  private void addDummyTransactions(int weeks) {
    for (int i = 1; i <= weeks; i++) {
      Stock stock = new Stock("APPL", "Apple", new ArrayList<>());
      Share dummyShare =
          new Share(stock,
              new BigDecimal(1),
              new BigDecimal(120 + i));
      player.getTransactionArchive().add(new Purchase(dummyShare, i));
    }
  }

}