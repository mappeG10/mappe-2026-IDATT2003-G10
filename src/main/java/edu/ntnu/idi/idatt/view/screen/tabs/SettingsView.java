package edu.ntnu.idi.idatt.view.screen.tabs;

import edu.ntnu.idi.idatt.controller.GameController;
import edu.ntnu.idi.idatt.dal.exception.DataAccessException;
import edu.ntnu.idi.idatt.view.util.ViewUtility;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class SettingsView extends VBox {
  private final GameController controller;

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

    VBox fullscreenCard = createSettingCard(
        "Fullscreen",
        "Toggle fullscreen mode",
        "Toggle",
        this::handleFullscreenToggle
    );

    VBox saveGameCard = createSettingCard(
        "Save Game",
        "Save your progress to a file",
        "Save",
        this::handleSaveGame
    );

    HBox.setHgrow(fullscreenCard, Priority.ALWAYS);
    HBox.setHgrow(saveGameCard,   Priority.ALWAYS);
    cardsContainer.getChildren().addAll(fullscreenCard, saveGameCard);


    this.getChildren().addAll(headerContainer, cardsContainer);
  }

  private VBox createSettingCard(String title, String description, String buttonText, Runnable action) {
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

  private void handleFullscreenToggle() {
    if (getScene() != null && getScene().getWindow() instanceof Stage stage) {
      stage.setFullScreen(!stage.isFullScreen());
    }
  }

  private void handleSaveGame() {
    String formattedSavePlayerName =
        controller.getPlayerName().toLowerCase()
            .replaceAll("\\s+", "_")
            .replaceAll("[^a-z0-9\\-_]", "");

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save game");
    fileChooser.setInitialFileName(formattedSavePlayerName);
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Millions Save File", "*.millions")
    );
    File file = fileChooser.showSaveDialog(getScene().getWindow());
    if (file != null) {
      try {
        controller.save(file.getAbsolutePath());
      } catch (DataAccessException e){
        ViewUtility.showErrorAlert("Save Error",
            "Could not save game: " + e.getMessage());
      } catch (Exception e) {
        ViewUtility.showErrorAlert("Save Error",
            "An unexpected error occurred saving: "
                + e.getMessage());
      }
    }
  }
}
