package edu.ntnu.idi.idatt.view.viewcontent;

import edu.ntnu.idi.idatt.controllers.MarketController;
import edu.ntnu.idi.idatt.models.Stock;
import edu.ntnu.idi.idatt.view.GameObserver;
import edu.ntnu.idi.idatt.view.ViewUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class MarketView extends VBox implements GameObserver {

  private final MarketController marketController;
  private final TableView<Stock> marketTable;

  public MarketView(MarketController marketController) {
    this.marketController = marketController;
    this.marketTable = buildMarketTable();
    marketTable.setItems(FXCollections.observableArrayList(marketController.getAllStocks()));

    getChildren().add(buildTopContainer());
    getChildren().add(marketTable);
    marketController.registerObserver(this);

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
    changeCol.setCellValueFactory(data -> new SimpleStringProperty(ViewUtils.formatCurrency(data.getValue().getLatestPriceChange())));

    TableColumn<Stock, String> changePercentCol = new TableColumn<>("Change %");
    changePercentCol.setCellValueFactory(data -> new SimpleStringProperty(ViewUtils.formatPercentage(data.getValue().getLatestPriceChangePercent())));


    TableView<Stock> marketTable = new TableView<>();
    marketTable.getColumns().addAll(symbolCol, companyCol, priceCol, changeCol, changePercentCol);
    return marketTable;
  }

  private VBox buildTopContainer() {
    Label title = new Label("Market");
    Label subTitle = new Label("Browse and buy stocks from the market");
    TextField searchField = new TextField("\uD83D\uDD0D Search by symbol or company name");

    VBox topContainer = new VBox();
    topContainer.getChildren().addAll(title, subTitle, searchField);

    return topContainer;
  }

  @Override
  public void update() {
    marketTable.refresh();
  }
}
