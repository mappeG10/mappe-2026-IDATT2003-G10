package edu.ntnu.idi.idatt.view.screen.tabs;

import edu.ntnu.idi.idatt.controller.MarketController;
import edu.ntnu.idi.idatt.model.Stock;
import edu.ntnu.idi.idatt.observer.GameObserver;
import edu.ntnu.idi.idatt.view.util.TableColumnFactory;
import edu.ntnu.idi.idatt.view.util.ViewUtility;
import edu.ntnu.idi.idatt.view.component.PurchaseWidget;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;


public class MarketView extends VBox implements GameObserver {

  private final MarketController marketController;
  private final TableView<Stock> marketTable;
  private final TextField searchField;

  public MarketView(MarketController marketController) {
    this.marketController = marketController;
    this.marketTable = buildMarketTable();
    this.searchField = new TextField();

    getStyleClass().add("content-view");
    VBox.setVgrow(marketTable, Priority.ALWAYS);
    getChildren().add(buildTopContainer());
    getChildren().add(marketTable);
    marketController.registerObserver(this);
    update();
  }

  private TableView<Stock> buildMarketTable() {
    TableView<Stock> marketTable = new TableView<>();
    TableColumnFactory.addSymbolAndCompanyColToTable(marketTable, Stock::getSymbol, Stock::getCompany);

    TableColumn<Stock, String> priceCol = TableColumnFactory.createPriceColumn("Price", Stock::getSalesPrice);
    TableColumn<Stock, String> changeCol = TableColumnFactory.<Stock>createColoredChangeColumn(
        "Change", s -> ViewUtility.formatPriceChange(s.getLatestPriceChange()));
    TableColumn<Stock, String> changePercentCol = TableColumnFactory.<Stock>createColoredChangeColumn(
        "Change %", s -> ViewUtility.formatPercentage(s.getLatestPriceChangePercent()));

    TableColumn<Stock, String> buyButtonCol = new TableColumn<>("Action");
    buyButtonCol.setCellFactory(param -> new TableCell<>() {
      private final Button button = buildBuyButton(this);

      @Override
      protected void updateItem(String s, boolean b) {
        super.updateItem(s, b);
        if (b || getTableRow().getItem() == null) {
          setGraphic(null);
        } else {
          setGraphic(button);
        }
      }
    });

    marketTable.getColumns().addAll(priceCol, changeCol, changePercentCol, buyButtonCol);
    ViewUtility.applyRoundedClip(marketTable, 12);
    return marketTable;
  }

  private VBox buildTopContainer() {
    Label title = new Label("Market");
    title.getStyleClass().add("view-title");

    Label subTitle = new Label("Browse and buy stocks from the market");
    subTitle.getStyleClass().add("view-subtitle");

    searchField.setPromptText("Search by symbol or company name");
    searchField.textProperty().addListener((_, _, searchTerm) -> refreshTable(searchTerm));
    searchField.setMaxWidth(500);

    VBox topContainer = new VBox(8);
    topContainer.getChildren().addAll(title, subTitle, searchField);
    return topContainer;
  }

  private void refreshTable(String searchTerm) {
    if (searchTerm == null || searchTerm.isBlank()) {
      return;
    }
    marketTable.setItems(FXCollections.observableArrayList(marketController.findStocks(searchTerm)));
  }

  private Button buildBuyButton(TableCell<Stock, String> cell) {
    Button button = new Button("Buy");

    button.setOnAction(event -> {
      if (cell.getTableRow() != null) {
        Stock selectedStock = cell.getTableRow().getItem();
        handlePurchase(selectedStock, cell.getScene().getWindow());
      }
    });

    return button;
  }

  private void handlePurchase(Stock stock, Window parentWindow) {
    if (stock == null || parentWindow == null) {
      return;
    }

    PurchaseWidget purchaseWidget = new PurchaseWidget(stock, marketController);
    purchaseWidget.openDialog(parentWindow);
  }

  @Override
  public void update() {
    marketTable.refresh();
    marketTable.setItems(FXCollections.observableArrayList(marketController.getAllStocks()));
  }
}
