package edu.ntnu.idi.idatt.controllers.dto;

import java.math.BigDecimal;

public record GameSetup(
    String playerName,
    BigDecimal startingCapital,
    String source
) {}
