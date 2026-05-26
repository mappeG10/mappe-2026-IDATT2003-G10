package edu.ntnu.idi.idatt.view.component;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Abstract base class for confirmation modal dialogs.
 *
 * <p>Builds the shared layout: a title, a body description, a Cancel button that dismisses the
 * dialog, and a confirm button whose style and label are supplied by the subclass. The confirm
 * button is wired to {@link #handleConfirm()}, which subclasses must implement.
 *
 * <p>Concrete subclasses supply the text and CSS specifics via the protected constructor — see
 * {@link FinishGameWidget} and {@link ExitGameWidget}.
 */
public abstract class ConfirmWidget extends BaseModal<Void> {

  private final String title;
  private final String body;
  private final String confirmLabel;
  private final String confirmStyleClass;

  /**
   * Constructs and immediately lays out a confirmation dialog.
   *
   * @param title the heading shown at the top of the dialog
   * @param body the descriptive text explaining what will happen on confirmation
   * @param confirmLabel the label for the confirm action button
   * @param confirmStyleClass the CSS class applied to the confirm button
   */
  protected ConfirmWidget(
      String title, String body, String confirmLabel, String confirmStyleClass) {
    super(null);
    this.title = title;
    this.body = body;
    this.confirmLabel = confirmLabel;
    this.confirmStyleClass = confirmStyleClass;
    setupUi();
  }

  /** {@inheritDoc} */
  @Override
  protected void setupUi() {
    getStyleClass().add("widget-root");

    this.titleLabel = new Label(title);
    this.titleLabel.getStyleClass().add("widget-title");

    Label bodyLabel = new Label(body);
    bodyLabel.getStyleClass().add("widget-subtitle");
    bodyLabel.setWrapText(true);

    this.closeButton = new Button("Cancel");
    this.closeButton.getStyleClass().add("btn-cancel");
    this.closeButton.setMaxWidth(Double.MAX_VALUE);

    Button confirmBtn = new Button(confirmLabel);
    confirmBtn.getStyleClass().add(confirmStyleClass);
    confirmBtn.setMaxWidth(Double.MAX_VALUE);
    confirmBtn.setOnAction(e -> handleConfirm());

    HBox.setHgrow(closeButton, Priority.ALWAYS);
    HBox.setHgrow(confirmBtn, Priority.ALWAYS);
    HBox buttonsRow = new HBox(8, closeButton, confirmBtn);

    setSpacing(12);
    getChildren().addAll(titleLabel, bodyLabel, buttonsRow);
  }

  /**
   * Executes the action when the player clicks the confirm button.
   *
   * <p>Implementations should perform the intended action (e.g. end the game or exit the app) and
   * call {@link #requestClose()} if the dialog should close afterwards.
   */
  protected abstract void handleConfirm();
}
