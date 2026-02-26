package edu.ntnu.idi.idatt.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

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
  }

  @Test
  void testGetTransactionArchive() {
    assertNotNull(player.getTransactionArchive());
  }


}