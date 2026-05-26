package edu.ntnu.idi.idatt.observer;

/**
 * Defines the contract for objects that observe state changes in the game.
 *
 * <p>Implementing classes register with a {@link GameSubject} and receive a callback via {@link
 * #update()} whenever the subject's state changes — for example, when a new trading week advances
 * or a transaction is committed.
 */
public interface GameObserver {

  /** Called by the observed {@link GameSubject} to notify this observer of a state change. */
  void update();
}
