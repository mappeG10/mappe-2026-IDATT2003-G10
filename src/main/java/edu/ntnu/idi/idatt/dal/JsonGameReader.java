package edu.ntnu.idi.idatt.dal;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idi.idatt.dal.dto.GameStateDto;
import edu.ntnu.idi.idatt.dal.exception.DataAccessException;
import java.io.File;
import java.io.IOException;

public class JsonGameReader implements DataReader<GameStateDto> {
  private final ObjectMapper mapper;

  public JsonGameReader() {
    this.mapper = new ObjectMapper();
  }

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
