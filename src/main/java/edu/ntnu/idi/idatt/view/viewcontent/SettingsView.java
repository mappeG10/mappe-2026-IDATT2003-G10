package edu.ntnu.idi.idatt.view.viewcontent;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SettingsView extends VBox {


  public SettingsView() {
    this.setSpacing(20);

    Label titleLabel = new Label("Settings");
    Label subtitleLabel = new Label("Manage your game session");
    VBox headerContainer = new VBox(titleLabel, subtitleLabel);
    headerContainer.setSpacing(5);

    // 2. Options Cards Row
    HBox cardsContainer = new HBox();
    cardsContainer.setSpacing(20);

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

    cardsContainer.getChildren().addAll(fullscreenCard, saveGameCard, finishGameCard);


    this.getChildren().addAll(headerContainer, cardsContainer);
  }

  private VBox createSettingCard(String title, String description, String buttonText, Runnable action) {
    VBox card = new VBox();
    card.setSpacing(15);

    Label cardTitle = new Label(title);
    Label cardDescription = new Label(description);
    Button actionButton = new Button(buttonText);
    actionButton.setOnAction(e -> action.run());

    card.getChildren().addAll(cardTitle, cardDescription, actionButton);
    return card;
  }

  private void handleFullscreenToggle() {
    System.out.println("Toggle fullscreen mode");
  }

  private void handleSaveGame() {

    System.out.println("Save progression hook triggered");
  }

  private void handleFinishGame() {

    System.out.println("Finish session hook triggered");

  }
}
