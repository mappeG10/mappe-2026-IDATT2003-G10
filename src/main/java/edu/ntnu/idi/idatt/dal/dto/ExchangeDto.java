package edu.ntnu.idi.idatt.dal.dto;

import java.util.List;

/**
 * Data transfer object representing the serialised state of a stock exchange.
 *
 * <p>Captures the exchange name, the current game week, and the full list of listed stocks
 * including their price histories, so the exchange can be fully reconstructed on load.
 *
 * @param name the display name of the exchange
 * @param week the current game week at the time the state was saved
 * @param stocks the list of all stocks listed on the exchange, each with a complete price history
 */
public record ExchangeDto(String name, int week, List<StockDto> stocks) {}
