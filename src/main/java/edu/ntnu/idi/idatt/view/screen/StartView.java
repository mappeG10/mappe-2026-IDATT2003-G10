package edu.ntnu.idi.idatt.view.screen;

import edu.ntnu.idi.idatt.controller.dto.GameSetup;
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

/**
 * The application's start screen where the player configures a new game or loads a saved one.
 *
 * <p>Provides a centred card form with fields for the player name, starting capital, and a
 * CSV stock-data file selection. Input validation is performed in-line: a non-numeric capital
 * or a missing file selection causes an error label to appear. The "Start Game" button fires
 * the {@code onStartRequested} callback with a fully populated {@link GameSetup}; the
 * "Load Game" button opens a file chooser and fires the {@code onLoadRequested} callback.</p>
 */
public class StartView extends StackPane {

  private final TextField nameField = new TextField();
  private final TextField capitalField = new TextField();
  private final Label fileNameLabel = new Label("No file selected");
  private final Label errorLabel = new Label();
  private String sourcePath;

  private final Consumer<GameSetup> onStartRequested;
  private final Consumer<String> onLoadRequested;

  /**
   * Constructs the start view and wires the provided callbacks to the form buttons.
   *
   * @param onStartRequested a callback invoked with a {@link GameSetup} when the player
   *                         submits a valid new-game form
   * @param onLoadRequested  a callback invoked with the absolute path of the selected save
   *                         file when the player chooses to load a game
   */
  public StartView(Consumer<GameSetup> onStartRequested, Consumer<String> onLoadRequested) {
    this.onStartRequested = onStartRequested;
    this.onLoadRequested = onLoadRequested;

    getStyleClass().add("start-view");
    getChildren().add(buildCard());
  }

  /**
   * Builds the centred card containing all form fields and action buttons.
   *
   * @return the assembled card {@link VBox}
   */
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

  /**
   * Builds the title section containing the application name and tagline.
   *
   * @return the assembled title {@link VBox}
   */
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

  /**
   * Builds a horizontal separator for use between form sections.
   *
   * @return a styled {@link Separator}
   */
  private Separator buildDivider() {
    Separator sep = new Separator();
    sep.getStyleClass().add("start-divider");
    return sep;
  }

  /**
   * Builds a labelled text-field form group.
   *
   * @param labelText the text of the field's descriptive label
   * @param field     the {@link TextField} to include in the group
   * @param prompt    the placeholder text shown inside the field when empty
   * @return the assembled form-field {@link VBox}
   */
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

  /**
   * Builds the stock-data file selection field with a browse button.
   *
   * @return the assembled file-field {@link VBox}
   */
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

  /**
   * Builds the inline error label, initially hidden.
   *
   * @return the configured error {@link Label}
   */
  private Label buildErrorLabel() {
    errorLabel.getStyleClass().add("error-label");
    errorLabel.setMaxWidth(Double.MAX_VALUE);
    errorLabel.setVisible(false);
    errorLabel.setManaged(false);
    return errorLabel;
  }

  /**
   * Builds the primary "Start Game" action button.
   *
   * @return the configured start {@link Button}
   */
  private Button buildStartButton() {
    Button btn = new Button("Start Game →");
    btn.getStyleClass().add("btn-primary");
    btn.setMaxWidth(Double.MAX_VALUE);
    btn.setOnAction(e -> handleStartGame());
    return btn;
  }

  /**
   * Builds the secondary "Load Game" action button.
   *
   * @return the configured load {@link Button}
   */
  private Button buildLoadButton() {
    Button btn = new Button("Load Game");
    btn.getStyleClass().add("btn-secondary");
    btn.setMaxWidth(Double.MAX_VALUE);
    btn.setOnAction(e -> handleLoadGame());
    return btn;
  }

  /**
   * Opens a file chooser filtered to CSV files and stores the selected path.
   */
  private void handleBrowseFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("CSV File", "*.csv"),
        new FileChooser.ExtensionFilter("All Files", "*.*")
    );
    File file = fileChooser.showOpenDialog(getScene().getWindow());
    if (file != null) {
      sourcePath = file.getAbsolutePath();
      fileNameLabel.setText(file.getName());
      showError(null);
    }
  }

  /**
   * Opens a file chooser filtered to {@code .millions} save files and fires the load callback.
   */
  private void handleLoadGame() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Load Millions Save File");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Millions Save File", "*.millions")
    );
    File file = fileChooser.showOpenDialog(getScene().getWindow());
    if (file != null) {
      onLoadRequested.accept(file.getAbsolutePath());
    }
  }

  /**
   * Validates the form and fires the start-game callback if all fields are valid.
   *
   * <p>Displays an inline error if the starting capital is not a valid number or if any
   * other {@link IllegalArgumentException} is raised during {@link GameSetup} construction.</p>
   */
  private void handleStartGame() {
    try {
      GameSetup setup = new GameSetup(
          nameField.getText(),
          new BigDecimal(capitalField.getText()),
          sourcePath
      );
      showError(null);
      onStartRequested.accept(setup);
    } catch (NumberFormatException e) {
      showError("Please enter a valid number for starting capital.");
    } catch (IllegalArgumentException e) {
      showError(e.getMessage());
    }
  }

  /**
   * Shows or hides the inline error label.
   *
   * @param message the error message to display, or {@code null} to hide the label
   */
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
