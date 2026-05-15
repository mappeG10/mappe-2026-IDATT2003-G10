package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.view.Navigator;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {

    primaryStage.setTitle("Millions");
    Navigator navigator = new Navigator(primaryStage);
    navigator.toStart();
    primaryStage.show();

  }
  public static void main(String[] args) {
    launch(args);
  }
}
