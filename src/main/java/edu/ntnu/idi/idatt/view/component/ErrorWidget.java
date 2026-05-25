package edu.ntnu.idi.idatt.view.component;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ErrorWidget extends BaseModal<String> {

  public ErrorWidget(String title, String message) {
    super(message);
    setupUI();
    titleLabel.setText(title);
  }

  @Override
  protected void setupUI() {
    getStyleClass().add("widget-root");

    this.titleLabel = new Label("Error");
    this.titleLabel.getStyleClass().add("widget-title");

    Label messageLabel = new Label(target);
    messageLabel.getStyleClass().add("error-label");
    messageLabel.setMaxWidth(Double.MAX_VALUE);
    messageLabel.setWrapText(true);

    this.closeButton = new Button("Close");
    this.closeButton.getStyleClass().add("btn-primary");
    this.closeButton.setMaxWidth(Double.MAX_VALUE);

    setSpacing(12);
    getChildren().addAll(titleLabel, messageLabel, closeButton);
  }
}
