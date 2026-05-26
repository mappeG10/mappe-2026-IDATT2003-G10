package edu.ntnu.idi.idatt.view.screen.tabs;

import edu.ntnu.idi.idatt.controller.DashboardController;
import edu.ntnu.idi.idatt.model.Share;
import edu.ntnu.idi.idatt.model.Stock;
import edu.ntnu.idi.idatt.observer.GameObserver;
import edu.ntnu.idi.idatt.view.component.StockChartWidget;
import edu.ntnu.idi.idatt.view.util.FormatUtil;
import edu.ntnu.idi.idatt.view.util.TableColumnFactory;
import edu.ntnu.idi.idatt.view.util.ViewUtility;
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

/**
 * Overview tab combining four summary stat cards, a portfolio holdings table, a top-gainers table,
 * and a top-losers strip.
 *
 * <p>The layout is arranged vertically:
 *
 * <ol>
 *   <li>A top container with four stat cards: Net Worth, Cash Balance, Portfolio Value, and Total
 *       Gain/Loss (with all-time percentage sub-label).
 *   <li>A middle container with the portfolio holdings table on the left and an "Advance to the
 *       next week" button plus top-gainers table on the right.
 *   <li>A bottom container listing the top {@value #TOP_LOSERS_LIMIT} losers as individual
 *       mini-cards.
 * </ol>
 *
 * <p>Implements {@link GameObserver}: all labels, tables, and the losers strip refresh
 * automatically via {@link #update()} whenever the exchange advances a week or a transaction is
 * committed. The gainers table and losers strip are hidden when no weekly price-change data is
 * available (i.e., on week one before any advance).
 */
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
  private static final int TOP_GAINERS_LIMIT = 5;
  private static final int TOP_LOSERS_LIMIT = 5;

  /**
   * Constructs the dashboard view, builds all sub-sections, registers as a game observer, and
   * performs an initial data refresh.
   *
   * @param dashboardController the controller providing all data for this view; must not be {@code
   *     null}
   */
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

  /**
   * Builds the top row of four summary stat cards.
   *
   * <p>The four cards display: Net Worth (with starting-capital sub-label), Cash Balance, Portfolio
   * Value, and Total Gain/Loss (with the all-time percentage sub-label). The gain/loss value and
   * sub-label are coloured green or red by {@link #update()}.
   *
   * @return the assembled stat-cards {@link HBox}
   */
  private HBox buildTopContainer() {
    Label startSubLabel =
        new Label("Start: " + FormatUtil.formatCurrency(dashboardController.getStartingCapital()));
    Label cashSubLabel = new Label("Available to invest");
    Label portfolioSubLabel = new Label("Invested assets");

    HBox topContainer = new HBox(16);
    topContainer.getStyleClass().add("stat-cards-row");
    topContainer
        .getChildren()
        .addAll(
            buildStatCard("Net Worth", netWorthLabel, startSubLabel),
            buildStatCard("Cash Balance", cashBalanceLabel, cashSubLabel),
            buildStatCard("Portfolio Value", portfolioValueLabel, portfolioSubLabel),
            buildStatCard("Total Gain/Loss", totalGainLossLabel, fourthCardSubLabel));
    return topContainer;
  }

  /**
   * Builds a single labelled stat card containing a title, a dynamic value label, and a sub-label.
   *
   * <p>The value and sub-labels are shared with field-level references so that {@link #update()}
   * can update their text and CSS classes directly without traversing the scene graph. The card
   * is configured to grow horizontally to fill available space in the stat-cards row.
   *
   * @param titleText the descriptive title displayed above the value
   * @param valueLabel the pre-constructed label whose text will be set on each update
   * @param subLabel the pre-constructed sub-label shown below the value
   * @return the assembled stat-card {@link VBox}
   */
  private VBox buildStatCard(String titleText, Label valueLabel, Label subLabel) {
    Label titleLabel = new Label(titleText);
    titleLabel.getStyleClass().add("stat-card-title");
    valueLabel.getStyleClass().add("stat-card-value");
    subLabel.getStyleClass().add("stat-card-sub");

    VBox card = new VBox(4);
    card.getStyleClass().add("stat-card");
    card.getChildren().addAll(titleLabel, valueLabel, subLabel);
    HBox.setHgrow(card, Priority.ALWAYS);
    return card;
  }

  /**
   * Builds the portfolio holdings table displayed in the middle-left area.
   *
   * <p>Columns: symbol + company (combined), quantity, current value, and colour-coded gain/loss.
   * Double-clicking a row opens a {@link StockChartWidget} for the underlying stock.
   *
   * @return the configured portfolio {@link TableView}
   */
  private TableView<Share> buildPortfolioTable() {
    TableView<Share> portfolioTable = new TableView<>();
    TableColumnFactory.addSymbolAndCompanyColToTable(
        portfolioTable, Share::getSymbol, Share::getCompany);

    TableColumn<Share, String> quantityCol =
        TableColumnFactory.<Share>createTextColumn(
            "Quantity", s -> FormatUtil.formatBigDecimalToString(s.getQuantity()));
    TableColumn<Share, String> currentCol =
        TableColumnFactory.createPriceColumn("Current", Share::getCurrentValue);
    TableColumn<Share, String> gainLossCol =
        TableColumnFactory.<Share>createColoredChangeColumn(
            "Gain/Loss", s -> FormatUtil.formatPriceChange(s.getGainLoss()));

    portfolioTable.getColumns().addAll(quantityCol, currentCol, gainLossCol);
    portfolioTable.setRowFactory(ViewUtility.doubleClickRowFactory(StockChartWidget::open));
    ViewUtility.applyRoundedClip(portfolioTable, 12);
    return portfolioTable;
  }

  /**
   * Builds the middle section containing the portfolio table and the advance/gainers panel.
   *
   * <p>The portfolio table occupies the left side and grows to fill available horizontal space. The
   * right side holds the "Advance to the next week" button above the top-gainers table.
   *
   * @return the assembled middle {@link HBox}
   */
  private HBox buildMiddleContainer() {
    final HBox middleContainer = new HBox(12);

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

    Label portfolioTitle = new Label("My Portfolio");
    portfolioTitle.getStyleClass().add("table-container-title");

    VBox portfolioContainer = new VBox(12);
    portfolioContainer.getStyleClass().add("table-container");
    VBox.setVgrow(portfolioTable, Priority.ALWAYS);
    portfolioContainer.getChildren().addAll(portfolioTitle, portfolioTable);

    HBox.setHgrow(portfolioContainer, Priority.ALWAYS);
    HBox.setHgrow(advanceAndGainersContainer, Priority.SOMETIMES);
    middleContainer.getChildren().addAll(portfolioContainer, advanceAndGainersContainer);

    return middleContainer;
  }

  /**
   * Builds the top-gainers table shown in the middle-right panel.
   *
   * <p>Columns: a combined symbol-and-company label and a colour-coded weekly percentage change.
   *
   * @return the configured top-gainers {@link TableView}
   */
  private TableView<Stock> buildTopGainersTable() {
    TableColumn<Stock, String> symbolCol =
        TableColumnFactory.<Stock>createTextColumn(
            "Stock", s -> s.getSymbol() + " " + s.getCompany());
    TableColumn<Stock, String> percentCol =
        TableColumnFactory.<Stock>createColoredChangeColumn(
            "Change", s -> FormatUtil.formatPercentage(s.getLatestPriceChangePercent()));

    TableView<Stock> gainersTable = new TableView<>();
    gainersTable.getColumns().addAll(symbolCol, percentCol);
    ViewUtility.applyRoundedClip(gainersTable, 12);
    return gainersTable;
  }

  /**
   * Builds the bottom container that holds the "TOP LOSERS" heading and the dynamically populated
   * {@link #topLosersStockRows} strip.
   *
   * @return the assembled bottom {@link VBox}
   */
  private VBox buildBottomContainer() {
    Label containerTitle = new Label("TOP LOSERS");
    containerTitle.getStyleClass().add("section-heading");

    topLosersStockRows.setSpacing(16);

    VBox container = new VBox(8);
    container.getStyleClass().add("gainers-losers-section");
    container.getChildren().addAll(containerTitle, topLosersStockRows);

    return container;
  }

  /**
   * Rebuilds the top-losers strip from the current controller data.
   *
   * <p>Clears the existing child nodes in {@link #topLosersStockRows} and adds a mini-card for each
   * of the top {@value #TOP_LOSERS_LIMIT} losing stocks, with the percentage change coloured red
   * via {@link ViewUtility#applySignStyleClass}.
   */
  private void refreshTopLosers() {
    topLosersStockRows.getChildren().clear();
    for (Stock stock : dashboardController.getLosers(TOP_LOSERS_LIMIT)) {
      Label stockTitle = new Label(stock.getSymbol() + " " + stock.getCompany());
      stockTitle.getStyleClass().add("stat-card-title");

      BigDecimal change = stock.getLatestPriceChangePercent();
      Label priceChange = new Label(FormatUtil.formatPercentage(change));
      priceChange.getStyleClass().add("stat-card-value");
      ViewUtility.applySignStyleClass(priceChange, change);

      VBox column = new VBox(4);
      column.getStyleClass().add("stat-card");
      column.getChildren().addAll(stockTitle, priceChange);
      topLosersStockRows.getChildren().add(column);
    }
  }

  /**
   * Refreshes all dynamic labels, tables, and the losers strip from the controller.
   *
   * <p>Called automatically via the observer mechanism whenever the exchange advances a week or a
   * transaction is committed. The gainers table and losers container are hidden when no weekly
   * price-change data is available (i.e., on the first week before any advance has occurred).
   */
  @Override
  public void update() {
    portfolioTable.setItems(
        FXCollections.observableArrayList(dashboardController.getAllSharesFromPortfolio()));
    netWorthLabel.setText(FormatUtil.formatCurrency(dashboardController.getNetWorth()));
    cashBalanceLabel.setText(FormatUtil.formatCurrency(dashboardController.getPlayerMoney()));
    portfolioValueLabel.setText(FormatUtil.formatCurrency(dashboardController.getPortfolioValue()));

    BigDecimal gainLoss = dashboardController.getTotalGainLoss();
    totalGainLossLabel.setText(FormatUtil.formatPriceChange(gainLoss));
    ViewUtility.applySignStyleClass(totalGainLossLabel, gainLoss);

    BigDecimal gainLossPercent = dashboardController.getTotalGainLossPercent();
    fourthCardSubLabel.setText(FormatUtil.formatPercentage(gainLossPercent) + " all time");
    ViewUtility.applySignStyleClass(fourthCardSubLabel, gainLossPercent);

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
