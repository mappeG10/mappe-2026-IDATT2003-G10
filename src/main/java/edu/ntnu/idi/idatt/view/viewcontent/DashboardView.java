package edu.ntnu.idi.idatt.view.viewcontent;

import edu.ntnu.idi.idatt.controllers.DashboardController;
import edu.ntnu.idi.idatt.models.Share;
import edu.ntnu.idi.idatt.models.Stock;
import edu.ntnu.idi.idatt.view.GameObserver;
import edu.ntnu.idi.idatt.view.TableColumnFactory;
import edu.ntnu.idi.idatt.view.ViewUtils;
import java.math.BigDecimal;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
    TableView<Share> portfolioTable = new TableView<>();
    TableColumnFactory.addSymbolAndCompanyColToTable(portfolioTable, Share::getSymbol, Share::getCompany);

    TableColumn<Share, String> quantityCol = TableColumnFactory.<Share>createTextColumn(
        "Quantity", s -> ViewUtils.formatBigDecimalToString(s.getQuantity()));
    TableColumn<Share, String> currentCol = TableColumnFactory.createPriceColumn("Current", Share::getCurrentValue);
    TableColumn<Share, String> gainLossLCol = TableColumnFactory.<Share>createColoredChangeColumn(
        "Gain/Loss", s -> ViewUtils.formatPriceChange(s.getGainLoss()));

    portfolioTable.getColumns().addAll(quantityCol, currentCol, gainLossLCol);
    ViewUtils.applyRoundedClip(portfolioTable, 12);
    return portfolioTable;
  }

  private HBox buildMiddleContainer() {
    HBox middleContainer = new HBox(12);

    Button advanceBtn = new Button("Advance to the next week →");
    advanceBtn.getStyleClass().add("btn-advance");
    advanceBtn.setMaxWidth(Double.MAX_VALUE);
    advanceBtn.setOnAction(actionEvent -> dashboardController.advanceWeek());

    Label gainersTitle = new Label("TOP GAINERS");
    gainersTitle.getStyleClass().add("section-heading");

    VBox gainersSection = new VBox(8);
    gainersSection.getStyleClass().add("gainers-losers-section");
    VBox.setVgrow(topGainersTable, Priority.ALWAYS);
    gainersSection.getChildren().addAll(gainersTitle, topGainersTable);

    VBox advanceAndGainersContainer = new VBox(8);
    VBox.setVgrow(gainersSection, Priority.ALWAYS);
    advanceAndGainersContainer.getChildren().addAll(advanceBtn, gainersSection);

    HBox.setHgrow(portfolioTable,             Priority.ALWAYS);
    HBox.setHgrow(advanceAndGainersContainer,  Priority.SOMETIMES);
    middleContainer.getChildren().addAll(portfolioTable, advanceAndGainersContainer);

    return middleContainer;
  }

  private TableView<Stock> buildTopGainersTable() {
    TableColumn<Stock, String> symbolCol = TableColumnFactory.<Stock>createTextColumn(
        "Stock", s -> s.getSymbol() + " " + s.getCompany());
    TableColumn<Stock, String> percentCol = TableColumnFactory.<Stock>createColoredChangeColumn(
        "Change", s -> ViewUtils.formatPercentage(s.getLatestPriceChangePercent()));

    TableView<Stock> gainersTable = new TableView<>();
    gainersTable.getColumns().addAll(symbolCol, percentCol);
    ViewUtils.applyRoundedClip(gainersTable, 12);
    return gainersTable;
  }



  private VBox buildBottomContainer() {
    Label containerTitle = new Label("TOP LOSERS");
    containerTitle.getStyleClass().add("section-heading");

    topLosersStockRows.setSpacing(16);

    VBox container = new VBox(8);
    container.getStyleClass().add("gainers-losers-section");
    container.getChildren().addAll(containerTitle, topLosersStockRows);

    return container;
  }

  private void refreshTopLosers() {
    topLosersStockRows.getChildren().clear();
    for (Stock stock : dashboardController.getLosers(TOP_LOSERS_LIMIT)) {
      Label stockTitle = new Label(stock.getSymbol() + " " + stock.getCompany());
      stockTitle.getStyleClass().add("stat-card-title");

      BigDecimal change = stock.getLatestPriceChangePercent();
      Label priceChange = new Label(ViewUtils.formatPercentage(change));
      priceChange.getStyleClass().add("stat-card-value");
      ViewUtils.applySignStyleClass(priceChange, change);

      VBox column = new VBox(4);
      column.getStyleClass().add("stat-card");
      column.getChildren().addAll(stockTitle, priceChange);
      topLosersStockRows.getChildren().add(column);
    }
  }

  @Override
  public void update() {
    portfolioTable.setItems(FXCollections.observableArrayList(dashboardController.getAllSharesFromPortfolio()));
    netWorthLabel.setText(ViewUtils.formatCurrency(dashboardController.getNetWorth()));
    cashBalanceLabel.setText(ViewUtils.formatCurrency(dashboardController.getPlayerMoney()));
    portfolioValueLabel.setText(ViewUtils.formatCurrency(dashboardController.getPortfolioValue()));

    BigDecimal gainLoss = dashboardController.getTotalGainLoss();
    totalGainLossLabel.setText(ViewUtils.formatPriceChange(gainLoss));
    ViewUtils.applySignStyleClass(totalGainLossLabel, gainLoss);

    BigDecimal gainLossPercent = dashboardController.getTotalGainLossPercent();
    fourthCardSubLabel.setText(ViewUtils.formatPercentage(gainLossPercent) + " all time");
    ViewUtils.applySignStyleClass(fourthCardSubLabel, gainLossPercent);

    List<Stock> gainers = dashboardController.getGainers(TOP_GAINERS_LIMIT);
    boolean gainersListNotEmpty = !gainers.isEmpty();

    topGainersTable.setVisible(gainersListNotEmpty);
    topGainersTable.setManaged(gainersListNotEmpty);
    bottomContainer.setVisible(gainersListNotEmpty);
    bottomContainer.setManaged(gainersListNotEmpty);

    topGainersTable.setItems(FXCollections.observableArrayList(gainers));
    refreshTopLosers();
    portfolioTable.refresh();
  }
}
