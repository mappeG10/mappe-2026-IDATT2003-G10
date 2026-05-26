package edu.ntnu.idi.idatt.dal.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data transfer object representing a stock and its complete price history.
 *
 * <p>Used to serialise and deserialise a {@link edu.ntnu.idi.idatt.model.Stock} as part
 * of a saved {@link ExchangeDto}. The full price list is preserved so that historical
 * chart data and trend calculations are available after a game is reloaded.</p>
 *
 * @param symbol  the ticker symbol that uniquely identifies this stock
 * @param company the full name of the issuing company
 * @param prices  the complete price history in chronological order; the last entry is the
 *                current market price
 */
public record StockDto(
    String symbol,
    String company,
    List<BigDecimal> prices
) {}
