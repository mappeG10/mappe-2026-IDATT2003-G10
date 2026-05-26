package edu.ntnu.idi.idatt.dal;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idi.idatt.dal.dto.GameStateDto;
import edu.ntnu.idi.idatt.dal.exception.DataAccessException;
import java.io.File;
import java.io.IOException;

/**
 * Reads a saved game state from a {@code .millions} JSON file.
 *
 * <p>The file is expected to contain a JSON object that can be deserialised into a {@link
 * GameStateDto}. Jackson's {@link ObjectMapper} is used for deserialisation with default settings.
 */
public class JsonGameReader implements DataReader<GameStateDto> {

  private final ObjectMapper mapper;

  /** Constructs a new {@code JsonGameReader} with a default Jackson {@link ObjectMapper}. */
  public JsonGameReader() {
    this.mapper = new ObjectMapper();
  }

  /**
   * Reads and deserialises the saved game state from the JSON file at the given path.
   *
   * @param source the absolute or relative path to the {@code .millions} save file
   * @return a {@link GameStateDto} representing the deserialised game state; never {@code null}
   * @throws DataAccessException if the file does not exist at {@code source}, or if the JSON
   *     content cannot be mapped to a {@link GameStateDto}
   */
  @Override
  public GameStateDto read(String source) throws DataAccessException {
    File file = new File(source);
    if (!file.exists()) {
      throw new DataAccessException("Save file not found: " + source);
    }
    try {
      return mapper.readValue(file, GameStateDto.class);
    } catch (IOException e) {
      throw new DataAccessException("Failed to parse game state from: " + source, e);
    }
  }
}
