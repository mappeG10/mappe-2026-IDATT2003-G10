package edu.ntnu.idi.idatt.view.viewcontent;

import edu.ntnu.idi.idatt.controllers.DashboardController;
import javafx.scene.layout.Priority;
import edu.ntnu.idi.idatt.models.Share;
import edu.ntnu.idi.idatt.models.Stock;
import edu.ntnu.idi.idatt.view.GameObserver;
import edu.ntnu.idi.idatt.view.ViewUtils;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DashboardView extends VBox implements GameObserver {

  private final DashboardController dashboardController;
  private final TableView<Share> portfolioTable;
  private final TableView<Stock> topGainersTable;
  private final HBox topLosersStockRows;
  private final Label netWorthLabel;
  private final Label cashBalanceLabel;
  private final Label portfolioValueLabel;
  private final Label totalGainLossLabel;
  private final Label fourthCardSubLabel;
  private final VBox bottomContainer;

  private final static int TOP_GAINERS_LIMIT = 5, TOP_LOSERS_LIMIT = 5;


  public DashboardView(DashboardController dashboardController) {
    this.dashboardController = dashboardController;
    this.netWorthLabel = new Label();
    this.cashBalanceLabel = new Label();
    this.portfolioValueLabel = new Label();
    this.totalGainLossLabel = new Label();
    this.fourthCardSubLabel = new Label();
    this.topLosersStockRows = new HBox();
    this.bottomContainer = buildBottomContainer();

    this.portfolioTable = buildPortfolioTable();
    this.topGainersTable = buildTopGainersTable();

    getStyleClass().add("content-view");
    HBox middleContainer = buildMiddleContainer();
    VBox.setVgrow(middleContainer, Priority.ALWAYS);
    getChildren().addAll(buildTopContainer(), middleContainer, bottomContainer);
    dashboardController.registerObserver(this);
    update();
  }

  private HBox buildTopContainer() {
    HBox topContainer = new HBox(16);
    topContainer.getStyleClass().add("stat-cards-row");

    VBox firstCard = new VBox(4);
    firstCard.getStyleClass().add("stat-card");
    Label firstCardTitle = new Label("Net Worth");
    firstCardTitle.getStyleClass().add("stat-card-title");
    netWorthLabel.getStyleClass().add("stat-card-value");
    Label firstCardSubLabel = new Label("Start: " + ViewUtils.formatCurrency(dashboardController.getStartingCapital()));
    firstCardSubLabel.getStyleClass().add("stat-card-sub");
    firstCard.getChildren().addAll(firstCardTitle, netWorthLabel, firstCardSubLabel);

    VBox secondCard = new VBox(4);
    secondCard.getStyleClass().add("stat-card");
    Label secondCardTitle = new Label("Cash Balance");
    secondCardTitle.getStyleClass().add("stat-card-title");
    cashBalanceLabel.getStyleClass().add("stat-card-value");
    Label secondCardSubLabel = new Label("Available to invest");
    secondCardSubLabel.getStyleClass().add("stat-card-sub");
    secondCard.getChildren().addAll(secondCardTitle, cashBalanceLabel, secondCardSubLabel);

    VBox thirdCard = new VBox(4);
    thirdCard.getStyleClass().add("stat-card");
    Label thirdCardTitle = new Label("Portfolio Value");
    thirdCardTitle.getStyleClass().add("stat-card-title");
    portfolioValueLabel.getStyleClass().add("stat-card-value");
    Label thirdCardSubLabel = new Label("Invested assets");
    thirdCardSubLabel.getStyleClass().add("stat-card-sub");
    thirdCard.getChildren().addAll(thirdCardTitle, portfolioValueLabel, thirdCardSubLabel);

    VBox fourthCard = new VBox(4);
    fourthCard.getStyleClass().add("stat-card");
    Label fourthCardTitle = new Label("Total Gain/Loss");
    fourthCardTitle.getStyleClass().add("stat-card-title");
    totalGainLossLabel.getStyleClass().add("stat-card-value");
    fourthCardSubLabel.getStyleClass().add("stat-card-sub");
    fourthCard.getChildren().addAll(fourthCardTitle, totalGainLossLabel, fourthCardSubLabel);

    HBox.setHgrow(firstCard,  Priority.ALWAYS);
    HBox.setHgrow(secondCard, Priority.ALWAYS);
    HBox.setHgrow(thirdCard,  Priority.ALWAYS);
    HBox.setHgrow(fourthCard, Priority.ALWAYS);
    topContainer.getChildren().addAll(firstCard, secondCard, thirdCard, fourthCard);
    return topContainer;
  }

  private TableView<Share> buildPortfolioTable() {
    TableColumn<Share, String> symbolCol = new TableColumn<>("Symbol");
    symbolCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStock().getSymbol()));

    TableColumn<Share, String> companyCol = new TableColumn<>("Company");
    companyCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStock().getCompany()));

    TableColumn<Share, String> quantityCol = new TableColumn<>("Quantity");
    quantityCol.setCellValueFactory(data -> new SimpleStringProperty(ViewUtils.formatBigDecimalToString(data.getValue().getQuantity())));
    // TODO: Implement utility method to format quantity

    TableColumn<Share, String> currentCol = new TableColumn<>("Current");
    currentCol.setCellValueFactory(data -> new SimpleStringProperty(ViewUtils.formatCurrency(data.getValue().getCurrentValue())));

    TableColumn<Share, String> gainLossLCol = new TableColumn<>("Gain/Loss");
    gainLossLCol.setCellValueFactory(data -> new SimpleStringProperty(ViewUtils.formatPriceChange(data.getValue().getGainLoss())));


    TableView<Share> portfolioTable = new TableView<>();
    portfolioTable.getColumns().addAll(symbolCol, companyCol, quantityCol, currentCol, gainLossLCol);
    return portfolioTable;
  }

  private HBox buildMiddleContainer() {
    HBox middleContainer = new HBox();

    Button advanceBtn = new Button("Advance to the next week →");
    advanceBtn.getStyleClass().add("btn-advance");
    advanceBtn.setMaxWidth(Double.MAX_VALUE);
    advanceBtn.setOnAction(actionEvent -> dashboardController.advanceWeek());

    VBox advanceAndGainersContainer = new VBox(8);
    VBox.setVgrow(topGainersTable, Priority.ALWAYS);
    advanceAndGainersContainer.getChildren().addAll(advanceBtn, topGainersTable);

    HBox.setHgrow(portfolioTable,            Priority.ALWAYS);
    HBox.setHgrow(advanceAndGainersContainer, Priority.SOMETIMES);
    middleContainer.getChildren().addAll(portfolioTable, advanceAndGainersContainer);

    return middleContainer;
  }

  private TableView<Stock> buildTopGainersTable() {
    TableColumn<Stock, String> symbolCol = new TableColumn<>();
    symbolCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSymbol() + " " + data.getValue().getCompany()));

    TableColumn<Stock, String> percentCol = new TableColumn<>();
    percentCol.setCellValueFactory(data -> new SimpleStringProperty(ViewUtils.formatPercentage(data.getValue().getLatestPriceChangePercent())));


    TableView<Stock> portfolioTable = new TableView<>();
    portfolioTable.getColumns().addAll(symbolCol, percentCol);
    return portfolioTable;
  }



  private VBox buildBottomContainer() {
    VBox container = new VBox();
    Label containerTitle = new Label("Top Losers");

    refreshTopLosers();

    container.getChildren().addAll(containerTitle, topLosersStockRows);

    return container;
  }

  private void refreshTopLosers() {
    topLosersStockRows.getChildren().clear();
    for (Stock stock : dashboardController.getLosers(TOP_LOSERS_LIMIT)) {
      VBox column = new VBox();
      Label stockTitle = new Label(stock.getSymbol() + " " + stock.getCompany());
      Label pricechange = new Label(ViewUtils.formatPriceChange(stock.getLatestPriceChangePercent()));
      column.getChildren().addAll(stockTitle, pricechange);
      topLosersStockRows.getChildren().add(column);
    }
  }

  @Override
  public void update() {
    portfolioTable.setItems(FXCollections.observableArrayList(dashboardController.getAllSharesFromPortfolio()));
    netWorthLabel.setText(ViewUtils.formatCurrency(dashboardController.getNetWorth()));
    cashBalanceLabel.setText(ViewUtils.formatCurrency(dashboardController.getPlayerMoney()));
    portfolioValueLabel.setText(ViewUtils.formatCurrency(dashboardController.getPortfolioValue()));
    totalGainLossLabel.setText(ViewUtils.formatPriceChange(dashboardController.getTotalGainLoss()));
    fourthCardSubLabel.setText(ViewUtils.formatPercentage(dashboardController.getTotalGainLossPercent()));

    List<Stock> gainers = dashboardController.getGainers(TOP_GAINERS_LIMIT);
    boolean gainersListNotEmpty = !gainers.isEmpty();

    topGainersTable.setVisible(gainersListNotEmpty);
    topGainersTable.setManaged(gainersListNotEmpty);
    bottomContainer.setVisible(gainersListNotEmpty);
    bottomContainer.setManaged(gainersListNotEmpty);

    topGainersTable.setItems(FXCollections.observableArrayList(gainers));
    refreshTopLosers();


  }
}
