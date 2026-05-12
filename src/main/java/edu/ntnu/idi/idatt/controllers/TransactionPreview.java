package edu.ntnu.idi.idatt.controllers;

import java.math.BigDecimal;

public record TransactionPreview(
    BigDecimal gross,
    BigDecimal commission,
    BigDecimal tax,
    BigDecimal total
) {}
