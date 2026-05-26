package edu.ntnu.idi.idatt.controller.init;

import edu.ntnu.idi.idatt.controller.GameController;
import edu.ntnu.idi.idatt.controller.dto.GameSetup;
import edu.ntnu.idi.idatt.dal.DataReader;
import edu.ntnu.idi.idatt.dal.DataReaderFactory;
import edu.ntnu.idi.idatt.dal.dto.GameStateDto;
import edu.ntnu.idi.idatt.dal.exception.DataAccessException;
import edu.ntnu.idi.idatt.dal.mapper.GameMapper;
import edu.ntnu.idi.idatt.model.Exchange;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Stock;
import java.io.IOException;
import java.util.List;

/**
 * Factory responsible for constructing a fully initialised {@link GameController}.
 *
 * <p>Supports two creation paths:
 * <ul>
 *   <li>{@link #createController(GameSetup)} — creates a brand-new game from a
 *       {@link GameSetup} by reading stock data and setting up a fresh player.</li>
 *   <li>{@link #createControllerFromSave(String)} — restores a game session from a
 *       previously saved {@code .millions} file.</li>
 * </ul>
 * </p>
 *
 * <p>This class is not instantiable; all methods are static.</p>
 */
public class GameFactory {

  private static final String EXCHANGE_NAME = "Millions Exchange";

  /**
   * Creates a new {@link GameController} from the provided setup configuration.
   *
   * <p>Reads stock data from the source specified in {@code setup}, constructs a new
   * {@link Exchange} with those stocks, and creates a new {@link Player} with the
   * configured name and starting capital.</p>
   *
   * @param setup the game configuration chosen by the player on the start screen;
   *              must not be {@code null}
   * @return a fully initialised {@link GameController} for a new game session
   * @throws IOException           if the stock data source cannot be opened or read
   * @throws DataAccessException   if the stock data source exists but cannot be parsed
   */
  public static GameController createController(GameSetup setup)
      throws IOException, DataAccessException {
    Player player = new Player(setup.playerName(), setup.startingCapital());

    DataReader<List<Stock>> stockReader = DataReaderFactory.getStockReader(setup.source());

    List<Stock> stockList = stockReader.read(setup.source());
    Exchange exchange = new Exchange(EXCHANGE_NAME, stockList);
    return new GameController(exchange, player);
  }

  /**
   * Restores a {@link GameController} from a previously saved game file.
   *
   * <p>Reads and deserialises the {@code .millions} save file at the given path, then
   * reconstructs the domain objects via {@link GameMapper#fromDto(GameStateDto)}.</p>
   *
   * @param source the absolute or relative path to the {@code .millions} save file
   * @return a fully initialised {@link GameController} representing the saved session
   * @throws IOException           if the save file cannot be opened or read
   * @throws DataAccessException   if the save file exists but its contents are invalid
   */
  public static GameController createControllerFromSave(String source)
      throws IOException, DataAccessException {
    DataReader<GameStateDto> gameReader = DataReaderFactory.getGameReader(source);
    GameStateDto dto = gameReader.read(source);
    GameMapper.GameState state = GameMapper.fromDto(dto);

    return new GameController(state.exchange(), state.player());
  }
}
