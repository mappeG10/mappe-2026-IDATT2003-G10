package edu.ntnu.idi.idatt.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShareTest {
  Share share;
  Stock stock = new Stock("AAPL",
      "Apple",
      new ArrayList<>(List.of(
          new BigDecimal("182.50"),
          new BigDecimal("183.75"),
          new BigDecimal("181.20"))));
  BigDecimal quantity = new BigDecimal("100");
  BigDecimal purchasePrice = stock.getSalesPrice().multiply(quantity);

  @BeforeEach
  void setUp() {
    share = new Share(stock, quantity, purchasePrice);
  }

  @Test
  void testConstructorWithValidArguments() {
    assertNotNull(share);
    assertEquals(stock, share.getStock());
    assertEquals(purchasePrice, share.getPurchasePrice());
    assertEquals(quantity, share.getQuantity());
  }

  @Test
  void testConstructorWithInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> {
      new Share(null, quantity, purchasePrice);
    }, "Constructor didn't throw IllegalArgumentException when argument is null");
    assertThrows(IllegalArgumentException.class, () -> {
      new Share(stock, null, purchasePrice);
    }, "Constructor didn't throw IllegalArgumentException when argument is null");
    assertThrows(IllegalArgumentException.class, () -> {
      new Share(stock, quantity, null);
    }, "Constructor didn't throw IllegalArgumentException when argument is null");
    assertThrows(IllegalArgumentException.class, () -> {
      new Share(stock, new BigDecimal(0), purchasePrice);
    }, "Constructor didn't throw IllegalArgumentException when quantity is invalid");
  }
}