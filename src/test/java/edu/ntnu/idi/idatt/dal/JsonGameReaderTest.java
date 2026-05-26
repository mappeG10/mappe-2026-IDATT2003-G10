package edu.ntnu.idi.idatt.dal;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.idatt.dal.exception.DataAccessException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JsonGameReaderTest {
  @TempDir Path tempDir;
  private JsonGameReader reader;

  @BeforeEach
  void setUp() {
    reader = new JsonGameReader();
  }

  @Test
  void testReadInvalidJsonThrowsException() throws IOException {
    Path path = tempDir.resolve("bad.json");
    Files.writeString(path, "{ \"invalid\": \"json\" "); // Missing closing brace

    assertThrows(DataAccessException.class, () -> reader.read(path.toString()));
  }

  @Test
  void testReadNonExistentFileThrowsException() {
    assertThrows(DataAccessException.class, () -> reader.read("does_not_exist.json"));
  }
}
