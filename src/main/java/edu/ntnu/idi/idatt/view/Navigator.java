package edu.ntnu.idi.idatt.view;

import edu.ntnu.idi.idatt.controller.GameController;
import edu.ntnu.idi.idatt.controller.dto.GameSetup;
import edu.ntnu.idi.idatt.controller.dto.GameSummary;
import edu.ntnu.idi.idatt.controller.init.GameFactory;
import edu.ntnu.idi.idatt.dal.exception.DataAccessException;
import edu.ntnu.idi.idatt.view.screen.MainView;
import edu.ntnu.idi.idatt.view.screen.StartView;
import edu.ntnu.idi.idatt.view.screen.SummaryView;
import edu.ntnu.idi.idatt.view.screen.tabs.DashboardView;
import edu.ntnu.idi.idatt.view.screen.tabs.MarketView;
import edu.ntnu.idi.idatt.view.screen.tabs.PortfolioView;
import edu.ntnu.idi.idatt.view.screen.tabs.SettingsView;
import edu.ntnu.idi.idatt.view.screen.tabs.TransactionHistoryView;
import edu.ntnu.idi.idatt.view.util.ViewUtility;
import java.io.IOException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Manages all screen transitions for the application.
 *
 * <p>The navigator is the single point responsible for constructing scenes, applying the global
 * stylesheet, and swapping the content of the primary {@link Stage}. It decouples individual
 * screens from each other: no screen holds a reference to another; instead, each screen accepts
 * callback lambdas that delegate back to the navigator.
 *
 * <p>The three top-level screens are:
 *
 * <ul>
 *   <li><strong>Start</strong> — collects setup information or loads a save file.
 *   <li><strong>Game</strong> — the main in-game shell with a sidebar and content area.
 *   <li><strong>Summary</strong> — displays end-game statistics after the session ends.
 * </ul>
 */
public class Navigator {

  private static final double WIDTH = 1280;
  private static final double HEIGHT = 800;
  private final Stage stage;
  private MainView mainView;
  private GameController gameController;

  /**
   * Constructs a new navigator bound to the given primary stage.
   *
   * @param stage the primary application window; must not be {@code null}
   */
  public Navigator(Stage stage) {
    this.stage = stage;
  }

  /** Navigates to the start screen, replacing the current scene. */
  public void toStart() {
    StartView startView = new StartView(this::handleOnStartRequested, this::handleOnLoadRequested);

    Scene scene = new Scene(startView, WIDTH, HEIGHT);
    applyStylesheet(scene);
    stage.setScene(scene);
    stage.setFullScreen(true);
  }

  /**
   * Handles game initialisation using the provided supplier, showing an error alert on failure.
   *
   * @param errorTitle the title for the error alert dialog
   * @param errorPrefix a prefix prepended to the exception message in the error dialog
   * @param successTitle the title for a success alert, or {@code null} to skip showing one
   * @param successMessage the body of the success alert, or {@code null} to skip showing one
   * @param supplier a functional interface that creates or loads a {@link GameController}
   */
  private void handleInitRequested(
      String errorTitle,
      String errorPrefix,
      String successTitle,
      String successMessage,
      GameControllerSupplier supplier) {
    try {
      GameController gc = supplier.get();
      if (successTitle != null) {
        ViewUtility.showSuccessAlert(successTitle, successMessage);
      }
      this.toGame(gc);
    } catch (DataAccessException | IOException e) {
      ViewUtility.showErrorAlert(errorTitle, errorPrefix + ": " + e.getMessage());
    } catch (Exception e) {
      ViewUtility.showErrorAlert(errorTitle, "Unexpected error: " + e.getMessage());
    }
  }

  /**
   * Handles a new-game request from the start screen.
   *
   * @param gameSetup the setup configuration submitted by the player
   */
  private void handleOnStartRequested(GameSetup gameSetup) {
    handleInitRequested(
        "Start Error",
        "Could not start game",
        null,
        null,
        () -> GameFactory.createController(gameSetup));
  }

  /**
   * Handles a load-game request from the start screen.
   *
   * @param path the absolute path to the {@code .millions} save file chosen by the player
   */
  private void handleOnLoadRequested(String path) {
    handleInitRequested(
        "Load Error",
        "Could not load save file",
        "Game Loaded",
        "Your save file was loaded successfully.",
        () -> GameFactory.createControllerFromSave(path));
  }

  /**
   * Navigates to the main game screen using the given controller.
   *
   * <p>The market tab is shown by default on entry.
   *
   * @param gc the fully initialised {@link GameController} for this session; must not be {@code
   *     null}
   */
  public void toGame(GameController gc) {
    this.gameController = gc;
    this.mainView = new MainView(gc, this::navigateTo, this::finishGame);

    this.navigateTo(GameTab.MARKET);
    Scene scene = new Scene(this.mainView, WIDTH, HEIGHT);
    applyStylesheet(scene);
    stage.setScene(scene);
    stage.setFullScreen(true);
  }

  /**
   * Navigates to the summary screen displaying the given end-game statistics.
   *
   * @param summary the final performance statistics produced by {@link
   *     edu.ntnu.idi.idatt.controller.SummaryController#finishGame()}
   */
  public void toSummary(GameSummary summary) {
    SummaryView summaryView = new SummaryView(summary, this::toStart);
    Scene scene = new Scene(summaryView, WIDTH, HEIGHT);
    applyStylesheet(scene);
    stage.setScene(scene);
    stage.setFullScreen(true);
  }

  /** Finalises the current game session and navigates to the summary screen. */
  private void finishGame() {
    GameSummary summary = gameController.getSummaryController().finishGame();
    toSummary(summary);
  }

  /**
   * Loads the global stylesheet and applies it to the given scene.
   *
   * @param scene the scene to style; must not be {@code null}
   */
  private void applyStylesheet(Scene scene) {
    String css = getClass().getResource("/style.css").toExternalForm();
    scene.getStylesheets().add(css);
  }

  /**
   * Replaces the content area of the main view with the panel for the specified tab.
   *
   * @param tab the tab to activate; must not be {@code null}
   */
  public void navigateTo(GameTab tab) {
    Parent content =
        switch (tab) {
          case MARKET -> new MarketView(gameController.getMarketController());
          case PORTFOLIO -> new PortfolioView(gameController.getPortfolioController());
          case HISTORY ->
              new TransactionHistoryView(gameController.getTransactionHistoryController());
          case DASHBOARD -> new DashboardView(gameController.getDashboardController());
          case SETTINGS -> new SettingsView(gameController);
        };

    mainView.setContent(content);
  }

  /**
   * Functional interface used internally to abstract over the two game-creation paths (new game and
   * load game), both of which may throw checked exceptions.
   */
  @FunctionalInterface
  private interface GameControllerSupplier {

    /**
     * Creates or restores a {@link GameController}.
     *
     * @return a fully initialised {@link GameController}
     * @throws DataAccessException if the data source cannot be read or parsed
     * @throws IOException if an I/O error occurs while reading the source
     */
    GameController get() throws DataAccessException, IOException;
  }
}
