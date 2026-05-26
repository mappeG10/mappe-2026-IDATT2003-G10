package edu.ntnu.idi.idatt.dal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.ntnu.idi.idatt.dal.dto.GameStateDto;
import edu.ntnu.idi.idatt.dal.exception.DataAccessException;
import java.io.File;
import java.io.IOException;

public class JsonGameWriter {
  private final ObjectMapper mapper;

  public JsonGameWriter() {
    this.mapper = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);
  }

  public void write(String destination, GameStateDto state) throws DataAccessException {
    try {
      mapper.writeValue(new File(destination), state);
    } catch (IOException e) {
      throw new DataAccessException("Failed to save game state to: " + destination, e);
    }
  }
}
