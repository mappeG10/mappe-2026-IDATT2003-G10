package edu.ntnu.idi.idatt.controllers;

import edu.ntnu.idi.idatt.dal.StockParser;
import edu.ntnu.idi.idatt.models.Exchange;
import edu.ntnu.idi.idatt.models.Player;
import edu.ntnu.idi.idatt.models.Stock;
import java.math.BigDecimal;
import java.util.List;

public class GameFactory {

  private static final String EXCHANGE_NAME = "Millions Exchange";

  public static GameController createController(GameSetup setup) {
    Player player = new Player(setup.playerName(), setup.startingCapital());
    List<Stock> stockList = StockParser.parseStocks(setup.csvPath());
    Exchange exchange = new Exchange(EXCHANGE_NAME, stockList);
    return new GameController(exchange, player);
  }
}
