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

public abstract class BaseModal<T> extends VBox {

  protected final T target;
  protected Label titleLabel;
  protected Button closeButton;

  private Runnable onCloseRequested;

  protected BaseModal(T target) {
    this.target = target;
    if (closeButton != null) {
      closeButton.setOnAction(e -> requestClose());
    }
  }

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
    stage.show();

    if (owner != null) {
      stage.setX(owner.getX() + (owner.getWidth()  - stage.getWidth())  / 2);
      stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight()) / 2);
    }
    requestFocus();
  }

  public void setOnCloseRequested(Runnable callback) {
    this.onCloseRequested = callback;
  }

  protected void requestClose() {
    if (onCloseRequested != null) {
      onCloseRequested.run();
    }
  }

  protected abstract void setupUI();
}
