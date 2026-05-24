package edu.ntnu.idi.idatt.controllers;

import edu.ntnu.idi.idatt.models.Exchange;
import edu.ntnu.idi.idatt.models.Player;
import edu.ntnu.idi.idatt.models.Share;
import edu.ntnu.idi.idatt.models.Stock;
import edu.ntnu.idi.idatt.models.transaction.Transaction;
import edu.ntnu.idi.idatt.models.transaction.calculator.TransactionCalculator;
import edu.ntnu.idi.idatt.models.transaction.TransactionFactory;
import edu.ntnu.idi.idatt.models.transaction.TransactionType;
import edu.ntnu.idi.idatt.observer.GameObserver;
import java.math.BigDecimal;
import java.util.List;

public class MarketController extends BaseController{

  public MarketController(Exchange exchange, Player player) {
    super(exchange, player);
  }

  public List<Stock> getAllStocks() {
    return exchange.getAllStocks();
  }

  public List<Stock> findStocks(String searchTerm) {
    return exchange.findStock(searchTerm);
  }

  public List<Stock> getGainers(int limit) {
    return exchange.getGainers(limit);
  }

  public List<Stock> getLosers(int limit) {
    return exchange.getLosers(limit);
  }

  public TransactionPreview previewBuy(String symbol, BigDecimal quantity) {
    Stock stock = exchange.getStock(symbol);
    Share sharePreview = new Share(stock, quantity, stock.getSalesPrice());
    Transaction previewPurchase = TransactionFactory.createTransaction(TransactionType.PURCHASE, sharePreview, exchange.getWeek());
    TransactionCalculator previewCalculator = previewPurchase.getCalculator();
    return new TransactionPreview(previewCalculator.calculateGross(), previewCalculator.calculateCommission(), previewCalculator.calculateTax(), previewCalculator.calculateTotal());
  }

  public Transaction executeBuy(String symbol, BigDecimal quantity) {
    Transaction purchase = exchange.buy(symbol, quantity, player);
    return purchase;
  }



}
