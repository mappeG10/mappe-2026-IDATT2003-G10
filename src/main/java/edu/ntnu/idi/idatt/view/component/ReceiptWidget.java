package edu.ntnu.idi.idatt.view.component;

import edu.ntnu.idi.idatt.controller.dto.TransactionReceipt;
import edu.ntnu.idi.idatt.model.transaction.TransactionType;
import edu.ntnu.idi.idatt.view.util.FormatUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ReceiptWidget extends BaseModal<TransactionReceipt> {

  public ReceiptWidget(TransactionReceipt receipt) {
    super(receipt);
    setupUI();
  }

  @Override
  protected void setupUI() {
    getStyleClass().add("widget-root");

    this.titleLabel = new Label(switch (target.type()) {
      case PURCHASE -> "Purchase Confirmed";
      case SALE     -> "Sale Confirmed";
    });
    this.titleLabel.getStyleClass().add("widget-title");

    Label subtitleLabel = new Label(target.company() + " · Week " + target.week());
    subtitleLabel.getStyleClass().add("widget-subtitle");

    BigDecimal pricePerShare = target.gross()
        .divide(target.quantity(), 2, RoundingMode.HALF_UP);
    Label detailLabel = new Label(
        switch (target.type()) { case PURCHASE -> "Bought "; case SALE -> "Sold "; }
            + FormatUtil.formatBigDecimalToString(target.quantity())
            + " share(s) of " + target.symbol()
            + " @ " + FormatUtil.formatCurrency(pricePerShare)
    );
    detailLabel.getStyleClass().add("widget-subtitle");

    this.closeButton = new Button("Done");
    this.closeButton.getStyleClass().add(switch (target.type()) {
      case PURCHASE -> "btn-purchase";
      case SALE     -> "btn-sale";
    });
    this.closeButton.setMaxWidth(Double.MAX_VALUE);

    this.setSpacing(12);
    this.getChildren().addAll(
        titleLabel,
        subtitleLabel,
        detailLabel,
        buildSummaryRow(),
        closeButton
    );
  }

  private HBox buildSummaryRow() {
    Label grossKey = new Label(switch (target.type()) {
      case PURCHASE -> "Gross Cost";
      case SALE     -> "Gross Proceeds";
    });
    grossKey.getStyleClass().add("widget-label-key");
    Label commKey = new Label(switch (target.type()) {
      case PURCHASE -> "Commission (0.5%)";
      case SALE     -> "Commission (1%)";
    });
    commKey.getStyleClass().add("widget-label-key");
    Label totalKey = new Label(switch (target.type()) {
      case PURCHASE -> "Total Cost";
      case SALE     -> "Net Proceeds";
    });
    totalKey.getStyleClass().add("widget-label-total");

    Label grossValue = new Label(FormatUtil.formatCurrency(target.gross()));
    grossValue.getStyleClass().add("widget-label-value");
    Label commValue = new Label(FormatUtil.formatCurrency(target.commission()));
    commValue.getStyleClass().add("widget-label-value");
    Label totalValue = new Label(FormatUtil.formatCurrency(target.total()));
    totalValue.getStyleClass().addAll("widget-label-total", switch (target.type()) {
      case PURCHASE -> "receipt-total-purchase";
      case SALE     -> "text-positive";
    });

    VBox keys = new VBox(6, grossKey, commKey);
    VBox values = new VBox(6, grossValue, commValue);

    if (target.type() == TransactionType.SALE && target.tax().compareTo(BigDecimal.ZERO) > 0) {
      Label taxKey = new Label("Capital Gains Tax (30%)");
      taxKey.getStyleClass().add("widget-label-key");
      Label taxValue = new Label(FormatUtil.formatCurrency(target.tax()));
      taxValue.getStyleClass().add("widget-label-value");
      keys.getChildren().add(taxKey);
      values.getChildren().add(taxValue);
    }

    keys.getChildren().add(totalKey);
    values.getChildren().add(totalValue);

    HBox row = new HBox(24, keys, values);
    row.getStyleClass().add("widget-summary");
    return row;
  }
}
