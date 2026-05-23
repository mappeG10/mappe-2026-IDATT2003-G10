package edu.ntnu.idi.idatt.view.viewcontent;

import edu.ntnu.idi.idatt.controllers.MarketController;
import edu.ntnu.idi.idatt.models.Stock;
import edu.ntnu.idi.idatt.view.GameObserver;
import edu.ntnu.idi.idatt.view.ViewUtils;
import edu.ntnu.idi.idatt.view.widgets.PurchaseWidget;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

    getChildren().add(buildTopContainer());
    getChildren().add(marketTable);
    marketController.registerObserver(this);
    update();
  }

  private TableView<Stock> buildMarketTable() {
    TableColumn<Stock, String> symbolCol = new TableColumn<>("Symbol");
    symbolCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSymbol()));

    TableColumn<Stock, String> companyCol = new TableColumn<>("Company");
    companyCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCompany()));

    TableColumn<Stock, String> priceCol = new TableColumn<>("Price");
    priceCol.setCellValueFactory(data -> new SimpleStringProperty(
        ViewUtils.formatCurrency(data.getValue().getSalesPrice())));

    TableColumn<Stock, String> changeCol = new TableColumn<>("Change");
    changeCol.setCellValueFactory(data -> new SimpleStringProperty(ViewUtils.formatPriceChange(data.getValue().getLatestPriceChange())));

    TableColumn<Stock, String> changePercentCol = new TableColumn<>("Change %");
    changePercentCol.setCellValueFactory(data -> new SimpleStringProperty(ViewUtils.formatPercentage(data.getValue().getLatestPriceChangePercent())));

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

    TableView<Stock> marketTable = new TableView<>();
    marketTable.getColumns().addAll(symbolCol, companyCol, priceCol, changeCol, changePercentCol, buyButtonCol);
    return marketTable;
  }

  private VBox buildTopContainer() {
    Label title = new Label("Market");
    Label subTitle = new Label("Browse and buy stocks from the market");

    searchField.setPromptText("\uD83D\uDD0D Search by symbol or company name");
    searchField.textProperty().addListener((_, _, searchTerm) -> refreshTable(searchTerm));

    VBox topContainer = new VBox();
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
