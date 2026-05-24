package edu.ntnu.idi.idatt.dal.dto;

import edu.ntnu.idi.idatt.model.transaction.TransactionType;
import java.math.BigDecimal;

public record TransactionDto(
    TransactionType type,
    String stockSymbol,
    BigDecimal quantity,
    BigDecimal price,
    int week
) {}
