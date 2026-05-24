package edu.ntnu.idi.idatt.view.component;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public abstract class TransactionWidget<T> extends VBox {
  protected final T target;
  protected Label titleLabel;
  protected TextField quantityField;
  protected Label totalLabel;
  protected Button cancelButton;
  protected Button actionButton;

  private Runnable onCloseRequested;

  protected TransactionWidget(T target) {
    this.target = target;
    setupUI();
    setupListeners();
  }

  public void openDialog(Window owner) {
    Stage stage = new Stage();
    stage.initStyle(StageStyle.TRANSPARENT);
    stage.initOwner(owner);
    stage.initModality(Modality.WINDOW_MODAL);

    Scene scene = new Scene(this, Color.TRANSPARENT);
    String css = TransactionWidget.class.getResource("/style.css").toExternalForm();
    scene.getStylesheets().add(css);
    stage.setScene(scene);

    this.setOnCloseRequested(stage::close);
    stage.show();

    if (owner != null) {
      stage.setX(owner.getX() + (owner.getWidth()  - stage.getWidth())  / 2);
      stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight()) / 2);
    }
    this.requestFocus();
  }

  public void setOnCloseRequested(Runnable callback) {
    this.onCloseRequested = callback;
  }

  protected void requestClose() {
    if (onCloseRequested != null) {
      onCloseRequested.run();
    }
  }

  private void setupListeners() {
    if (quantityField != null) {
      quantityField.textProperty()
          .addListener((_, _, newValue)
              -> updatedPreview(newValue));
    }
    if (actionButton != null) {
      actionButton.setOnAction(event -> handleAction());
    }
    if (cancelButton != null) {
      cancelButton.setOnAction(event -> requestClose());
    }
  }

  protected abstract void updatedPreview(String quantity);
  protected abstract void handleAction();
  protected abstract void setupUI();

}
