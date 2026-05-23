package edu.ntnu.idi.idatt.dal;

import java.io.IOException;

public interface DataReader<T> {
  T read(String source) throws IOException, DataAccessException;
}
