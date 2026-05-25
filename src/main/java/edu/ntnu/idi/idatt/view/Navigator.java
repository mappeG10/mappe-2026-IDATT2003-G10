package edu.ntnu.idi.idatt.view;

import edu.ntnu.idi.idatt.controller.GameController;
import edu.ntnu.idi.idatt.controller.dto.GameSummary;
import edu.ntnu.idi.idatt.controller.dto.GameSetup;
import edu.ntnu.idi.idatt.controller.init.GameFactory;
import edu.ntnu.idi.idatt.dal.exception.DataAccessException;
import edu.ntnu.idi.idatt.view.util.ViewUtility;
import edu.ntnu.idi.idatt.view.screen.*;
import edu.ntnu.idi.idatt.view.screen.tabs.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Navigator {

  private static final double WIDTH = 1280, HEIGHT = 800;

  private final Stage stage;
  private MainView mainView;
  private GameController gameController;

  public Navigator(Stage stage) {
    this.stage = stage;
  }

  public void toStart(){
    StartView startView = new StartView(
        this::handleOnStartRequested,
        this::handleOnLoadRequested);

    Scene scene = new Scene(startView, WIDTH, HEIGHT);
    applyStylesheet(scene);
    stage.setScene(scene);
    stage.setFullScreen(true);
  }

  private void handleInitRequested(String errorTitle, String errorPrefix,
      String successTitle, String successMessage, GameControllerSupplier supplier) {
    try {
      GameController gc = supplier.get();
      if (successTitle != null) {
        ViewUtility.showSuccessAlert(successTitle, successMessage);
      }
      this.toGame(gc);
    } catch (DataAccessException | IOException e){
      ViewUtility.showErrorAlert(errorTitle,
          errorPrefix + ": " + e.getMessage());
    } catch (Exception e){
      ViewUtility.showErrorAlert(errorTitle,
          "Unexpected error: " + e.getMessage());
    }
  }
  private void handleOnStartRequested(GameSetup gameSetup) {
    handleInitRequested("Start Error", "Could not start game", null, null,
        () -> GameFactory.createController(gameSetup));
  }
  private void handleOnLoadRequested(String path) {
    handleInitRequested("Load Error", "Could not load save file",
        "Game Loaded", "Your save file was loaded successfully.",
        () -> GameFactory.createControllerFromSave(path));
  }

  public void toGame(GameController gc) {
    this.gameController = gc;
    this.mainView = new MainView(gc, this::navigateTo, this::finishGame);

    this.navigateTo(GameTab.MARKET);
    Scene scene = new Scene(this.mainView, WIDTH, HEIGHT);
    applyStylesheet(scene);
    stage.setScene(scene);
    stage.setFullScreen(true);
  }

  public void toSummary(GameSummary summary) {
    SummaryView summaryView = new SummaryView(summary, this::toStart);
    Scene scene = new Scene(summaryView, WIDTH, HEIGHT);
    applyStylesheet(scene);
    stage.setScene(scene);
    stage.setFullScreen(true);
  }

  private void finishGame() {
    GameSummary summary = gameController.getSummaryController().finishGame();
    toSummary(summary);
  }

  private void applyStylesheet(Scene scene) {
    String css = getClass().getResource("/style.css").toExternalForm();
    scene.getStylesheets().add(css);
  }

  public void navigateTo(GameTab tab) {
    Parent content = switch (tab) {
      case MARKET -> new MarketView(gameController.getMarketController());
      case PORTFOLIO ->  new PortfolioView(gameController.getPortfolioController());
      case HISTORY ->  new TransactionHistoryView(gameController.getTransactionHistoryController());
      case DASHBOARD -> new DashboardView(gameController.getDashboardController());
      case SETTINGS -> new SettingsView(gameController);
    };

    mainView.setContent(content);
  }

  @FunctionalInterface
  private interface GameControllerSupplier {
    GameController get() throws DataAccessException, IOException;
  }

}
