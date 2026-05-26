package edu.ntnu.idi.idatt.view.component;

import edu.ntnu.idi.idatt.controller.MarketController;
import edu.ntnu.idi.idatt.controller.dto.TransactionPreview;
import edu.ntnu.idi.idatt.controller.dto.TransactionReceipt;
import edu.ntnu.idi.idatt.model.Stock;
import edu.ntnu.idi.idatt.model.exception.InsufficientFundsException;
import edu.ntnu.idi.idatt.model.exception.StockNotFoundException;
import edu.ntnu.idi.idatt.model.exception.TransactionAlreadyCommittedException;
import edu.ntnu.idi.idatt.view.util.FormatUtil;
import edu.ntnu.idi.idatt.view.util.ViewUtility;
import java.math.BigDecimal;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Modal dialog for entering a stock purchase order.
 *
 * <p>Displays the stock's current price and provides a quantity input field whose value
 * is used to compute a live cost preview (gross, commission, total) via
 * {@link MarketController#previewBuy(String, BigDecimal)}. When the player confirms, the
 * purchase is executed, this dialog closes, and a {@link ReceiptWidget} is opened.</p>
 */
public class PurchaseWidget extends TransactionWidget<Stock> {

  private final MarketController controller;
  private Label grossValueLabel;
  private Label commissionValueLabel;

  /**
   * Constructs and immediately lays out a purchase dialog for the given stock.
   *
   * @param target     the stock to be purchased; must not be {@code null}
   * @param controller the market controller used to preview and execute the purchase;
   *                   must not be {@code null}
   */
  public PurchaseWidget(Stock target, MarketController controller) {
    super(target);
    this.controller = controller;
    setupUI();
    setupTransactionListeners();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setupUI() {
    getStyleClass().add("widget-root");

    this.titleLabel = new Label("Buy: " + target.getSymbol());
    this.titleLabel.getStyleClass().add("widget-title");

    Label subtitleLabel = new Label(target.getCompany() + " · Current price: " + FormatUtil.formatCurrency(target.getSalesPrice()));
    subtitleLabel.getStyleClass().add("widget-subtitle");

    this.quantityField = new TextField();
    this.quantityField.setPromptText("0");
    this.quantityField.getStyleClass().add("widget-input");

    this.grossValueLabel = new Label();
    this.commissionValueLabel = new Label();
    this.totalLabel = new Label();

    this.closeButton = new Button("Cancel");
    this.closeButton.getStyleClass().add("btn-cancel");
    this.actionButton = new Button("Confirm Purchase");
    this.actionButton.getStyleClass().add("btn-purchase");

    this.setSpacing(12);

    this.getChildren().addAll(
        titleLabel,
        subtitleLabel,
        new Label("Quantity:"),
        quantityField,
        buildSummaryRow(),
        new HBox(8, actionButton, closeButton)
    );
  }

  /**
   * Builds the cost summary panel showing gross cost, commission, and total.
   *
   * @return an {@link HBox} containing the aligned key-value rows
   */
  private HBox buildSummaryRow() {
    Label grossKey = new Label("Gross Cost");
    grossKey.getStyleClass().add("widget-label-key");
    Label commKey = new Label("Commission (0.5%)");
    commKey.getStyleClass().add("widget-label-key");
    Label totalKey = new Label("Total");
    totalKey.getStyleClass().add("widget-label-total");

    grossValueLabel.getStyleClass().add("widget-label-value");
    commissionValueLabel.getStyleClass().add("widget-label-value");
    totalLabel.getStyleClass().add("widget-label-total");

    VBox keys   = new VBox(6, grossKey, commKey, totalKey);
    VBox values = new VBox(6, grossValueLabel, commissionValueLabel, totalLabel);
    HBox row = new HBox(24, keys, values);
    row.getStyleClass().add("widget-summary");
    return row;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Updates the gross, commission, and total labels by previewing the purchase at the
   * entered quantity. Resets all labels to {@code $0.00} if the input is not a valid
   * positive number.</p>
   */
  @Override
  protected void updatedPreview(String quantityStr) {
    try {
      BigDecimal quantity = new BigDecimal(quantityStr);
      TransactionPreview preview = controller.previewBuy(target.getSymbol(), quantity);

      grossValueLabel.setText(FormatUtil.formatCurrency(preview.gross()));
      commissionValueLabel.setText(FormatUtil.formatCurrency(preview.commission()));
      totalLabel.setText(FormatUtil.formatCurrency(preview.total()));
    } catch (IllegalArgumentException ignoredException) {
      grossValueLabel.setText("$0.00");
      commissionValueLabel.setText("$0.00");
      totalLabel.setText("$0.00");
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>Parses the quantity field, executes the purchase via the controller, closes this
   * dialog, and opens a {@link ReceiptWidget} on success. Shows an error alert for invalid
   * input or business-rule violations.</p>
   */
  @Override
  protected void handleAction() {
    try {
      BigDecimal quantity = new BigDecimal(quantityField.getText());
      TransactionReceipt receipt = controller.executeBuy(this.target.getSymbol(), quantity);
      Window owner = ((Stage) getScene().getWindow()).getOwner();
      requestClose();
      new ReceiptWidget(receipt).openDialog(owner);
    } catch (NumberFormatException e) {
      ViewUtility.showErrorAlert("Invalid quantity", "Please enter a valid number");
    } catch (InsufficientFundsException e) {
      ViewUtility.showErrorAlert("Insufficient funds", e.getMessage());
    } catch (StockNotFoundException e) {
      ViewUtility.showErrorAlert("Stock not found", e.getMessage());
    } catch (TransactionAlreadyCommittedException e) {
      ViewUtility.showErrorAlert("Error", e.getMessage());
    } catch (IllegalArgumentException e) {
      ViewUtility.showErrorAlert("Unable to buy", e.getMessage());
    }
  }
}
