package edu.ntnu.idi.idatt.dal.dto;

import java.math.BigDecimal;

public record ShareDto(
    String stockSymbol,
    BigDecimal quantity,
    BigDecimal purchasePrice
) {}
