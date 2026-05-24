package edu.ntnu.idi.idatt.observer;

public interface GameSubject {
  void register(GameObserver observer);
  void unregister(GameObserver observer);
}
