package edu.ntnu.idi.idatt.controller.dto;

import java.math.BigDecimal;

public record GameSetup(
    String playerName,
    BigDecimal startingCapital,
    String source
) {}
