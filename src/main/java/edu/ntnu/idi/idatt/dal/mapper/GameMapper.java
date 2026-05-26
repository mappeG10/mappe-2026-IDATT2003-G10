package edu.ntnu.idi.idatt.dal.mapper;

import edu.ntnu.idi.idatt.dal.dto.ExchangeDto;
import edu.ntnu.idi.idatt.dal.dto.GameStateDto;
import edu.ntnu.idi.idatt.dal.dto.PlayerDto;
import edu.ntnu.idi.idatt.dal.dto.PortfolioDto;
import edu.ntnu.idi.idatt.dal.dto.ShareDto;
import edu.ntnu.idi.idatt.dal.dto.StockDto;
import edu.ntnu.idi.idatt.dal.dto.TransactionDto;
import edu.ntnu.idi.idatt.model.Exchange;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Share;
import edu.ntnu.idi.idatt.model.Stock;
import edu.ntnu.idi.idatt.model.transaction.Transaction;
import edu.ntnu.idi.idatt.model.transaction.TransactionFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converts between domain model objects and their data transfer object (DTO) representations.
 *
 * <p>This class handles the two primary mapping directions required for game persistence:
 *
 * <ul>
 *   <li>{@link #toDto(Player, Exchange)} — serialises the live game state into a {@link
 *       GameStateDto} that can be written to disk.
 *   <li>{@link #fromDto(GameStateDto)} — deserialises a {@link GameStateDto} back into fully wired
 *       domain objects, including re-linking share positions and archived transactions to their
 *       stock references.
 * </ul>
 *
 * <p>This class is not instantiable; all methods are static.
 */
public class GameMapper {

  /** Prevents instantiation of this static utility class. */
  private GameMapper() {}

  /**
   * Serialises the current game state into a {@link GameStateDto}.
   *
   * <p>The resulting DTO captures the complete state of both the player (balance, portfolio,
   * transaction archive, and status) and the exchange (name, current week, and full price history
   * for every listed stock).
   *
   * @param player the player whose state should be serialised; must not be {@code null}
   * @param exchange the exchange whose state should be serialised; must not be {@code null}
   * @return a {@link GameStateDto} representing the current game state; never {@code null}
   */
  public static GameStateDto toDto(Player player, Exchange exchange) {
    ExchangeDto exchangeDto =
        new ExchangeDto(
            exchange.getName(),
            exchange.getWeek(),
            exchange.getAllStocks().stream()
                .map(s -> new StockDto(s.getSymbol(), s.getCompany(), s.getHistoricalPrices()))
                .toList());

    PortfolioDto portfolioDto =
        new PortfolioDto(
            player.getPortfolio().getShares().stream()
                .map(s -> new ShareDto(s.getSymbol(), s.getQuantity(), s.getPurchasePrice()))
                .toList());

    List<TransactionDto> transactionDtos =
        player.getTransactionArchive().getDistinctWeeksAsList().stream()
            .flatMap(w -> player.getTransactionArchive().getTransactions(w).stream())
            .map(
                t ->
                    new TransactionDto(
                        t.getTransactionType(),
                        t.getSymbol(),
                        t.getQuantity(),
                        t.getPurchasePrice(),
                        t.getWeek()))
            .toList();

    PlayerDto playerDto =
        new PlayerDto(
            player.getName(),
            player.getMoney(),
            player.getStartingMoney(),
            player.getStatus(),
            portfolioDto,
            transactionDtos);

    return new GameStateDto(playerDto, exchangeDto);
  }

  /**
   * Deserialises a {@link GameStateDto} into a fully constructed {@link GameState}.
   *
   * <p>Stock references are resolved from the reconstructed exchange by symbol, then used to
   * rebuild portfolio share positions and the transaction archive. All reconstructed transactions
   * are marked as committed so they cannot be accidentally re-applied.
   *
   * @param dto the game state DTO to deserialise; must not be {@code null}
   * @return a {@link GameState} containing the fully wired {@link Player} and {@link Exchange};
   *     never {@code null}
   */
  public static GameState fromDto(GameStateDto dto) {
    List<Stock> stocks =
        dto.exchange().stocks().stream()
            .map(s -> new Stock(s.symbol(), s.company(), s.prices()))
            .toList();
    Exchange exchange = new Exchange(dto.exchange().name(), stocks);
    exchange.setWeek(dto.exchange().week());

    Player player = new Player(dto.player().name(), dto.player().startingMoney());
    player.setMoney(dto.player().money());
    player.setStatus(dto.player().status());

    Map<String, Stock> stockMap =
        exchange.getAllStocks().stream().collect(Collectors.toMap(Stock::getSymbol, s -> s));

    for (ShareDto shareDto : dto.player().portfolio().shares()) {
      Stock stock = stockMap.get(shareDto.stockSymbol());
      player
          .getPortfolio()
          .addShare(new Share(stock, shareDto.quantity(), shareDto.purchasePrice()));
    }

    for (TransactionDto txDto : dto.player().transactions()) {
      Stock stock = stockMap.get(txDto.stockSymbol());
      Share share = new Share(stock, txDto.quantity(), txDto.price());
      Transaction tx = TransactionFactory.createTransaction(txDto.type(), share, txDto.week());
      tx.setCommitted();
      player.getTransactionArchive().add(tx);
    }

    return new GameState(player, exchange);
  }

  /**
   * A value object pairing a reconstructed {@link Player} with its associated {@link Exchange}
   * after deserialisation.
   *
   * @param player the fully reconstructed player
   * @param exchange the fully reconstructed exchange
   */
  public record GameState(Player player, Exchange exchange) {}
}
