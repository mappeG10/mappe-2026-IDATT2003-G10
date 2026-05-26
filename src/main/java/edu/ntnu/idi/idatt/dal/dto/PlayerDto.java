package edu.ntnu.idi.idatt.dal.dto;

import edu.ntnu.idi.idatt.model.Player;
import java.math.BigDecimal;
import java.util.List;

/**
 * Data transfer object representing the serialised state of a player.
 *
 * <p>Captures all mutable player state required to restore a game session, including the current
 * cash balance, the starting balance (used for gain/loss calculations), the activity status, the
 * current portfolio, and the full transaction history.
 *
 * @param name the display name of the player
 * @param money the player's cash balance at the time the state was saved
 * @param startingMoney the cash balance the player began the game with
 * @param status the player's activity status at the time the state was saved
 * @param portfolio the player's portfolio of share positions
 * @param transactions the player's complete transaction history
 */
public record PlayerDto(
    String name,
    BigDecimal money,
    BigDecimal startingMoney,
    Player.Status status,
    PortfolioDto portfolio,
    List<TransactionDto> transactions) {}
