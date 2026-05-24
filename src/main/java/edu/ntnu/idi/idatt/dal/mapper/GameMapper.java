package edu.ntnu.idi.idatt.dal.mapper;

import edu.ntnu.idi.idatt.dal.dto.*;
import edu.ntnu.idi.idatt.model.*;
import edu.ntnu.idi.idatt.model.transaction.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameMapper {

  public static GameStateDto toDto(Player player, Exchange exchange) {
    ExchangeDto exchangeDto = new ExchangeDto(
        exchange.getName(),
        exchange.getWeek(),
        exchange.getAllStocks().stream()
            .map(s -> new StockDto(s.getSymbol(), s.getCompany(), s.getHistoricalPrices()))
            .toList()
    );

    PortfolioDto portfolioDto = new PortfolioDto(
        player.getPortfolio().getShares().stream()
            .map(s -> new ShareDto(s.getSymbol(), s.getQuantity(), s.getPurchasePrice()))
            .toList()
    );

    List<TransactionDto> transactionDtos = player.getTransactionArchive().getDistinctWeeksAsList().stream()
        .flatMap(w -> player.getTransactionArchive().getTransactions(w).stream())
        .map(t -> new TransactionDto(
            t.getTransactionType(),
            t.getSymbol(),
            t.getQuantity(),
            t.getPurchasePrice(),
            t.getWeek()
        ))
        .toList();

    PlayerDto playerDto = new PlayerDto(
        player.getName(),
        player.getMoney(),
        player.getStartingMoney(),
        player.getStatus(),
        portfolioDto,
        transactionDtos
    );

    return new GameStateDto(playerDto, exchangeDto);
  }

  public static GameState fromDto(GameStateDto dto) {
    List<Stock> stocks = dto.exchange().stocks().stream()
        .map(s -> new Stock(s.symbol(), s.company(), s.prices()))
        .toList();
    Exchange exchange = new Exchange(dto.exchange().name(), stocks);
    exchange.setWeek(dto.exchange().week());

    Player player = new Player(dto.player().name(), dto.player().startingMoney());
    player.setMoney(dto.player().money());
    player.setStatus(dto.player().status());

    Map<String, Stock> stockMap = exchange.getAllStocks().stream()
        .collect(Collectors.toMap(Stock::getSymbol, s -> s));

    for (ShareDto shareDto : dto.player().portfolio().shares()) {
      Stock stock = stockMap.get(shareDto.stockSymbol());
      player.getPortfolio().addShare(new Share(stock, shareDto.quantity(), shareDto.purchasePrice()));
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

  public record GameState(Player player, Exchange exchange) {}
}
