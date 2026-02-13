package edu.ntnu.idi.idatt.models;

import java.math.BigDecimal;

public class Player {

  private final String name;
  private final BigDecimal startingMoney;
  private BigDecimal money;
  private final Portfolio portfolio;
  private final TransactionArchive transactionArchive;

  public Player(String name, BigDecimal startingMoney) {
    // TODO: implemement guard conditions
    this.name = name;
    this.startingMoney = startingMoney;
    this.money = startingMoney;
    this.portfolio = new Portfolio();
    this.transactionArchive = new TransactionArchive();
  }

  public String getName() {
    return name;
  }

  public BigDecimal getMoney() {
    return money;
  }

  public void addMoney(BigDecimal amount) {
    money = money.add(amount); // TODO: handle potential illegal operations
  }

  public void withdrawMoney(BigDecimal amount) {
    money = money.subtract(amount); // TODO: handle potential illegal operations
  }

  public Portfolio getPortfolio() {
    return portfolio;
  }

  public TransactionArchive getTransactionArchive() {
    return transactionArchive;
  }
}
