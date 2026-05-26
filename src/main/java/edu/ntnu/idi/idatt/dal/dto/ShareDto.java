package edu.ntnu.idi.idatt.dal.dto;

import java.math.BigDecimal;

/**
 * Data transfer object representing a single share position in a serialised portfolio.
 *
 * <p>The underlying {@link edu.ntnu.idi.idatt.model.Stock} is referenced by its ticker
 * symbol rather than embedded directly, so the stock object can be resolved from the
 * exchange during deserialisation.</p>
 *
 * @param stockSymbol   the ticker symbol of the stock held in this position
 * @param quantity      the number of shares held
 * @param purchasePrice the average price per share at which this position was acquired
 */
public record ShareDto(
    String stockSymbol,
    BigDecimal quantity,
    BigDecimal purchasePrice
) {}
