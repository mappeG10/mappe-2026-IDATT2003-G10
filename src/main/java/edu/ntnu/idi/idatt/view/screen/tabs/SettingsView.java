package edu.ntnu.idi.idatt.view.screen.tabs;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SettingsView extends VBox {


  public SettingsView() {
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

    VBox finishGameCard = createSettingCard(
        "Finish Game",
        "Sell all shares and end the session",
        "Finish",
        this::handleFinishGame
    );

    HBox.setHgrow(fullscreenCard, Priority.ALWAYS);
    HBox.setHgrow(saveGameCard,   Priority.ALWAYS);
    HBox.setHgrow(finishGameCard, Priority.ALWAYS);
    cardsContainer.getChildren().addAll(fullscreenCard, saveGameCard, finishGameCard);


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

    System.out.println("Save progression hook triggered");
  }

  private void handleFinishGame() {

    System.out.println("Finish session hook triggered");

  }
}
