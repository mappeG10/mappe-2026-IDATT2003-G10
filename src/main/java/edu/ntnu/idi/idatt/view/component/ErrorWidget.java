package edu.ntnu.idi.idatt.view.component;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ErrorWidget extends TransactionWidget<String> {

  public ErrorWidget(String title, String message) {
    super(message);
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

    this.cancelButton = new Button("Close");
    this.cancelButton.getStyleClass().add("btn-primary");
    this.cancelButton.setMaxWidth(Double.MAX_VALUE);

    setSpacing(12);
    getChildren().addAll(titleLabel, messageLabel, cancelButton);
  }

  @Override
  protected void updatedPreview(String quantity) {}

  @Override
  protected void handleAction() {}
}
