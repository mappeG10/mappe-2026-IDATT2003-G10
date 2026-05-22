package edu.ntnu.idi.idatt.controllers;

import java.math.BigDecimal;

public record GameSetup(
    String playerName,
    BigDecimal startinCapital,
    String csvPath
) {}
