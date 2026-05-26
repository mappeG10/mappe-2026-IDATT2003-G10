package edu.ntnu.idi.idatt.controller.dto;

import java.math.BigDecimal;

/**
 * Data transfer object carrying the configuration options chosen by the player on the start screen.
 *
 * <p>Passed to {@link edu.ntnu.idi.idatt.controller.init.GameFactory#createController(GameSetup)}
 * to initialise a new game session.
 *
 * @param playerName the display name entered by the player; must not be {@code null} or blank
 * @param startingCapital the initial cash balance allocated to the player; must be non-negative
 * @param source the file path of the stock data source (e.g., a {@code .csv} file) used to populate
 *     the exchange
 */
public record GameSetup(String playerName, BigDecimal startingCapital, String source) {}
