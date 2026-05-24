package edu.ntnu.idi.idatt.dal.dto;

import java.util.List;

public record ExchangeDto(
    String name,
    int week,
    List<StockDto> stocks
) {}
