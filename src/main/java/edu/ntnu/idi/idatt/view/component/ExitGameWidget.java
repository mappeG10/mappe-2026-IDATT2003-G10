package edu.ntnu.idi.idatt.view.component;

import javafx.application.Platform;
import javafx.stage.Window;

/**
 * Confirmation dialog displayed before the player exits the application.
 *
 * <p>Informs the player that unsaved progress will be lost. The "Exit" button terminates the
 * JavaFX application via {@link Platform#exit()}; the "Cancel" button closes the dialog without
 * action.
 *
 * <p>Open via the static factory method {@link #open(Window)}.
 */
public class ExitGameWidget extends ConfirmWidget {

  /** Constructs and immediately lays out the exit-confirmation dialog. */
  public ExitGameWidget() {
    super(
        "Exit Game",
        "Are you sure you want to exit? Any unsaved progress will be lost.",
        "Exit",
        "btn-danger");
  }

  /**
   * Opens a new {@link ExitGameWidget} dialog anchored to the given owner window.
   *
   * @param owner the parent window to anchor the dialog to; may be {@code null}
   */
  public static void open(Window owner) {
    new ExitGameWidget().openDialog(owner);
  }

  /** {@inheritDoc} */
  @Override
  protected void handleConfirm() {
    Platform.exit();
  }
}
