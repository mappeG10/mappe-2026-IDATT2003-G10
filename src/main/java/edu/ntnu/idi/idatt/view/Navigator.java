package edu.ntnu.idi.idatt.view;

import edu.ntnu.idi.idatt.controllers.GameController;
import edu.ntnu.idi.idatt.controllers.GameFactory;
import edu.ntnu.idi.idatt.view.viewcontent.MarketView;
import edu.ntnu.idi.idatt.view.viewcontent.PortfolioView;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class Navigator {
  private final double WIDTH = 1280, HEIGHT = 800; //TODO: Move this out for cleaner code.

  private final Stage stage;
  private MainView mainView;
  private GameController gameController;

  public Navigator(Stage stage) {
    this.stage = stage;
  }

  public void toStart(){
    StartView startView = new StartView();

    startView.getStartButton().setOnAction(event -> {
      try {
        String name = startView.getPlayerName();
        BigDecimal capital = new BigDecimal(startView.getCapital());
        String csvPath = startView.getCsvPath();

        gameController = GameFactory.createController(name, capital, csvPath);
        this.toGame(gameController);
      } catch (Exception e) {
        ViewUtils.showErrorAlert("Setup Error", e.getMessage());
      }
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
      case HISTORY ->  new MarketView(gameController.getMarketController()); //TODO: Change this with the correct page
      default -> new MarketView(gameController.getMarketController()); //TODO: Change this with the correct page
    };

    mainView.setContent(content);
  }

}
