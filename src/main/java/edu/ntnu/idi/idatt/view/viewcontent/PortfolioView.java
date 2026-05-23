package edu.ntnu.idi.idatt.view.viewcontent;

import edu.ntnu.idi.idatt.controllers.PortfolioController;
import edu.ntnu.idi.idatt.models.Share;
import edu.ntnu.idi.idatt.view.GameObserver;
import edu.ntnu.idi.idatt.view.ViewUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
        ViewUtils.formatCurrency(data.getValue().getQuantity())));

    TableColumn<Share, String> currentCol = new TableColumn<>("Current");
    currentCol.setCellValueFactory(data -> new SimpleStringProperty(ViewUtils.formatCurrency(data.getValue().getCurrentValue())));

    TableColumn<Share, String> gainLossCol = new TableColumn<>("Gain/Loss");
    gainLossCol.setCellValueFactory(data -> new SimpleStringProperty(ViewUtils.formatCurrency(data.getValue().getGainLoss())));

    TableColumn<Share, String> gainLossPercentCol = new TableColumn<>("Gain %");
    gainLossPercentCol.setCellValueFactory(data -> new SimpleStringProperty(ViewUtils.formatPercentage(data.getValue().getGainLossPercent())));


    TableView<Share> portfolioTable = new TableView<>();
    portfolioTable.getColumns().addAll(symbolCol, companyCol, quantityCol, currentCol, gainLossCol, gainLossPercentCol);
    return portfolioTable;
  }

  private VBox buildTopContainer() {
    Label title = new Label("Portfolio");
    Label subTitle = new Label("Your current holdings and performance");

    HBox portfolioDataContainer = new HBox();
    portfolioDataContainer.getChildren().addAll(
        portfolioValueLabel, totalInvestedLabel, unrealisedPnLLabel, stockAmountLabel
    );

    VBox topContainer = new VBox();
    topContainer.getChildren().addAll(title, subTitle, portfolioDataContainer);

    return topContainer;
  }

  @Override
  public void update() {
    portfolioTable.setItems(FXCollections.observableArrayList(portfolioController.getAllShares()));
    portfolioValueLabel.setText("Portfolio value \n" + ViewUtils.formatCurrency(portfolioController.getNetWorth()));
    totalInvestedLabel.setText("Total invested \n" + ViewUtils.formatCurrency(portfolioController.getTotalInvested()));
    unrealisedPnLLabel.setText("Unrealised P&L \n" + ViewUtils.formatCurrency(portfolioController.getUnrealisedPnL()));
    stockAmountLabel.setText("Positions \n" + portfolioController.getPositionsCount());


  }
}
