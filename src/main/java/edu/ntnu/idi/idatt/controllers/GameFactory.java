package edu.ntnu.idi.idatt.controllers;

import edu.ntnu.idi.idatt.dal.StockParser;
import edu.ntnu.idi.idatt.models.Exchange;
import edu.ntnu.idi.idatt.models.Player;
import edu.ntnu.idi.idatt.models.Stock;
import java.math.BigDecimal;
import java.util.List;

public class GameFactory {

  private static final String EXCHANGE_NAME = "Millions Exchange";

  public static GameController createController(String playerName, BigDecimal startingCapital, String csvFilePath) {
    Player player = new Player(playerName, startingCapital);
    List<Stock> stockList = StockParser.parseStocks(csvFilePath);
    Exchange exchange = new Exchange(EXCHANGE_NAME, stockList);
    return new GameController(exchange, player);
  }
}
