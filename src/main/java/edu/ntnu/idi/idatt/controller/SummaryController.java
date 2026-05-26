package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.controller.dto.GameSummary;
import edu.ntnu.idi.idatt.model.Exchange;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Share;
import java.util.List;

/**
 * Controller responsible for finalising a game session and producing an end-game summary.
 *
 * <p>When the player chooses to finish the game, this controller liquidates all remaining
 * portfolio positions at current market prices, re-evaluates the player's status, and
 * returns a {@link GameSummary} containing the final financial statistics.</p>
 */
public class SummaryController extends BaseController {

  /**
   * Constructs a new {@code SummaryController} for the given exchange and player.
   *
   * @param exchange the stock exchange for this game session; must not be {@code null}
   * @param player   the player for this game session; must not be {@code null}
   */
  public SummaryController(Exchange exchange, Player player) {
    super(exchange, player);
  }

  /**
   * Finalises the game by selling all remaining portfolio positions and collecting statistics.
   *
   * <p>All share positions held at the time of this call are sold at the current market
   * price. The player's status is re-evaluated after all positions are closed. The returned
   * {@link GameSummary} reflects the player's final cash balance, total gain/loss, and
   * earned status.</p>
   *
   * @return a {@link GameSummary} capturing the player's final performance statistics
   */
  public GameSummary finishGame() {
    List<Share> shares = List.copyOf(player.getPortfolio().getShares());
    shares.forEach(share -> {
      exchange.sell(share, share.getQuantity(), player);
    });
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
