package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.controller.dto.TransactionPreview;
import edu.ntnu.idi.idatt.model.Exchange;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Share;
import edu.ntnu.idi.idatt.model.transaction.Transaction;
import edu.ntnu.idi.idatt.model.transaction.TransactionCalculator;
import edu.ntnu.idi.idatt.model.transaction.TransactionFactory;
import edu.ntnu.idi.idatt.model.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.List;

public class PortfolioController extends BaseController {

  public PortfolioController(Exchange exchange, Player player) {
    super(exchange, player);
  }

  public List<Share> getAllShares() {
    return player.getPortfolio().getShares();
  }

  public BigDecimal getNetWorth() {
    return player.getPortfolio().getNetWorth();
  }

  public BigDecimal getTotalInvested() {
    return player.getPortfolio().getTotalInvested();
  }

  public BigDecimal getUnrealisedPnL() {
    return player.getPortfolio().getNetWorth().subtract(
        player.getPortfolio().getTotalInvested()
    );
  }

  public int getPositionsCount() {
    return player.getPortfolio().getShares().size();
  }

  public TransactionPreview previewSell(Share share, BigDecimal quantity) {
    Share previewShare = new Share(share.getStock(), quantity, share.getPurchasePrice());
    Transaction previewSell = TransactionFactory.createTransaction(
        TransactionType.SALE, previewShare, exchange.getWeek());
    TransactionCalculator previewCalculator = previewSell.getCalculator();
    return new TransactionPreview(previewCalculator.calculateGross(),
        previewCalculator.calculateCommission(), previewCalculator.calculateTax(),
        previewCalculator.calculateTotal());
  }

  public Transaction executeSell(Share share, BigDecimal quantity) {
    Transaction sale = exchange.sell(share, quantity, player);
    return sale;
  }



}
