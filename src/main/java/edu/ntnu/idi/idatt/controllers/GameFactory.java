package edu.ntnu.idi.idatt.controllers;

import edu.ntnu.idi.idatt.dal.CsvStockReader;
import edu.ntnu.idi.idatt.models.Exchange;
import edu.ntnu.idi.idatt.models.Player;
import edu.ntnu.idi.idatt.models.Stock;

import java.util.List;

public class GameFactory {

  private static final String EXCHANGE_NAME = "Millions Exchange";

  public static GameController createController(GameSetup setup) {
    Player player = new Player(setup.playerName(), setup.startingCapital());
    List<Stock> stockList = CsvStockReader.parseStocks(setup.csvPath());
    Exchange exchange = new Exchange(EXCHANGE_NAME, stockList);
    return new GameController(exchange, player);
  }
}
