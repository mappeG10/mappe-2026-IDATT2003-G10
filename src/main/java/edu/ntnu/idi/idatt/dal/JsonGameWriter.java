package edu.ntnu.idi.idatt.dal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.ntnu.idi.idatt.dal.dto.GameStateDto;
import edu.ntnu.idi.idatt.dal.exception.DataAccessException;
import java.io.File;
import java.io.IOException;

/**
 * Serialises a game state to a pretty-printed JSON file.
 *
 * <p>The output file format is intended to be human-readable, with indented JSON produced by
 * enabling {@link SerializationFeature#INDENT_OUTPUT}. The file extension used for saved games is
 * {@code .millions}, matched by {@link DataReaderFactory#getGameReader(String)} on load.
 */
public class JsonGameWriter {

  private final ObjectMapper mapper;

  /** Constructs a new {@code JsonGameWriter} with pretty-printing enabled. */
  public JsonGameWriter() {
    this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
  }

  /**
   * Serialises the given game state to a JSON file at the specified destination path.
   *
   * <p>If a file already exists at {@code destination}, it will be overwritten.
   *
   * @param destination the absolute or relative path to write the JSON file to
   * @param state the game state data transfer object to serialise; must not be {@code null}
   * @throws DataAccessException if the file cannot be written due to an I/O error
   */
  public void write(String destination, GameStateDto state) throws DataAccessException {
    try {
      mapper.writeValue(new File(destination), state);
    } catch (IOException e) {
      throw new DataAccessException("Failed to save game state to: " + destination, e);
    }
  }
}
