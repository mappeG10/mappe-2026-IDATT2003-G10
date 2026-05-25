package edu.ntnu.idi.idatt.dal.dto;

import java.math.BigDecimal;
import java.util.List;

public record StockDto(
    String symbol,
    String company,
    List<BigDecimal> prices
) {}
