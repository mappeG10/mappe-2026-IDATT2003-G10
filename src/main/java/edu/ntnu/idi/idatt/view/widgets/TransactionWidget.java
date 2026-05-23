package edu.ntnu.idi.idatt.view.widgets;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
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
    stage.initStyle(StageStyle.UNDECORATED);
    stage.initOwner(owner);
    stage.initModality(Modality.WINDOW_MODAL);
    stage.setScene(new Scene(this));

    this.setOnCloseRequested(stage::close);
    stage.show();
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
