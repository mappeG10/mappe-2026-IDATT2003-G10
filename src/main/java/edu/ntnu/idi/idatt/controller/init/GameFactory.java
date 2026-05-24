package edu.ntnu.idi.idatt.controller.init;

import edu.ntnu.idi.idatt.controller.GameController;
import edu.ntnu.idi.idatt.controller.dto.GameSetup;
import edu.ntnu.idi.idatt.dal.DataReader;
import edu.ntnu.idi.idatt.dal.StockReaderFactory;
import edu.ntnu.idi.idatt.dal.exception.DataAccessException;
import edu.ntnu.idi.idatt.model.Exchange;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Stock;

import java.io.IOException;
import java.util.List;

public class GameFactory {

  private static final String EXCHANGE_NAME = "Millions Exchange";

  public static GameController createController(GameSetup setup) throws IOException, DataAccessException {
    Player player = new Player(setup.playerName(), setup.startingCapital());

    DataReader<List<Stock>> stockReader = StockReaderFactory.getStockReader(setup.source());

    List<Stock> stockList = stockReader.read(setup.source());
    Exchange exchange = new Exchange(EXCHANGE_NAME, stockList);
    return new GameController(exchange, player);
  }
}
