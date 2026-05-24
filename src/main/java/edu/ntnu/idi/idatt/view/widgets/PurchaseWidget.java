package edu.ntnu.idi.idatt.view.widgets;

import edu.ntnu.idi.idatt.controllers.MarketController;
import edu.ntnu.idi.idatt.controllers.TransactionPreview;
import edu.ntnu.idi.idatt.models.Stock;
import edu.ntnu.idi.idatt.view.ViewUtils;
import java.math.BigDecimal;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PurchaseWidget extends TransactionWidget<Stock> {
  private final MarketController controller;
  private Label grossValueLabel;
  private Label commissionValueLabel;

  public PurchaseWidget(Stock target, MarketController controller) {
    super(target);
    this.controller = controller;
  }

  @Override
  protected void setupUI() {
    getStyleClass().add("widget-root");

    this.titleLabel = new Label("Buy: " + target.getSymbol());
    this.titleLabel.getStyleClass().add("widget-title");

    Label subtitleLabel = new Label(target.getCompany() + " · Current price: " + ViewUtils.formatCurrency(target.getSalesPrice()));
    subtitleLabel.getStyleClass().add("widget-subtitle");

    this.quantityField = new TextField();
    this.quantityField.setPromptText("0");
    this.quantityField.getStyleClass().add("widget-input");

    this.grossValueLabel = new Label();
    this.commissionValueLabel = new Label();
    this.totalLabel = new Label();

    this.cancelButton = new Button("Cancel");
    this.cancelButton.getStyleClass().add("btn-cancel");
    this.actionButton = new Button("Confirm Purchase");
    this.actionButton.getStyleClass().add("btn-purchase");

    this.setSpacing(12);

    this.getChildren().addAll(
        titleLabel,
        subtitleLabel,
        new Label("Quantity:"),
        quantityField,
        buildSummaryRow(),
        new HBox(8, actionButton, cancelButton)
    );
  }

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

  @Override
  protected void updatedPreview(String quantityStr) {
    try {
      BigDecimal quantity = new BigDecimal(quantityStr);
      TransactionPreview preview = controller.previewBuy(target.getSymbol(), quantity);

      grossValueLabel.setText(ViewUtils.formatCurrency(preview.gross()));
      commissionValueLabel.setText(ViewUtils.formatCurrency(preview.commission()));
      totalLabel.setText(ViewUtils.formatCurrency(preview.total()));
    } catch (Exception _) {
      grossValueLabel.setText("$0.00");
      commissionValueLabel.setText("$0.00");
      totalLabel.setText("$0.00");
    }
  }

  @Override
  protected void handleAction() {
    try {
      BigDecimal quantity = new BigDecimal(quantityField.getText());
      controller.executeBuy(this.target.getSymbol(), quantity);
      requestClose();
    } catch (NumberFormatException e) {
      ViewUtils.showErrorAlert("Invalid quantity", "Please enter a valid number");
    } catch (IllegalArgumentException e) {
      ViewUtils.showErrorAlert("Unable to buy", e.getMessage());
    }
  }
}
