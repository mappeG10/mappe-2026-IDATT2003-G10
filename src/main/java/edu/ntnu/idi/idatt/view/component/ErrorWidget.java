package edu.ntnu.idi.idatt.view.component;

/**
 * Modal dialog widget for displaying an error message to the player.
 *
 * <p>Rendered with a red-tinted header, an exclamation-mark badge, and two buttons: "Cancel"
 * (closes without action) and "OK" (also closes). The dialog is opened via {@link
 * #openDialog(javafx.stage.Window)} inherited from {@link BaseModal}.
 *
 * <p>Convenience method: {@link edu.ntnu.idi.idatt.view.util.ViewUtility#showErrorAlert}.
 */
public class ErrorWidget extends AlertWidget {

  /**
   * Constructs and immediately lays out an error dialog with the given title and message.
   *
   * @param title the heading displayed in the coloured alert header
   * @param message the error description shown in the alert body
   */
  public ErrorWidget(String title, String message) {
    super(title, message, "!", "error");
  }
}
