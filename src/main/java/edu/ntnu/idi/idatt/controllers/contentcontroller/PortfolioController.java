package edu.ntnu.idi.idatt.controllers.contentcontroller;

import edu.ntnu.idi.idatt.controllers.BaseController;
import edu.ntnu.idi.idatt.controllers.dto.TransactionPreview;
import edu.ntnu.idi.idatt.models.Exchange;
import edu.ntnu.idi.idatt.models.Player;
import edu.ntnu.idi.idatt.models.Share;
import edu.ntnu.idi.idatt.models.transaction.Transaction;
import edu.ntnu.idi.idatt.models.transaction.calculator.TransactionCalculator;
import edu.ntnu.idi.idatt.models.transaction.TransactionFactory;
import edu.ntnu.idi.idatt.models.transaction.TransactionType;

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
