package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.controller.dto.GameSummary;
import edu.ntnu.idi.idatt.model.Exchange;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Share;
import java.util.List;

public class SummaryController extends BaseController {

  public SummaryController(Exchange exchange, Player player) {
    super(exchange, player);
  }

  public GameSummary finishGame() {
    List<Share> shares = List.copyOf(player.getPortfolio().getShares());
    for (Share share : shares) {
      exchange.sell(share, share.getQuantity(), player);
    }
    player.updateStatus();
    return new GameSummary(
        player.getName(),
        player.getStartingMoney(),
        player.getMoney(),
        player.getTotalGainLoss(),
        player.getTotalGainLossPercent(),
        player.getStatus(),
        exchange.getWeek(),
        player.getTransactionArchive().countDistinctWeeks()
    );
  }
}
