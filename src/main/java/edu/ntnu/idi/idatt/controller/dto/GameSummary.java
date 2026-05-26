package edu.ntnu.idi.idatt.controller.dto;

import edu.ntnu.idi.idatt.model.Player;
import java.math.BigDecimal;

/**
 * Data transfer object carrying the end-game performance statistics shown on the summary screen.
 *
 * <p>Produced by {@link edu.ntnu.idi.idatt.controller.SummaryController#finishGame()} after all
 * remaining portfolio positions have been liquidated.
 *
 * @param playerName the display name of the player
 * @param startingCapital the cash balance the player started the game with
 * @param finalBalance the player's cash balance after all positions were closed
 * @param totalGainLoss the absolute gain (positive) or loss (negative) relative to the starting
 *     capital
 * @param totalGainLossPercent the percentage gain or loss relative to the starting capital
 * @param finalStatus the player's earned {@link Player.Status} at game completion
 * @param weeksPlayed the total number of weeks elapsed on the exchange
 * @param activeWeeks the number of distinct weeks in which at least one transaction was committed
 */
public record GameSummary(
    String playerName,
    BigDecimal startingCapital,
    BigDecimal finalBalance,
    BigDecimal totalGainLoss,
    BigDecimal totalGainLossPercent,
    Player.Status finalStatus,
    int weeksPlayed,
    int activeWeeks) {}
