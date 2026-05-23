package edu.ntnu.idi.idatt.view.widgets;

import edu.ntnu.idi.idatt.controllers.PortfolioController;
import edu.ntnu.idi.idatt.controllers.TransactionPreview;
import edu.ntnu.idi.idatt.models.Share;
import edu.ntnu.idi.idatt.view.ViewUtils;
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
    updatedPreview(target.getQuantity().toPlainString());
  }

  @Override
  protected void setupUI() {
    this.titleLabel = new Label("Sell: " + target.getStock().getSymbol());

    this.quantityField = new TextField(target.getQuantity().toPlainString());

    this.grossValueLabel = new Label();
    this.taxValueLabel = new Label();
    this.totalLabel = new Label();

    this.cancelButton = new Button("Cancel");
    this.actionButton = new Button("Confirm Sale");

    this.setSpacing(15);
    this.setMinWidth(250);

    this.getChildren().addAll(
        titleLabel,
        new Label(target.getStock().getCompany() + " · Current price: " + ViewUtils.formatCurrency(target.getStock().getSalesPrice())),
        new Label("Quantity (max: " + target.getQuantity().toPlainString() + "):"),
        quantityField,
        buildSummaryRow(),
        new HBox(10, actionButton, cancelButton)
    );
  }

  private HBox buildSummaryRow() {
    VBox keys = new VBox(
        new Label("Gross Proceeds"),
        new Label("Capital Gains Tax (30%)"),
        new Label("Net Proceeds")
    );
    VBox values = new VBox(
        grossValueLabel,
        taxValueLabel,
        totalLabel
    );
    return new HBox(keys, values);
  }

  @Override
  protected void updatedPreview(String quantityStr) {
    try {
      BigDecimal quantity = new BigDecimal(quantityStr);
      TransactionPreview preview = controller.previewSell(target, quantity);
      grossValueLabel.setText(ViewUtils.formatCurrency(preview.gross()));
      taxValueLabel.setText(ViewUtils.formatCurrency(preview.tax()));
      totalLabel.setText(ViewUtils.formatCurrency(preview.total()));
    } catch (Exception _) {
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
      ViewUtils.showErrorAlert("Invalid quantity", "Please enter a valid number");
    } catch (IllegalArgumentException e) {
      ViewUtils.showErrorAlert("Unable to sell", e.getMessage());
    }
  }
}
