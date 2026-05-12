package edu.ntnu.idi.idatt.models;

import edu.ntnu.idi.idatt.view.GameObserver;

public interface GameSubject {
  void register(GameObserver observer);
  void unregister(GameObserver observer);
}
