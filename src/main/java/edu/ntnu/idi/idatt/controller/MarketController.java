package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.controller.dto.TransactionPreview;
import edu.ntnu.idi.idatt.controller.dto.TransactionReceipt;
import edu.ntnu.idi.idatt.model.Exchange;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Share;
import edu.ntnu.idi.idatt.model.Stock;
import edu.ntnu.idi.idatt.model.transaction.Transaction;
import edu.ntnu.idi.idatt.model.transaction.TransactionCalculator;
import edu.ntnu.idi.idatt.model.transaction.TransactionFactory;
import edu.ntnu.idi.idatt.model.transaction.TransactionType;
import java.math.BigDecimal;
import java.util.List;

public class MarketController extends BaseController {

  public MarketController(Exchange exchange, Player player) {
    super(exchange, player);
  }

  public List<Stock> getAllStocks() {
    return exchange.getAllStocks();
  }

  public List<Stock> findStocks(String searchTerm) {
    return exchange.findStock(searchTerm);
  }

  public TransactionPreview previewBuy(String symbol, BigDecimal quantity) {
    Stock stock = exchange.getStock(symbol);
    Share sharePreview = new Share(stock, quantity, stock.getSalesPrice());
    Transaction previewPurchase = TransactionFactory.createTransaction(TransactionType.PURCHASE, sharePreview, exchange.getWeek());
    TransactionCalculator previewCalculator = previewPurchase.getCalculator();
    return new TransactionPreview(previewCalculator.calculateGross(), previewCalculator.calculateCommission(), previewCalculator.calculateTax(), previewCalculator.calculateTotal());
  }

  public TransactionReceipt executeBuy(String symbol, BigDecimal quantity) {
    return TransactionReceipt.from(exchange.buy(symbol, quantity, player));
  }



}
