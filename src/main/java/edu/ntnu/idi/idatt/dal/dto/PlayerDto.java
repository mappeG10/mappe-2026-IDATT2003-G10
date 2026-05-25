package edu.ntnu.idi.idatt.dal.dto;

import edu.ntnu.idi.idatt.model.Player;
import java.math.BigDecimal;
import java.util.List;

public record PlayerDto(
    String name,
    BigDecimal money,
    BigDecimal startingMoney,
    Player.Status status,
    PortfolioDto portfolio,
    List<TransactionDto> transactions
) {}
