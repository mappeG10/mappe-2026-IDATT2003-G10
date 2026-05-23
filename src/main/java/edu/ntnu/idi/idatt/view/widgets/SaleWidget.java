package edu.ntnu.idi.idatt.view.widgets;

import edu.ntnu.idi.idatt.controllers.PortfolioController;
import edu.ntnu.idi.idatt.controllers.TransactionPreview;
import edu.ntnu.idi.idatt.models.Share;
import edu.ntnu.idi.idatt.view.ViewUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SaleWidget extends TransactionWidget<Share> {
  private final PortfolioController controller;
  private Label grossLabel;
  private Label taxLabel;

  public SaleWidget(Share target, PortfolioController controller) {
    super(target);
    this.controller = controller;
  }

  @Override
  protected void setupUI() {
    this.titleLabel = new Label("Sell: " + target.getStock().getSymbol());

    this.quantityField = new TextField("Selling: " //TODO: Implement partial share sale later.
        + target.getQuantity().toString()
        + " Shares @ "
        + ViewUtils.formatCurrency(target.getCurrentValue()));
    this.quantityField.setEditable(false);

    this.grossLabel = new Label("Gross Proceeds: $0.00");
    this.taxLabel = new Label("Tax: $0.00");
    this.totalLabel = new Label("Net Proceeds: $0.00");

    this.cancelButton = new Button("Cancel");
    this.actionButton = new Button("Confirm Sale");

    this.getChildren().addAll(
        titleLabel,
        quantityField,
        grossLabel,
        taxLabel,
        totalLabel,
        new HBox(10, actionButton, cancelButton)
    );

  }

  @Override
  protected void updatedPreview(String quantity) {
    TransactionPreview preview = controller.previewSell(target);

    grossLabel.setText("Gross Proceeds: " + ViewUtils.formatCurrency(preview.gross()));
    taxLabel.setText("Capital Gains Tax (22%): " + ViewUtils.formatCurrency(preview.tax()));
    totalLabel.setText("Net Proceeds: " + ViewUtils.formatCurrency(preview.total()));
  }

  @Override
  protected void handleAction() {
    try {
      controller.executeSell(target);
      requestClose();
    } catch (Exception e) {
      ViewUtils.showErrorAlert("Unnable to sell", e.getMessage());
    }
  }
}
