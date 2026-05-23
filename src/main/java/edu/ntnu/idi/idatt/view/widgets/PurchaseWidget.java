package edu.ntnu.idi.idatt.view.widgets;

import edu.ntnu.idi.idatt.controllers.MarketController;
import edu.ntnu.idi.idatt.controllers.TransactionPreview;
import edu.ntnu.idi.idatt.models.Stock;
import edu.ntnu.idi.idatt.view.ViewUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;

public class PurchaseWidget extends TransactionWidget<Stock> {
  private final MarketController controller;
  private Label grossCostLabel;
  private Label comissionLabel;

  public PurchaseWidget(Stock target, MarketController controller) {
    super(target);
    this.controller = controller;
  }


  //TODO: Fix styling later
  @Override
  protected void setupUI() {
    this.totalLabel = new Label("Buy: " + target.getSymbol());
    this.quantityField = new TextField();
    this.quantityField.setPromptText("0");

    this.grossCostLabel = new Label("Gross: $0.00");
    this.comissionLabel = new Label("Comission: $0.00");
    this.totalLabel = new Label("Total: %0.00");

    this.cancelButton = new Button("Cancel");
    this.actionButton = new Button("Confirm purchase");

    this.setSpacing(15);
    this.setMinWidth(250);

    this.getChildren().addAll(
        titleLabel,
        new Label("Quantity:"), quantityField,
        grossCostLabel,
        comissionLabel,
        totalLabel,
        new HBox(10, actionButton, cancelButton)
    );
  }

  @Override
  protected void updatedPreview(String quantityStr) {
    try {
      BigDecimal quantity = new BigDecimal(quantityStr);
      TransactionPreview preview = controller.previewBuy(target.getSymbol(), quantity);

      grossCostLabel.setText("Gross Cost" + ViewUtils.formatCurrency(preview.gross()));
      comissionLabel.setText("Comission (0.5%): " + ViewUtils.formatCurrency(preview.commission()));
      totalLabel.setText("Total: " + ViewUtils.formatCurrency(preview.total()));
    } catch (Exception e) {
      totalLabel.setText("Total: $0.00");
    }
  }

  @Override
  protected void handleAction() {
    try {
      BigDecimal quantity = new BigDecimal(quantityField.getText());
      controller.executeBuy(this.target.getSymbol(), quantity);
      requestClose();
    } catch (NumberFormatException e) { //TODO: Refactor from generic Exception Catching.
      ViewUtils.showErrorAlert("Unable to buy", e.getMessage());
    }
  }
}
