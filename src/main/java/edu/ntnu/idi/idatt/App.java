package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.view.Navigator;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Entry point for the Millions stock-market simulation application.
 *
 * <p>Bootstraps the JavaFX runtime and hands control over to the {@link Navigator}, which manages
 * all screen transitions for the lifetime of the application.
 */
public class App extends Application {

  /** Constructs a new {@code App} instance. Invoked by the JavaFX launcher. */
  public App() {}

  /**
   * Initialises the primary stage and navigates to the start screen.
   *
   * @param primaryStage the main window provided by the JavaFX runtime; must not be {@code null}
   * @throws Exception if any unchecked initialisation error occurs during startup
   */
  @Override
  public void start(Stage primaryStage) throws Exception {

    primaryStage.setTitle("Millions");
    Navigator navigator = new Navigator(primaryStage);
    navigator.toStart();
    primaryStage.show();
  }

  /**
   * Launches the JavaFX application.
   *
   * @param args command-line arguments forwarded to the JavaFX launcher; not currently used
   */
  public static void main(String[] args) {
    launch(args);
  }
}
