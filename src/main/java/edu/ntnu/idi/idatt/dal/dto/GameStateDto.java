package edu.ntnu.idi.idatt.dal.dto;

/**
 * Data transfer object representing the complete state of a game session.
 *
 * <p>This record is the root object serialised to and deserialised from a
 * {@code .millions} save file. It aggregates the player state and the exchange state
 * into a single snapshot that can fully restore a game in progress.</p>
 *
 * @param player   the serialised state of the player, including balance, portfolio, and
 *                 transaction history
 * @param exchange the serialised state of the exchange, including all listed stocks and
 *                 their complete price histories
 */
public record GameStateDto(
    PlayerDto player,
    ExchangeDto exchange
) {}
