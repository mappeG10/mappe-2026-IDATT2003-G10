package edu.ntnu.idi.idatt.view;

import edu.ntnu.idi.idatt.controllers.GameSetup;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigDecimal;
import java.util.function.Consumer;

public class StartView extends VBox {
  private final TextField nameField = new TextField();
  private final TextField capitalField = new TextField();
  private final Button startButton = new Button("Start Game");
  private final Label fileLabel = new Label("No file selected");
  private String csvPath;

  private final Consumer<GameSetup> onStartRequested;

  public StartView(Consumer<GameSetup> consumer) {
    //Style later
    this.setAlignment(Pos.CENTER);
    this.setSpacing(20);

    this.onStartRequested = consumer;
    this.getChildren().addAll(buildForm());
  }

  private VBox buildForm() {
    VBox form = new VBox(10);
    form.setAlignment(Pos.CENTER);
    form.setMaxWidth(300);

    Button browseButton = new Button("Choose a Stock file");

    browseButton.setOnAction(event -> handleBrowseFile());
    startButton.setOnAction(event -> handleStartGame());

    form.getChildren().addAll(
        new Label("Player Name:"), nameField,
        new Label("Starting Capital:"), capitalField,
        browseButton, fileLabel,
        startButton
    );
    return form;
  }

  private void handleBrowseFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("CSV File", "*.csv"),
        new FileChooser.ExtensionFilter("All Files", "*.*")
    );
    File file = fileChooser.showOpenDialog(this.getScene().getWindow());
    if (file != null) {
      this.csvPath = file.getAbsolutePath();
      this.fileLabel.setText(file.getName());
    }
  }


  private void handleStartGame() {
    try {
      GameSetup setup = new GameSetup(
          nameField.getText(),
          new BigDecimal(capitalField.getText()),
          csvPath
      );

      onStartRequested.accept(setup);
    }  catch (Exception e) { //TODO: Add a less generic exception catch here.
      ViewUtils.showErrorAlert("Input error", e.getMessage() + " Please verify your inputs");
    }
  }
}
