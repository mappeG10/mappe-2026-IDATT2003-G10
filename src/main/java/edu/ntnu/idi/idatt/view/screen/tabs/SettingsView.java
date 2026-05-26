package edu.ntnu.idi.idatt.view.screen.tabs;

import edu.ntnu.idi.idatt.controller.GameController;
import edu.ntnu.idi.idatt.dal.exception.DataAccessException;
import edu.ntnu.idi.idatt.view.util.ViewUtility;
import java.io.File;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Tab view exposing session-level settings: fullscreen toggle and save-game export.
 *
 * <p>Each setting is presented as a card with a title, a short description, and an action button:
 *
 * <ul>
 *   <li><strong>Fullscreen</strong> — toggles the primary {@link Stage} between windowed and
 *       fullscreen mode.
 *   <li><strong>Save Game</strong> — opens a {@link FileChooser} filtered to {@code .millions}
 *       files, pre-filling the filename with a sanitised version of the player's name (lowercased,
 *       spaces replaced by underscores, non-alphanumeric characters removed). On success a
 *       confirmation alert is shown; on failure an error alert is shown.
 * </ul>
 */
public class SettingsView extends VBox {

  private final GameController controller;

  /**
   * Constructs the settings view and assembles the fullscreen and save-game cards.
   *
   * @param controller the top-level game controller used for save operations and player-name
   *     retrieval; must not be {@code null}
   */
  public SettingsView(GameController controller) {
    this.controller = controller;
    getStyleClass().add("content-view");

    Label titleLabel = new Label("Settings");
    titleLabel.getStyleClass().add("view-title");

    Label subtitleLabel = new Label("Manage your game session");
    subtitleLabel.getStyleClass().add("view-subtitle");

    VBox headerContainer = new VBox(4, titleLabel, subtitleLabel);

    HBox cardsContainer = new HBox();
    cardsContainer.getStyleClass().add("settings-cards-row");

    VBox fullscreenCard =
        createSettingCard(
            "Fullscreen", "Toggle fullscreen mode", "Toggle", this::handleFullscreenToggle);

    VBox saveGameCard =
        createSettingCard(
            "Save Game", "Save your progress to a file", "Save", this::handleSaveGame);

    HBox.setHgrow(fullscreenCard, Priority.ALWAYS);
    HBox.setHgrow(saveGameCard, Priority.ALWAYS);
    cardsContainer.getChildren().addAll(fullscreenCard, saveGameCard);

    this.getChildren().addAll(headerContainer, cardsContainer);
  }

  /**
   * Creates a setting card containing a title label, a description label, and an action button.
   *
   * @param title the heading displayed at the top of the card
   * @param description the short explanatory text displayed beneath the title
   * @param buttonText the label on the action button
   * @param action the callback invoked when the action button is clicked
   * @return the assembled setting-card {@link VBox}
   */
  private VBox createSettingCard(
      String title, String description, String buttonText, Runnable action) {
    VBox card = new VBox(12);
    card.getStyleClass().add("settings-card");

    Label cardTitle = new Label(title);
    cardTitle.getStyleClass().add("settings-card-title");

    Label cardDescription = new Label(description);
    cardDescription.getStyleClass().add("settings-card-desc");

    Button actionButton = new Button(buttonText);
    actionButton.setOnAction(e -> action.run());

    card.getChildren().addAll(cardTitle, cardDescription, actionButton);
    return card;
  }

  /**
   * Toggles the primary window between fullscreen and windowed mode.
   *
   * <p>Has no effect if the current scene or its window is not a {@link Stage}.
   */
  private void handleFullscreenToggle() {
    if (getScene() != null && getScene().getWindow() instanceof Stage stage) {
      stage.setFullScreen(!stage.isFullScreen());
    }
  }

  /**
   * Opens a save-file dialog pre-filled with a sanitised player name and writes the current game
   * state to the chosen path.
   *
   * <p>The suggested filename is derived by lowercasing the player's name, replacing runs of
   * whitespace with underscores, and stripping any character that is not a lowercase letter, digit,
   * hyphen, or underscore. The dialog is filtered to {@code *.millions} files.
   *
   * <p>Displays a success alert if the save completes, or an error alert if a {@link
   * DataAccessException} or any other exception is thrown during the write.
   */
  private void handleSaveGame() {
    String formattedSavePlayerName =
        controller
            .getPlayerName()
            .toLowerCase()
            .replaceAll("\\s+", "_")
            .replaceAll("[^a-z0-9\\-_]", "");

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save game");
    fileChooser.setInitialFileName(formattedSavePlayerName);
    fileChooser
        .getExtensionFilters()
        .add(new FileChooser.ExtensionFilter("Millions Save File", "*.millions"));
    File file = fileChooser.showSaveDialog(getScene().getWindow());
    if (file != null) {
      try {
        controller.save(file.getAbsolutePath());
        ViewUtility.showSuccessAlert("Save Successful", "Game saved successfully.");
      } catch (DataAccessException e) {
        ViewUtility.showErrorAlert("Save Error", "Could not save game: " + e.getMessage());
      } catch (Exception e) {
        ViewUtility.showErrorAlert(
            "Save Error", "An unexpected error occurred saving: " + e.getMessage());
      }
    }
  }
}
