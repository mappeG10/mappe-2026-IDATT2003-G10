package edu.ntnu.idi.idatt.view.viewcontent;

import edu.ntnu.idi.idatt.controllers.dto.GameSetup;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigDecimal;
import java.util.function.Consumer;

public class StartView extends StackPane {

  private final TextField nameField = new TextField();
  private final TextField capitalField = new TextField();
  private final Label fileNameLabel = new Label("No file selected");
  private final Label errorLabel = new Label();
  private String csvPath;

  private final Consumer<GameSetup> onStartRequested;

  public StartView(Consumer<GameSetup> onStartRequested) {
    this.onStartRequested = onStartRequested;
    getStyleClass().add("start-view");
    getChildren().add(buildCard());
  }

  private VBox buildCard() {
    VBox card = new VBox(20);
    card.getStyleClass().add("start-card");
    card.getChildren().addAll(
        buildTitleSection(),
        buildDivider(),
        buildTextField("Player Name", nameField, "Enter your name"),
        buildTextField("Starting Capital ($)", capitalField, "e.g. 10 000"),
        buildFileField(),
        buildErrorLabel(),
        buildStartButton(),
        buildLoadButton()
    );
    return card;
  }

  private VBox buildTitleSection() {
    Label title = new Label("Millions");
    title.getStyleClass().add("start-title");

    Label subtitle = new Label("Stock Market Simulation");
    subtitle.getStyleClass().add("start-subtitle");

    VBox section = new VBox(8);
    section.getStyleClass().add("start-title-section");
    section.setAlignment(Pos.CENTER);
    section.getChildren().addAll(title, subtitle);
    return section;
  }

  private Separator buildDivider() {
    Separator sep = new Separator();
    sep.getStyleClass().add("start-divider");
    return sep;
  }

  private VBox buildTextField(String labelText, TextField field, String prompt) {
    Label label = new Label(labelText);
    label.getStyleClass().add("form-label");

    field.setPromptText(prompt);
    field.getStyleClass().add("text-field");
    field.setMaxWidth(Double.MAX_VALUE);

    VBox group = new VBox(6);
    group.getStyleClass().add("form-field");
    group.getChildren().addAll(label, field);
    return group;
  }

  private VBox buildFileField() {
    Label label = new Label("Stock Data File");
    label.getStyleClass().add("form-label");

    fileNameLabel.getStyleClass().add("file-name-display");
    HBox.setHgrow(fileNameLabel, Priority.ALWAYS);

    Button browseButton = new Button("Browse…");
    browseButton.getStyleClass().add("browse-button");
    browseButton.setOnAction(e -> handleBrowseFile());

    HBox fileRow = new HBox(8);
    fileRow.getStyleClass().add("file-row");
    fileRow.getChildren().addAll(fileNameLabel, browseButton);

    VBox group = new VBox(6);
    group.getStyleClass().add("form-field");
    group.getChildren().addAll(label, fileRow);
    return group;
  }

  private Label buildErrorLabel() {
    errorLabel.getStyleClass().add("error-label");
    errorLabel.setMaxWidth(Double.MAX_VALUE);
    errorLabel.setVisible(false);
    errorLabel.setManaged(false);
    return errorLabel;
  }

  private Button buildStartButton() {
    Button btn = new Button("Start Game →");
    btn.getStyleClass().add("btn-primary");
    btn.setMaxWidth(Double.MAX_VALUE);
    btn.setOnAction(e -> handleStartGame());
    return btn;
  }

  private Button buildLoadButton() {
    Button btn = new Button("Load Game");
    btn.getStyleClass().add("btn-secondary");
    btn.setMaxWidth(Double.MAX_VALUE);
    return btn;
  }

  private void handleBrowseFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("CSV File", "*.csv"),
        new FileChooser.ExtensionFilter("All Files", "*.*")
    );
    File file = fileChooser.showOpenDialog(getScene().getWindow());
    if (file != null) {
      csvPath = file.getAbsolutePath();
      fileNameLabel.setText(file.getName());
      showError(null);
    }
  }

  private void handleStartGame() {
    try {
      GameSetup setup = new GameSetup(
          nameField.getText(),
          new BigDecimal(capitalField.getText()),
          csvPath
      );
      showError(null);
      onStartRequested.accept(setup);
    } catch (NumberFormatException e) {
      showError("Please enter a valid number for starting capital.");
    } catch (IllegalArgumentException e) {
      showError(e.getMessage());
    }
  }

  private void showError(String message) {
    if (message == null || message.isBlank()) {
      errorLabel.setVisible(false);
      errorLabel.setManaged(false);
    } else {
      errorLabel.setText(message);
      errorLabel.setVisible(true);
      errorLabel.setManaged(true);
    }
  }
}
