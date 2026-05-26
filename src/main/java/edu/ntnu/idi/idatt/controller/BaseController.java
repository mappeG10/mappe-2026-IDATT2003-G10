package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.model.Exchange;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.observer.GameObserver;

/**
 * Abstract base class providing shared state and observer management for all game controllers.
 *
 * <p>Every concrete controller receives a reference to the same {@link Exchange} and
 * {@link Player} instances, which represent the single source of truth for the game
 * session. This class also provides convenience methods to register and unregister a
 * {@link GameObserver} with both subjects simultaneously, so UI components only need
 * a single call to subscribe to the full game state.</p>
 */
public abstract class BaseController {

  /** The stock exchange for the current game session. */
  protected final Exchange exchange;

  /** The player participating in the current game session. */
  protected final Player player;

  /**
   * Constructs a new controller with the given exchange and player.
   *
   * @param exchange the stock exchange for this session; must not be {@code null}
   * @param player   the player for this session; must not be {@code null}
   */
  protected BaseController(Exchange exchange, Player player) {
    this.exchange = exchange;
    this.player = player;
  }

  /**
   * Registers an observer with both the {@link Exchange} and the {@link Player}.
   *
   * <p>The observer will receive update notifications whenever either the exchange state
   * (e.g., a week advance or a completed trade) or the player state (e.g., a balance
   * change or a status update) changes.</p>
   *
   * @param observer the observer to register; must not be {@code null}
   */
  public void registerObserver(GameObserver observer) {
    exchange.register(observer);
    player.register(observer);
  }

  /**
   * Removes an observer from both the {@link Exchange} and the {@link Player}.
   *
   * <p>If the observer is not currently registered with either subject, the call has
   * no effect for that subject.</p>
   *
   * @param observer the observer to remove
   */
  public void unregisterObserver(GameObserver observer) {
    exchange.unregister(observer);
    player.unregister(observer);
  }

}
