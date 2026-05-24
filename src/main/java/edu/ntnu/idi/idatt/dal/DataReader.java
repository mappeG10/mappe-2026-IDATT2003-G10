package edu.ntnu.idi.idatt.dal;

import edu.ntnu.idi.idatt.dal.exceptions.DataAccessException;
import java.io.IOException;

public interface DataReader<T> {
  T read(String source) throws IOException, DataAccessException;
}
