package edu.ntnu.idi.idatt.view.component;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public abstract class TransactionWidget<T> extends BaseModal<T> {

  protected TextField quantityField;
  protected Label totalLabel;
  protected Button actionButton;

  protected TransactionWidget(T target) {
    super(target);
    setupTransactionListeners();
  }

  private void setupTransactionListeners() {
    if (quantityField != null) {
      quantityField.textProperty()
          .addListener((_, _, newValue) -> updatedPreview(newValue));
    }
    if (actionButton != null) {
      actionButton.setOnAction(event -> handleAction());
    }
  }

  protected abstract void updatedPreview(String quantity);
  protected abstract void handleAction();
}
