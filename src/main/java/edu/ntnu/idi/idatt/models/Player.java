package edu.ntnu.idi.idatt.models;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Player {

  public enum Status {
    NOVICE, INVESTOR, SPECULATOR
  }

  private final String name;
  private final BigDecimal startingMoney;
  private BigDecimal money;
  private final Portfolio portfolio;
  private final TransactionArchive transactionArchive;

  private Status status;

  public Player(String name, BigDecimal startingMoney) {

    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Player name cannot be null or blank");
    }
    if (startingMoney == null || startingMoney.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Start money cannot be negative");
    }
    this.name = name;
    this.startingMoney = startingMoney;
    this.money = startingMoney;
    this.portfolio = new Portfolio();
    this.transactionArchive = new TransactionArchive();

    this.status = Status.NOVICE;
  }



  public String getName() {
    return name;
  }

  public BigDecimal getMoney() {
    return money;
  }

  public Status getStatus() {
    return status;
  }

  public void addMoney(BigDecimal amount) {
    if (amount == null  || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("You cannot add negative money or zero");
    } // TODO: migrate over to custom exceptions later
    money = money.add(amount);
  }

  public void withdrawMoney(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("You cannot withdraw negative money or zero");
    } // TODO: migrate over to custom exceptions later
    money = money.subtract(amount);
  }

  public void updateStatus() {
    if (startingMoney.compareTo(BigDecimal.ZERO) <= 0) return;

    int weeks = this.transactionArchive.countDistinctWeeks();

    BigDecimal profitPercent = money.subtract(startingMoney)
        .divide(money, MathContext.DECIMAL128)
        .multiply(new BigDecimal(100))
        .setScale(2, RoundingMode.HALF_UP);

    if (weeks >= 20 && profitPercent
        .compareTo(new BigDecimal("100")) >= 0)
      status = Status.SPECULATOR;

    else if (weeks >= 10 && profitPercent
        .compareTo(new BigDecimal("20")) >= 0)
      status = Status.INVESTOR;

    else status = Status.NOVICE;
  }

  public Portfolio getPortfolio() {
    return portfolio;
  }

  public TransactionArchive getTransactionArchive() {
    return transactionArchive;
  }

  public BigDecimal getNetWorth() {
    return money.add(portfolio.getNetWorth());
  }
}
