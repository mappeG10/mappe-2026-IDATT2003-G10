package edu.ntnu.idi.idatt.observer;

/**
 * Defines the contract for game entities that broadcast state-change notifications.
 *
 * <p>Classes implementing this interface maintain a list of {@link GameObserver} instances
 * and notify them when relevant internal state changes occur, following the Observer pattern.</p>
 */
public interface GameSubject {

  /**
   * Registers an observer to receive state-change notifications from this subject.
   *
   * <p>If the observer is already registered, this method has no effect.</p>
   *
   * @param observer the observer to register
   */
  void register(GameObserver observer);

  /**
   * Removes a previously registered observer so it no longer receives notifications.
   *
   * <p>If the observer is not currently registered, this method has no effect.</p>
   *
   * @param observer the observer to remove
   */
  void unregister(GameObserver observer);
}
