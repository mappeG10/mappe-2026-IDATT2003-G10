package edu.ntnu.idi.idatt.view.screen.tabs;

import edu.ntnu.idi.idatt.controller.PortfolioController;
import edu.ntnu.idi.idatt.model.Share;
import edu.ntnu.idi.idatt.observer.GameObserver;
import edu.ntnu.idi.idatt.view.component.SaleWidget;
import edu.ntnu.idi.idatt.view.component.StockChartWidget;
import edu.ntnu.idi.idatt.view.util.FormatUtil;
import edu.ntnu.idi.idatt.view.util.TableColumnFactory;
import edu.ntnu.idi.idatt.view.util.ViewUtility;
import java.math.BigDecimal;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
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

    Label holdingsTitle = new Label("Holdings");
    holdingsTitle.getStyleClass().add("table-container-title");

    VBox tableContainer = new VBox(12);
    tableContainer.getStyleClass().add("table-container");
    VBox.setVgrow(portfolioTable, Priority.ALWAYS);
    tableContainer.getChildren().addAll(holdingsTitle, portfolioTable);
    VBox.setVgrow(tableContainer, Priority.ALWAYS);

    getChildren().addAll(buildTopContainer(), tableContainer);
    portfolioController.registerObserver(this);
    update();
  }


  private TableView<Share> buildPortfolioTable() {
    TableView<Share> portfolioTable = new TableView<>();
    TableColumnFactory.addSymbolAndCompanyColToTable(portfolioTable, Share::getSymbol, Share::getCompany);

    TableColumn<Share, String> quantityCol = TableColumnFactory.<Share>createTextColumn(
        "Quantity", s -> FormatUtil.formatBigDecimalToString(s.getQuantity()));
    TableColumn<Share, String> currentCol = TableColumnFactory.createPriceColumn("Current", Share::getCurrentValue);
    TableColumn<Share, String> gainLossCol = TableColumnFactory.<Share>createColoredChangeColumn(
        "Gain/Loss", s -> FormatUtil.formatPriceChange(s.getGainLoss()));
    TableColumn<Share, String> gainLossPercentCol = TableColumnFactory.<Share>createColoredChangeColumn(
        "Gain %", s -> FormatUtil.formatPercentage(s.getGainLossPercent()));

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

    portfolioTable.getColumns().addAll(quantityCol, currentCol, gainLossCol, gainLossPercentCol, sellButtonCol);
    portfolioTable.setRowFactory(ViewUtility.doubleClickRowFactory(StockChartWidget::open));
    ViewUtility.applyRoundedClip(portfolioTable, 12);
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
    button.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseEvent::consume);
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
    portfolioValueLabel.setText(FormatUtil.formatCurrency(portfolioController.getNetWorth()));
    totalInvestedLabel.setText(FormatUtil.formatCurrency(portfolioController.getTotalInvested()));

    BigDecimal pnl = portfolioController.getUnrealisedPnL();
    unrealisedPnLLabel.setText(FormatUtil.formatPriceChange(pnl));
    ViewUtility.applySignStyleClass(unrealisedPnLLabel, pnl);

    stockAmountLabel.setText(String.valueOf(portfolioController.getPositionsCount()));
  }
}
