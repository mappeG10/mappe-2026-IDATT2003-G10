package edu.ntnu.idi.idatt.dal.dto;

import java.util.List;

public record PortfolioDto(
    List<ShareDto> shares
) {}
