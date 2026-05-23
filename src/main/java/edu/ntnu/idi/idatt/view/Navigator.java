package edu.ntnu.idi.idatt.view;

import edu.ntnu.idi.idatt.controllers.GameController;
import edu.ntnu.idi.idatt.controllers.GameFactory;
import edu.ntnu.idi.idatt.view.viewcontent.DashboardView;
import edu.ntnu.idi.idatt.view.viewcontent.MarketView;
import edu.ntnu.idi.idatt.view.viewcontent.PortfolioView;
import edu.ntnu.idi.idatt.view.viewcontent.TransactionHistoryView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
      GameController gc = GameFactory.createController(gameSetup);
      this.toGame(gc);
    });
    stage.setScene(new Scene(startView, WIDTH, HEIGHT));
  }

  public void toGame(GameController gc) {
    this.gameController = gc;
    this.mainView = new MainView(gc, this::navigateTo);

    this.navigateTo(GameTab.MARKET);
    stage.setScene(new Scene(this.mainView, WIDTH, HEIGHT));
  }

  public void navigateTo(GameTab tab) {
    Parent content = switch (tab) {
      case MARKET -> new MarketView(gameController.getMarketController());
      case PORTFOLIO ->  new PortfolioView(gameController.getPortfolioController());
      case HISTORY ->  new TransactionHistoryView(gameController.getTransactionHistoryController());
      case DASHBOARD -> new DashboardView(gameController.getDashboardController());
    };

    mainView.setContent(content);
  }

}
