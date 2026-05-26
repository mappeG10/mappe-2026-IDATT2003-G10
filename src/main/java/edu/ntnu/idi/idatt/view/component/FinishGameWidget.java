package edu.ntnu.idi.idatt.view.component;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Confirmation dialog displayed before the player ends the game session.
 *
 * <p>Informs the player that all remaining shares will be sold at market price. The "Confirm"
 * button invokes the provided {@code onConfirm} callback and then closes the dialog. The "Cancel"
 * button closes without taking any action.
 */
public class FinishGameWidget extends BaseModal<Void> {

  private final Runnable onConfirm;

  /**
   * Constructs and immediately lays out the finish-game confirmation dialog.
   *
   * @param onConfirm the action to execute when the player confirms; typically the navigator's
   *     finish-game handler passed in from {@code MainView}
   */
  public FinishGameWidget(Runnable onConfirm) {
    super(null);
    setupUI();
    this.onConfirm = onConfirm;
  }

  /** {@inheritDoc} */
  @Override
  protected void setupUI() {
    getStyleClass().add("widget-root");

    this.titleLabel = new Label("Finish Game");
    this.titleLabel.getStyleClass().add("widget-title");

    Label bodyLabel =
        new Label("This will sell all your shares at market price and end the session.");
    bodyLabel.getStyleClass().add("widget-subtitle");
    bodyLabel.setWrapText(true);

    this.closeButton = new Button("Cancel");
    this.closeButton.getStyleClass().add("btn-cancel");
    this.closeButton.setMaxWidth(Double.MAX_VALUE);

    Button confirmBtn = new Button("Confirm");
    confirmBtn.getStyleClass().add("btn-primary");
    confirmBtn.setMaxWidth(Double.MAX_VALUE);
    confirmBtn.setOnAction(
        e -> {
          onConfirm.run();
          requestClose();
        });

    HBox.setHgrow(closeButton, Priority.ALWAYS);
    HBox.setHgrow(confirmBtn, Priority.ALWAYS);
    HBox buttonsRow = new HBox(8, closeButton, confirmBtn);

    setSpacing(12);
    getChildren().addAll(titleLabel, bodyLabel, buttonsRow);
  }
}
