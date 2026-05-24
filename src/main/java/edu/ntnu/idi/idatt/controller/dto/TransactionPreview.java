package edu.ntnu.idi.idatt.controller.dto;

import java.math.BigDecimal;

public record TransactionPreview(
    BigDecimal gross,
    BigDecimal commission,
    BigDecimal tax,
    BigDecimal total
) {}
