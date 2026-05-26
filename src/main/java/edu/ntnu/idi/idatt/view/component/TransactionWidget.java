package edu.ntnu.idi.idatt.view.component;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Abstract base class for buy and sell transaction modal widgets.
 *
 * <p>Provides the common UI scaffolding for transaction dialogs: a quantity input field,
 * a running total label, and an action button. Subclasses implement the preview update
 * logic and the action handler that commits the trade.</p>
 *
 * <p>Listeners are not attached during construction; subclasses must call
 * {@link #setupTransactionListeners()} after {@link #setupUI()} has populated
 * {@link #quantityField} and {@link #actionButton}.</p>
 *
 * @param <T> the type of the subject the transaction operates on (e.g.,
 *            {@link edu.ntnu.idi.idatt.model.Stock} for purchases or
 *            {@link edu.ntnu.idi.idatt.model.Share} for sales)
 */
public abstract class TransactionWidget<T> extends BaseModal<T> {

  protected TextField quantityField;
  protected Label totalLabel;
  protected Button actionButton;

  /**
   * Constructs a new transaction widget for the given target.
   *
   * @param target the subject of the transaction; must not be {@code null}
   */
  protected TransactionWidget(T target) {
    super(target);
  }

  /**
   * Wires the quantity field and action button to their respective handlers.
   *
   * <p>Must be called by the subclass after {@link #setupUI()} has assigned
   * {@link #quantityField} and {@link #actionButton}.</p>
   */
  protected void setupTransactionListeners() {
    if (quantityField != null) {
      quantityField.textProperty()
          .addListener((_, _, newValue) -> updatedPreview(newValue));
    }
    if (actionButton != null) {
      actionButton.setOnAction(event -> handleAction());
    }
  }

  /**
   * Updates the cost or proceeds preview based on the quantity currently entered.
   *
   * <p>Called every time the text in {@link #quantityField} changes. Implementations
   * should update the relevant labels in the widget and silently swallow
   * {@link IllegalArgumentException}s that occur when the input is not yet a valid number.</p>
   *
   * @param quantity the raw text currently in the quantity field; may be empty or invalid
   */
  protected abstract void updatedPreview(String quantity);

  /**
   * Executes the trade when the player clicks the action button.
   *
   * <p>Implementations should validate the quantity, invoke the appropriate controller
   * method, close this dialog, and open a {@link ReceiptWidget} on success. Exceptions
   * such as {@link edu.ntnu.idi.idatt.model.exception.InsufficientFundsException} or
   * {@link edu.ntnu.idi.idatt.model.exception.InsufficientSharesException} should be
   * shown to the player via {@link edu.ntnu.idi.idatt.view.util.ViewUtility#showErrorAlert}.</p>
   */
  protected abstract void handleAction();
}
