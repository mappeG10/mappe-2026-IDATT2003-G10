package edu.ntnu.idi.idatt.controllers;

import edu.ntnu.idi.idatt.models.Exchange;
import edu.ntnu.idi.idatt.models.Player;
import edu.ntnu.idi.idatt.observer.GameObserver;

public abstract class BaseController {
  protected final Exchange exchange;
  protected final Player player;

  protected BaseController(Exchange exchange, Player player) {
    this.exchange = exchange;
    this.player = player;
  }

  public void registerObserver(GameObserver observer) {
    exchange.register(observer);
    player.register(observer);
  }

  public void unregisterObserver(GameObserver observer) {
    exchange.unregister(observer);
    player.unregister(observer);
  }

}
