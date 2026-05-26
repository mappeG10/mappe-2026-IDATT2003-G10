package edu.ntnu.idi.idatt.view.component;

/**
 * Confirmation dialog displayed before the player ends the game session.
 *
 * <p>Informs the player that all remaining shares will be sold at market price. The "Confirm"
 * button invokes the provided {@code onConfirm} callback and then closes the dialog. The "Cancel"
 * button closes without taking any action.
 */
public class FinishGameWidget extends ConfirmWidget {

  private final Runnable onConfirm;

  /**
   * Constructs and immediately lays out the finish-game confirmation dialog.
   *
   * @param onConfirm the action to execute when the player confirms; typically the navigator's
   *     finish-game handler passed in from {@code MainView}
   */
  public FinishGameWidget(Runnable onConfirm) {
    super(
        "Finish Game",
        "This will sell all your shares at market price and end the session.",
        "Confirm",
        "btn-primary");
    this.onConfirm = onConfirm;
  }

  /** {@inheritDoc} */
  @Override
  protected void handleConfirm() {
    onConfirm.run();
    requestClose();
  }
}
