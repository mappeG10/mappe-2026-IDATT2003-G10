package edu.ntnu.idi.idatt.dal;

import edu.ntnu.idi.idatt.dal.dto.*;
import edu.ntnu.idi.idatt.dal.exception.DataAccessException;
import edu.ntnu.idi.idatt.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class JsonGameWriterTest {
  @TempDir Path tempDir;
  GameStateDto dto;


  @BeforeEach
  void setUp() {
    StockDto s = new StockDto("T", "Test", List.of(BigDecimal.ONE));
    ExchangeDto e = new ExchangeDto("Ex", 1, List.of(s));
    PlayerDto p = new PlayerDto("P", BigDecimal.ONE, BigDecimal.ONE,
        Player.Status.NOVICE, new PortfolioDto(List.of()), List.of());
    dto = new GameStateDto(p, e);
  }

  @Test
  void testWriteCreatesFile() throws DataAccessException {
    JsonGameWriter writer = new JsonGameWriter();
    Path path = tempDir.resolve("save.json");


    writer.write(path.toString(), dto);
    
    assertTrue(Files.exists(path), "File should be created on disk");
    assertTrue(path.toFile().length() > 0, "File should not be empty");
  }

  @Test
  void testWriteFailsOnInvalidPath() {
    JsonGameWriter writer = new JsonGameWriter();
    String invalidPath = tempDir.toString();

    assertThrows(DataAccessException.class, () -> writer.write(invalidPath, dto));
  }
}
