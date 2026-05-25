package edu.ntnu.idi.idatt.view.component;

import edu.ntnu.idi.idatt.controller.PortfolioController;
import edu.ntnu.idi.idatt.controller.dto.TransactionPreview;
import edu.ntnu.idi.idatt.model.Share;
import edu.ntnu.idi.idatt.model.exception.InsufficientSharesException;
import edu.ntnu.idi.idatt.model.exception.TransactionAlreadyCommittedException;
import edu.ntnu.idi.idatt.view.util.FormatUtil;
import edu.ntnu.idi.idatt.view.util.ViewUtility;
import java.math.BigDecimal;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SaleWidget extends TransactionWidget<Share> {
  private final PortfolioController controller;
  private Label grossValueLabel;
  private Label taxValueLabel;

  public SaleWidget(Share target, PortfolioController controller) {
    super(target);
    this.controller = controller;
    setupUI();
    setupTransactionListeners();
    closeButton.setOnAction(e -> requestClose());
    updatedPreview(target.getQuantity().toPlainString());
  }

  @Override
  protected void setupUI() {
    getStyleClass().add("widget-root");

    this.titleLabel = new Label("Sell: " + target.getSymbol());
    this.titleLabel.getStyleClass().add("widget-title");

    Label subtitleLabel = new Label(target.getCompany() + " · Current price: "
        + FormatUtil.formatCurrency(target.getCurrentPrice()));
    subtitleLabel.getStyleClass().add("widget-subtitle");

    this.quantityField = new TextField(target.getQuantity().toPlainString());
    this.quantityField.getStyleClass().add("widget-input");

    this.grossValueLabel = new Label();
    this.taxValueLabel = new Label();
    this.totalLabel = new Label();

    this.closeButton = new Button("Cancel");
    this.closeButton.getStyleClass().add("btn-cancel");
    this.actionButton = new Button("Confirm Sale");
    this.actionButton.getStyleClass().add("btn-sale");

    this.setSpacing(12);

    this.getChildren().addAll(
        titleLabel,
        subtitleLabel,
        new Label("Quantity (max: " + target.getQuantity().toPlainString() + "):"),
        quantityField,
        buildSummaryRow(),
        new HBox(8, actionButton, closeButton)
    );
  }

  private HBox buildSummaryRow() {
    Label grossKey = new Label("Gross Proceeds");
    grossKey.getStyleClass().add("widget-label-key");
    Label taxKey = new Label("Capital Gains Tax (30%)");
    taxKey.getStyleClass().add("widget-label-key");
    Label totalKey = new Label("Net Proceeds");
    totalKey.getStyleClass().add("widget-label-total");

    grossValueLabel.getStyleClass().add("widget-label-value");
    taxValueLabel.getStyleClass().add("widget-label-value");
    totalLabel.getStyleClass().add("widget-label-total");

    VBox keys   = new VBox(6, grossKey, taxKey, totalKey);
    VBox values = new VBox(6, grossValueLabel, taxValueLabel, totalLabel);
    HBox row = new HBox(24, keys, values);
    row.getStyleClass().add("widget-summary");
    return row;
  }

  @Override
  protected void updatedPreview(String quantityStr) {
    try {
      BigDecimal quantity = new BigDecimal(quantityStr);
      TransactionPreview preview = controller.previewSell(target, quantity);
      grossValueLabel.setText(FormatUtil.formatCurrency(preview.gross()));
      taxValueLabel.setText(FormatUtil.formatCurrency(preview.tax()));
      totalLabel.setText(FormatUtil.formatCurrency(preview.total()));
    } catch (IllegalArgumentException ignoredException) {
      grossValueLabel.setText("$0.00");
      taxValueLabel.setText("$0.00");
      totalLabel.setText("$0.00");
    }
  }

  @Override
  protected void handleAction() {
    try {
      BigDecimal quantity = new BigDecimal(quantityField.getText());
      controller.executeSell(target, quantity);
      requestClose();
    } catch (NumberFormatException e) {
      ViewUtility.showErrorAlert("Invalid quantity", "Please enter a valid number");
    } catch (InsufficientSharesException e) {
      ViewUtility.showErrorAlert("Insufficient shares", e.getMessage());
    } catch (TransactionAlreadyCommittedException e) {
      ViewUtility.showErrorAlert("Error", e.getMessage());
    } catch (IllegalArgumentException e) {
      ViewUtility.showErrorAlert("Unable to sell", e.getMessage());
    }
  }
}
