package edu.ntnu.idi.idatt.dal.mapper;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.idatt.dal.dto.GameStateDto;
import edu.ntnu.idi.idatt.model.*;
import edu.ntnu.idi.idatt.model.transaction.TransactionType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameMapperTest {
  private Player player;
  private Exchange exchange;

  @BeforeEach
  void setUp() {
    Stock stock = new Stock("AAPL", "Apple", new ArrayList<>(List.of(new BigDecimal("150.00"))));
    exchange = new Exchange("Test Exchange", List.of(stock));
    player = new Player("Test Player", new BigDecimal("10000"));

    // Setup some state: move week, buy shares, record transactions
    exchange.advance(); // Week 2
    exchange.buy("AAPL", new BigDecimal("10"), player);
  }

  @Test
  void testToDtoMapsCorrectValues() {
    GameStateDto dto = GameMapper.toDto(player, exchange);

    assertEquals(exchange.getName(), dto.exchange().name());
    assertEquals(exchange.getWeek(), dto.exchange().week());
    assertEquals(player.getName(), dto.player().name());
    assertEquals(0, player.getMoney().compareTo(dto.player().money()));
    assertEquals(1, dto.player().portfolio().shares().size());
    assertEquals(1, dto.player().transactions().size());
  }

  @Test
  void testFromDtoRestoresStateCorrectively() {
    GameStateDto dto = GameMapper.toDto(player, exchange);
    GameMapper.GameState restored = GameMapper.fromDto(dto);

    assertEquals(exchange.getName(), restored.exchange().getName());
    assertEquals(exchange.getWeek(), restored.exchange().getWeek());
    assertEquals(player.getName(), restored.player().getName());
    assertEquals(0, player.getMoney().compareTo(restored.player().getMoney()));

    // Check portfolio
    assertEquals(1, restored.player().getPortfolio().getShares().size());
    assertEquals("AAPL", restored.player().getPortfolio().getShares().get(0).getSymbol());

    // Check history
    assertEquals(1, restored.player().getTransactionArchive().getTransactions(2).size());
    assertEquals(
        TransactionType.PURCHASE,
        restored.player().getTransactionArchive().getTransactions(2).get(0).getTransactionType());
  }
}
