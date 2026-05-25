package edu.ntnu.idi.idatt.controller.dto;

import edu.ntnu.idi.idatt.model.Player;
import java.math.BigDecimal;

public record GameSummary(
    String playerName,
    BigDecimal startingCapital,
    BigDecimal finalBalance,
    BigDecimal totalGainLoss,
    BigDecimal totalGainLossPercent,
    Player.Status finalStatus,
    int weeksPlayed,
    int activeWeeks
) {}
