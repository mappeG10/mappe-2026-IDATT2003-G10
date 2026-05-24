package edu.ntnu.idi.idatt.view;

import edu.ntnu.idi.idatt.controllers.GameController;
import edu.ntnu.idi.idatt.controllers.GameFactory;
import edu.ntnu.idi.idatt.dal.DataAccessException;
import edu.ntnu.idi.idatt.view.viewcontent.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Navigator {
  private final double WIDTH = 1280, HEIGHT = 800; //TODO: Move this out for cleaner code.

  private final Stage stage;
  private MainView mainView;
  private GameController gameController;

  public Navigator(Stage stage) {
    this.stage = stage;
  }

  public void toStart(){
    StartView startView = new StartView(gameSetup -> {
      try {
        GameController gc = GameFactory.createController(gameSetup);
        this.toGame(gc);
      } catch (DataAccessException | IOException e){
        ViewUtils.showErrorAlert("Data Error",
            "Could not load Stocks: " + e.getMessage());
      } catch (Exception e) {
        ViewUtils.showErrorAlert("Error",
            "An unexpected error occurred: " + e.getMessage());
      }
    });

    Scene scene = new Scene(startView, WIDTH, HEIGHT);
    applyStylesheet(scene);
    stage.setScene(scene);
    stage.setFullScreen(true);
  }

  public void toGame(GameController gc) {
    this.gameController = gc;
    this.mainView = new MainView(gc, this::navigateTo);

    this.navigateTo(GameTab.MARKET);
    Scene scene = new Scene(this.mainView, WIDTH, HEIGHT);
    applyStylesheet(scene);
    stage.setScene(scene);
    stage.setFullScreen(true);
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
      case SETTINGS -> new SettingsView();
    };

    mainView.setContent(content);
  }

}
