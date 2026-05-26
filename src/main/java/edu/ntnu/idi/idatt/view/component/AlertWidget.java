package edu.ntnu.idi.idatt.view.component;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Abstract base class for alert-style modal dialogs (error, info, etc.).
 *
 * <p>Builds the shared layout: a coloured header with a badge icon, a message body, a horizontal
 * divider, and a Cancel / OK button row. The {@code variant} string is used as a CSS class suffix
 * (e.g. {@code "error"} → {@code alert-header-error}, {@code btn-alert-ok-error}).
 *
 * <p>Concrete subclasses only need to supply the badge text and variant name via the protected
 * constructor — see {@link ErrorWidget} and {@link InfoWidget}.
 */
public abstract class AlertWidget extends BaseModal<String> {

  private final String badgeText;
  private final String variant;

  /**
   * Constructs and immediately lays out an alert dialog.
   *
   * @param title the heading displayed in the coloured alert header
   * @param message the text shown in the alert body
   * @param badgeText the short symbol shown in the badge (e.g. {@code "!"} or {@code "i"})
   * @param variant the CSS variant suffix that drives header and button colours (e.g. {@code
   *     "error"} or {@code "info"})
   */
  protected AlertWidget(String title, String message, String badgeText, String variant) {
    super(message);
    this.badgeText = badgeText;
    this.variant = variant;
    setupUi();
    titleLabel.setText(title);
  }

  /** {@inheritDoc} */
  @Override
  protected void setupUi() {
    getStyleClass().add("widget-root");
    setSpacing(0);

    Label badgeLabel = new Label(badgeText);
    badgeLabel
        .getStyleClass()
        .addAll("alert-icon-badge", "alert-icon-badge-" + variant, "alert-badge-text");

    this.titleLabel = new Label();
    this.titleLabel.getStyleClass().add("alert-title");

    HBox header = new HBox(12, badgeLabel, titleLabel);
    header.getStyleClass().addAll("alert-header", "alert-header-" + variant);

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
    this.closeButton.getStyleClass().add("btn-alert-ok-" + variant);

    HBox btnRow = new HBox(12, cancelButton, closeButton);
    btnRow.getStyleClass().add("alert-btn-row");

    getChildren().addAll(header, body, divider, btnRow);
  }
}
