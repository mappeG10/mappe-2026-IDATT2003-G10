package edu.ntnu.idi.idatt.view.viewcontent;

import edu.ntnu.idi.idatt.controllers.PortfolioController;
import edu.ntnu.idi.idatt.models.Share;
import edu.ntnu.idi.idatt.view.GameObserver;
import edu.ntnu.idi.idatt.view.ViewUtils;
import edu.ntnu.idi.idatt.view.widgets.SaleWidget;
import java.math.BigDecimal;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;


public class PortfolioView extends VBox implements GameObserver {

  private final PortfolioController portfolioController;
  private final TableView<Share> portfolioTable;
  private final Label portfolioValueLabel;
  private final Label totalInvestedLabel;
  private final Label unrealisedPnLLabel;
  private final Label stockAmountLabel;


  public PortfolioView(PortfolioController portfolioController) {
    this.portfolioController = portfolioController;
    this.portfolioValueLabel = new Label();
    this.totalInvestedLabel = new Label();
    this.unrealisedPnLLabel = new Label();
    this.stockAmountLabel = new Label();

    this.portfolioTable = buildPortfolioTable();

    getStyleClass().add("content-view");
    VBox.setVgrow(portfolioTable, Priority.ALWAYS);
    getChildren().addAll(buildTopContainer(), portfolioTable);
    portfolioController.registerObserver(this);
    update();
  }


  private TableView<Share> buildPortfolioTable() {
    TableColumn<Share, String> symbolCol = new TableColumn<>("Symbol");
    symbolCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStock().getSymbol()));

    TableColumn<Share, String> companyCol = new TableColumn<>("Company");
    companyCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStock().getCompany()));

    TableColumn<Share, String> quantityCol = new TableColumn<>("Quantity");
    quantityCol.setCellValueFactory(data -> new SimpleStringProperty(
        ViewUtils.formatBigDecimalToString(data.getValue().getQuantity())));

    TableColumn<Share, String> currentCol = new TableColumn<>("Current");
    currentCol.setCellValueFactory(data -> new SimpleStringProperty(ViewUtils.formatCurrency(data.getValue().getCurrentValue())));

    TableColumn<Share, String> gainLossCol = new TableColumn<>("Gain/Loss");
    gainLossCol.setCellValueFactory(data -> new SimpleStringProperty(ViewUtils.formatPriceChange(data.getValue().getGainLoss())));
    gainLossCol.setCellFactory(ViewUtils.coloredStringCellFactory());

    TableColumn<Share, String> gainLossPercentCol = new TableColumn<>("Gain %");
    gainLossPercentCol.setCellValueFactory(data -> new SimpleStringProperty(ViewUtils.formatPercentage(data.getValue().getGainLossPercent())));
    gainLossPercentCol.setCellFactory(ViewUtils.coloredStringCellFactory());

    TableColumn<Share, String> sellButtonCol = new TableColumn<>("Action");
    sellButtonCol.setCellFactory(param -> new TableCell<>() {
      private final Button sellButton = buildSellButton(this);

      @Override
      protected void updateItem(String s, boolean b) {
        super.updateItem(s, b);

        if(b || getTableRow() == null || getTableRow().getItem() == null) {
          setGraphic(null);
        } else {
          setGraphic(sellButton);
        }
      }
    });

    TableView<Share> portfolioTable = new TableView<>();
    portfolioTable.getColumns().addAll(symbolCol, companyCol, quantityCol, currentCol, gainLossCol, gainLossPercentCol, sellButtonCol);
    return portfolioTable;
  }

  private VBox buildTopContainer() {
    Label title = new Label("Portfolio");
    title.getStyleClass().add("view-title");

    Label subTitle = new Label("Your current holdings and performance");
    subTitle.getStyleClass().add("view-subtitle");

    HBox portfolioDataContainer = new HBox(12);
    portfolioDataContainer.getStyleClass().add("stat-cards-row");
    portfolioDataContainer.getChildren().addAll(
        buildPortfolioStatCard("Portfolio Value", portfolioValueLabel),
        buildPortfolioStatCard("Total Invested", totalInvestedLabel),
        buildPortfolioStatCard("Unrealised P&L", unrealisedPnLLabel),
        buildPortfolioStatCard("Positions", stockAmountLabel)
    );

    VBox topContainer = new VBox(8);
    topContainer.getChildren().addAll(title, subTitle, portfolioDataContainer);
    return topContainer;
  }

  private VBox buildPortfolioStatCard(String titleText, Label valueLabel) {
    Label titleLabel = new Label(titleText);
    titleLabel.getStyleClass().add("stat-card-title");
    valueLabel.getStyleClass().add("stat-card-value");

    VBox card = new VBox(4);
    card.getStyleClass().add("stat-card");
    card.getChildren().addAll(titleLabel, valueLabel);
    return card;
  }

  private Button buildSellButton(TableCell<Share, String> cell) {
    Button button = new Button("Sell");
    button.getStyleClass().add("btn-sell");
    button.setOnAction(event -> {
      if (cell.getTableRow() != null) {
        Share share = cell.getTableRow().getItem();
        handleSale(share,  cell.getScene().getWindow());
      }
    });
    return button;
  }

  private void handleSale(Share share, Window parentWindow) {
    if (share == null || parentWindow == null) {
      return;
    }

    SaleWidget saleWidget = new SaleWidget(share, portfolioController);
    saleWidget.openDialog(parentWindow);
  }

  @Override
  public void update() {
    portfolioTable.setItems(FXCollections.observableArrayList(portfolioController.getAllShares()));
    portfolioValueLabel.setText(ViewUtils.formatCurrency(portfolioController.getNetWorth()));
    totalInvestedLabel.setText(ViewUtils.formatCurrency(portfolioController.getTotalInvested()));

    BigDecimal pnl = portfolioController.getUnrealisedPnL();
    unrealisedPnLLabel.setText(ViewUtils.formatPriceChange(pnl));
    ViewUtils.applySignStyleClass(unrealisedPnLLabel, pnl);

    stockAmountLabel.setText(String.valueOf(portfolioController.getPositionsCount()));
  }
}
