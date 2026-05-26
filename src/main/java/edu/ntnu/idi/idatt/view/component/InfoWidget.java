package edu.ntnu.idi.idatt.view.component;

/**
 * Modal dialog widget for displaying an informational message to the player.
 *
 * <p>Rendered with a blue-tinted header, an "i" badge, and two buttons: "Cancel" and "OK". Used to
 * confirm successful operations such as saving the game or loading a save file. The dialog is
 * opened via {@link #openDialog(javafx.stage.Window)} inherited from {@link BaseModal}.
 *
 * <p>Convenience method: {@link edu.ntnu.idi.idatt.view.util.ViewUtility#showSuccessAlert}.
 */
public class InfoWidget extends AlertWidget {

  /**
   * Constructs and immediately lays out an informational dialog with the given title and message.
   *
   * @param title the heading displayed in the coloured alert header
   * @param message the informational text shown in the alert body
   */
  public InfoWidget(String title, String message) {
    super(title, message, "i", "info");
  }
}
