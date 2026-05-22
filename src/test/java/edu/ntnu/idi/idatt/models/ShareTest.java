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

  @Test
  void testGetCurrentValue() {
    BigDecimal excpectedValue = new BigDecimal("18120");

    assertEquals(0, excpectedValue.compareTo(share.getCurrentValue()));
  }

  @Test
  void testGetCurrentValueIncreasesWithNewPrice() {
    share.getStock().addNewSalesPrice(new BigDecimal("190.90"));

    BigDecimal excpectedValue = new BigDecimal("19090");

    assertEquals(0, excpectedValue.compareTo(share.getCurrentValue()));
  }

  @Test
  void testGetGainLoss() {
    List<BigDecimal> gainPrices = new ArrayList<>(List.of(
        new BigDecimal("100"),
        new BigDecimal("150")));
    Stock gainStock = new Stock("MSFT", "Microsoft", gainPrices);
    Share gainShare = new Share(gainStock, new BigDecimal("10"), new BigDecimal("100"));

    BigDecimal expectedGainLoss = new BigDecimal("500");

    assertEquals(0, expectedGainLoss.compareTo(gainShare.getGainLoss()),
        "Gain/loss should be 500 when 10 shares bought at 100 are now worth 150");
  }

  @Test
  void testGetGainLossNegative() {
    List<BigDecimal> lossPrices = new ArrayList<>(List.of(
        new BigDecimal("100"),
        new BigDecimal("50")));
    Stock lossStock = new Stock("MSFT", "Microsoft", lossPrices);
    Share lossShare = new Share(lossStock, new BigDecimal("10"), new BigDecimal("100"));

    BigDecimal expectedGainLoss = new BigDecimal("-500");

    assertEquals(0, expectedGainLoss.compareTo(lossShare.getGainLoss()),
        "Gain/loss should be -500 when 10 shares bought at 100 are now worth 50");
  }

  @Test
  void testGetGainLossPercent() {
    List<BigDecimal> gainPrices = new ArrayList<>(List.of(
        new BigDecimal("100"),
        new BigDecimal("150")));
    Stock gainStock = new Stock("MSFT", "Microsoft", gainPrices);
    Share gainShare = new Share(gainStock, new BigDecimal("10"), new BigDecimal("100"));

    BigDecimal expectedPercent = new BigDecimal("50");

    assertEquals(0, expectedPercent.compareTo(gainShare.getGainLossPercent()),
        "Gain/loss percent should be 50% when current price is 50% higher than purchase price");
  }

  @Test
  void testGetGainLossPercentNegative() {
    List<BigDecimal> lossPrices = new ArrayList<>(List.of(
        new BigDecimal("100"),
        new BigDecimal("50")));
    Stock lossStock = new Stock("MSFT", "Microsoft", lossPrices);
    Share lossShare = new Share(lossStock, new BigDecimal("10"), new BigDecimal("100"));

    BigDecimal expectedPercent = new BigDecimal("-50");

    assertEquals(0, expectedPercent.compareTo(lossShare.getGainLossPercent()),
        "Gain/loss percent should be -50% when current price is 50% lower than purchase price");
  }

  @Test
  void testGetGainLossPercentWithZeroPurchasePrice() {
    List<BigDecimal> prices = new ArrayList<>(List.of(new BigDecimal("100")));
    Stock zeroPriceStock = new Stock("MSFT", "Microsoft", prices);
    Share zeroPriceShare = new Share(zeroPriceStock, new BigDecimal("10"), BigDecimal.ZERO);

    BigDecimal expectedPercent = BigDecimal.ZERO;

    assertEquals(0, expectedPercent.compareTo(zeroPriceShare.getGainLossPercent()),
        "Gain/loss percent should be 0% when purchase price is zero");
  }

}