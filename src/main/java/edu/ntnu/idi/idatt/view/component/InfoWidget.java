package edu.ntnu.idi.idatt.view.component;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class InfoWidget extends BaseModal<String> {

  public InfoWidget(String title, String message) {
    super(message);
    setupUI();
    titleLabel.setText(title);
  }

  @Override
  protected void setupUI() {
    getStyleClass().add("widget-root");
    setSpacing(0);

    Label badgeLabel = new Label("i");
    badgeLabel.getStyleClass().addAll("alert-icon-badge", "alert-icon-badge-info", "alert-badge-text");

    this.titleLabel = new Label("Info");
    this.titleLabel.getStyleClass().add("alert-title");

    HBox header = new HBox(12, badgeLabel, titleLabel);
    header.getStyleClass().addAll("alert-header", "alert-header-info");

    Label messageLabel = new Label(target);
    messageLabel.getStyleClass().add("alert-message");
    VBox body = new VBox(messageLabel);
    body.getStyleClass().add("alert-body");

    Region divider = new Region();
    divider.getStyleClass().add("alert-divider");
    divider.setMaxWidth(Double.MAX_VALUE);

    Button cancelButton = new Button("Cancel");
    cancelButton.getStyleClass().add("btn-alert-cancel");
    cancelButton.setOnAction(e -> requestClose());

    this.closeButton = new Button("OK");
    this.closeButton.getStyleClass().add("btn-alert-ok-info");

    HBox btnRow = new HBox(12, cancelButton, closeButton);
    btnRow.getStyleClass().add("alert-btn-row");

    getChildren().addAll(header, body, divider, btnRow);
  }
}
