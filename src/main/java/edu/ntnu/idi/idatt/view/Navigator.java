package edu.ntnu.idi.idatt.view;

import edu.ntnu.idi.idatt.controllers.GameController;
import edu.ntnu.idi.idatt.controllers.GameFactory;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class Navigator {
  private final double WIDTH = 1280, HEIGHT = 800;
  private final Stage stage;


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

        GameController gameController = GameFactory.createController(name, capital, csvPath);
        this.toGame(gameController);
      } catch (Exception e) {
        ViewUtils.showErrorAlert("Setup Error", e.getMessage());
      }
    });
    stage.setScene(new Scene(startView, WIDTH, HEIGHT));
  }

  public void toGame(GameController gameController) {
    Label welcomeLabel = new Label("Welcome to the game, " + gameController.getPlayerName());
    stage.setScene(new Scene(welcomeLabel, WIDTH, HEIGHT));
  }

}
