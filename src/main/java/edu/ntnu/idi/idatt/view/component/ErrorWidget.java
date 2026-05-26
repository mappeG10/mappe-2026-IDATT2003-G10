package edu.ntnu.idi.idatt.view.component;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Modal dialog widget for displaying an error message to the player.
 *
 * <p>Rendered with a red-tinted header, an exclamation-mark badge, and two buttons:
 * "Cancel" (closes without action) and "OK" (also closes). The dialog is opened via
 * {@link #openDialog(javafx.stage.Window)} inherited from {@link BaseModal}.</p>
 *
 * <p>Convenience method: {@link edu.ntnu.idi.idatt.view.util.ViewUtility#showErrorAlert}.</p>
 */
public class ErrorWidget extends BaseModal<String> {

  /**
   * Constructs and immediately lays out an error dialog with the given title and message.
   *
   * @param title   the heading displayed in the coloured alert header
   * @param message the error description shown in the alert body
   */
  public ErrorWidget(String title, String message) {
    super(message);
    setupUI();
    titleLabel.setText(title);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setupUI() {
    getStyleClass().add("widget-root");
    setSpacing(0);

    Label badgeLabel = new Label("!");
    badgeLabel.getStyleClass().addAll("alert-icon-badge", "alert-icon-badge-error", "alert-badge-text");

    this.titleLabel = new Label("Error");
    this.titleLabel.getStyleClass().add("alert-title");

    HBox header = new HBox(12, badgeLabel, titleLabel);
    header.getStyleClass().addAll("alert-header", "alert-header-error");

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
    this.closeButton.getStyleClass().add("btn-alert-ok-error");

    HBox btnRow = new HBox(12, cancelButton, closeButton);
    btnRow.getStyleClass().add("alert-btn-row");

    getChildren().addAll(header, body, divider, btnRow);
  }
}
