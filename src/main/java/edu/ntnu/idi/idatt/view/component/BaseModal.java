package edu.ntnu.idi.idatt.view.component;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Abstract base class for all modal dialog widgets in the application.
 *
 * <p>A modal is a transparent {@link VBox} that is displayed in its own
 * {@link Stage} with {@link Modality#WINDOW_MODAL} blocking. Subclasses
 * are responsible for building their own UI in {@link #setupUI()} and may
 * expose a {@link #closeButton} that this class wires to {@link #requestClose()}
 * automatically when the dialog is opened.</p>
 *
 * @param <T> the type of the primary data object this widget operates on
 */
public abstract class BaseModal<T> extends VBox {

  protected final T target;
  protected Label titleLabel;
  protected Button closeButton;

  private Runnable onCloseRequested;

  /**
   * Constructs a new modal with the given target data object.
   *
   * @param target the data object the widget will display or operate on; may be {@code null}
   *               for widgets that require no input (e.g., confirmation dialogs)
   */
  protected BaseModal(T target) {
    this.target = target;
  }

  /**
   * Opens this widget as a modal dialog window anchored to the given owner.
   *
   * <p>The dialog is displayed with a transparent background and centred over the owner
   * window. If a {@link #closeButton} was configured in {@link #setupUI()}, it is
   * automatically wired to close the dialog.</p>
   *
   * @param owner the parent window the modal is attached to; may be {@code null}, in which
   *              case the dialog is not centred
   */
  public void openDialog(Window owner) {
    Stage stage = new Stage();
    stage.initStyle(StageStyle.TRANSPARENT);
    stage.initOwner(owner);
    stage.initModality(Modality.WINDOW_MODAL);

    Scene scene = new Scene(this, Color.TRANSPARENT);
    String css = BaseModal.class.getResource("/style.css").toExternalForm();
    scene.getStylesheets().add(css);
    stage.setScene(scene);

    setOnCloseRequested(stage::close);
    if (closeButton != null) {
      closeButton.setOnAction(e -> requestClose());
    }
    stage.show();

    if (owner != null) {
      stage.setX(owner.getX() + (owner.getWidth()  - stage.getWidth())  / 2);
      stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight()) / 2);
    }
    requestFocus();
  }

  /**
   * Registers a callback to be invoked when the dialog is requested to close.
   *
   * @param callback the action to perform when the dialog closes; must not be {@code null}
   */
  public void setOnCloseRequested(Runnable callback) {
    this.onCloseRequested = callback;
  }

  /**
   * Invokes the registered close callback to dismiss this dialog.
   *
   * <p>Has no effect if no callback has been registered.</p>
   */
  protected void requestClose() {
    if (onCloseRequested != null) {
      onCloseRequested.run();
    }
  }

  /**
   * Constructs and arranges the UI components for this modal dialog.
   *
   * <p>Subclasses must implement this method and typically assign values to
   * {@link #titleLabel} and {@link #closeButton} within it.</p>
   */
  protected abstract void setupUI();
}
