package edu.ntnu.idi.idatt.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

public class StartView extends VBox {
  private final TextField nameField = new TextField();
  private final TextField capitalField = new TextField();
  private final Button startButton = new Button("Start Game");
  private final Label fileLabel = new Label("No file selected");
  private String csvPath;

  public StartView() {
    //Style later

    this.setSpacing(10);
    this.setAlignment(Pos.CENTER);

    Button browseButton = new Button("Choose a Stock file");
    browseButton.setOnAction(event -> {
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
    });

    this.getChildren().addAll(
        new Label("Player Name:"), nameField,
        new Label("Starting Capital: "), capitalField,
        browseButton, fileLabel,
        startButton)
    ;
  }

  public String getPlayerName() {
    return nameField.getText();
  }
  public String getCapital() {
    return capitalField.getText();
  }
  public String getCsvPath() {
    return csvPath;
  }
  public Button getStartButton() {
    return startButton;
  }
}
