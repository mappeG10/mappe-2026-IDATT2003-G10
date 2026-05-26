package edu.ntnu.idi.idatt.dal.dto;

import java.util.List;

/**
 * Data transfer object representing the serialised state of a player's portfolio.
 *
 * <p>Contains the list of share positions held by the player at the time the game
 * state was saved. Each entry is sufficient to reconstruct the position by looking up
 * the stock in the exchange by its symbol.</p>
 *
 * @param shares the list of serialised share positions in the portfolio; may be empty
 *               if the player holds no shares
 */
public record PortfolioDto(
    List<ShareDto> shares
) {}
