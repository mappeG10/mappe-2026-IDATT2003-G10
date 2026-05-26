package edu.ntnu.idi.idatt.view;

/**
 * Enumerates the navigable tabs available within the main game screen.
 *
 * <p>Used by the {@link Navigator} and the sidebar in {@link
 * edu.ntnu.idi.idatt.view.screen.MainView} to identify which content panel to render in the centre
 * region of the layout.
 */
public enum GameTab {

  /** The market tab, where the player can browse stocks and execute purchases. */
  MARKET,

  /** The portfolio tab, showing the player's current holdings and allowing sales. */
  PORTFOLIO,

  /** The history tab, listing all committed transactions grouped by week. */
  HISTORY,

  /** The dashboard tab, showing financial summary, top movers, and the advance button. */
  DASHBOARD,

  /** The settings tab, providing save-game and fullscreen controls. */
  SETTINGS;
}
