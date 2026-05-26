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

/**
 * Tab view displaying the player's current holdings with per-row sell buttons and summary portfolio
 * statistics.
 *
 * <p>The layout is arranged vertically:
 *
 * <ol>
 *   <li>A top container with a title, subtitle, and four stat cards: Portfolio Value, Total
 *       Invested, Unrealised P&amp;L (colour-coded), and Positions count.
 *   <li>A full-height holdings table with columns for quantity, current value, gain/loss, gain/loss
 *       percentage, and a "Sell" action button per row.
 * </ol>
 *
 * <p>Clicking "Sell" opens a {@link SaleWidget} modal pre-filled with the position's full quantity.
 * Double-clicking any row opens a {@link StockChartWidget} showing the stock's full price history.
 *
 * <p>Implements {@link GameObserver}: all labels and the holdings table refresh automatically
 * whenever the exchange advances a week or a transaction is committed.
 */
public class PortfolioView extends VBox implements GameObserver {

  private final PortfolioController portfolioController;
  private final TableView<Share> portfolioTable;
  private final Label portfolioValueLabel;
  private final Label totalInvestedLabel;
  private final Label unrealisedPnlLabel;
  private final Label stockAmountLabel;

  /**
   * Constructs the portfolio view, builds all sub-sections, registers as a game observer, and
   * performs an initial data refresh.
   *
   * @param portfolioController the controller providing portfolio data and sale operations; must
   *     not be {@code null}
   */
  public PortfolioView(PortfolioController portfolioController) {
    this.portfolioController = portfolioController;
    this.portfolioValueLabel = new Label();
    this.totalInvestedLabel = new Label();
    this.unrealisedPnlLabel = new Label();
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

  /**
   * Builds the holdings table with six columns: symbol/company, quantity, current value, gain/loss,
   * gain/loss percentage, and a sell-action button.
   *
   * <p>The sell-action column renders a "Sell" button per row. Clicking the button opens a {@link
   * SaleWidget} for that row's share position; the click event is consumed so it does not also
   * trigger row-selection. Double-clicking a row opens a {@link StockChartWidget}.
   *
   * @return the fully configured holdings {@link TableView}
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
    TableColumn<Share, String> gainLossPercentCol =
        TableColumnFactory.<Share>createColoredChangeColumn(
            "Gain %", s -> FormatUtil.formatPercentage(s.getGainLossPercent()));

    TableColumn<Share, String> sellButtonCol = new TableColumn<>("Action");
    sellButtonCol.setCellFactory(
        param ->
            new TableCell<>() {
              private final Button sellButton = buildSellButton(this);

              /** {@inheritDoc} */
              @Override
              protected void updateItem(String s, boolean b) {
                super.updateItem(s, b);

                if (b || getTableRow() == null || getTableRow().getItem() == null) {
                  setGraphic(null);
                } else {
                  setGraphic(sellButton);
                }
              }
            });

    portfolioTable
        .getColumns()
        .addAll(quantityCol, currentCol, gainLossCol, gainLossPercentCol, sellButtonCol);
    portfolioTable.setRowFactory(ViewUtility.doubleClickRowFactory(StockChartWidget::open));
    ViewUtility.applyRoundedClip(portfolioTable, 12);
    return portfolioTable;
  }

  /**
   * Builds the top section containing the view title, subtitle, and the four portfolio stat cards.
   *
   * @return the assembled top-section {@link VBox}
   */
  private VBox buildTopContainer() {
    Label title = new Label("Portfolio");
    title.getStyleClass().add("view-title");

    Label subTitle = new Label("Your current holdings and performance");
    subTitle.getStyleClass().add("view-subtitle");

    HBox portfolioDataContainer = new HBox(12);
    portfolioDataContainer.getStyleClass().add("stat-cards-row");
    portfolioDataContainer
        .getChildren()
        .addAll(
            buildPortfolioStatCard("Portfolio Value", portfolioValueLabel),
            buildPortfolioStatCard("Total Invested", totalInvestedLabel),
            buildPortfolioStatCard("Unrealised P&L", unrealisedPnlLabel),
            buildPortfolioStatCard("Positions", stockAmountLabel));

    VBox topContainer = new VBox(8);
    topContainer.getChildren().addAll(title, subTitle, portfolioDataContainer);
    return topContainer;
  }

  /**
   * Builds a single labelled stat card that wraps the given value {@link Label}.
   *
   * <p>The value label is shared with the field-level reference so that {@link #update()} can
   * update its text directly without traversing the scene graph.
   *
   * @param titleText the descriptive title displayed above the value
   * @param valueLabel the pre-constructed label whose text will be set on each update
   * @return the assembled stat-card {@link VBox}
   */
  private VBox buildPortfolioStatCard(String titleText, Label valueLabel) {
    Label titleLabel = new Label(titleText);
    titleLabel.getStyleClass().add("stat-card-title");
    valueLabel.getStyleClass().add("stat-card-value");

    VBox card = new VBox(4);
    card.getStyleClass().add("stat-card");
    card.getChildren().addAll(titleLabel, valueLabel);
    return card;
  }

  /**
   * Creates a "Sell" button bound to the given table cell.
   *
   * <p>The button's click event is consumed via an event filter so that the underlying table-row
   * selection event is not fired simultaneously. The action handler resolves the current row item
   * at click time rather than at construction time to handle table virtualisation correctly.
   *
   * @param cell the table cell that provides the row context for the button; must not be {@code
   *     null}
   * @return the configured sell {@link Button}
   */
  private Button buildSellButton(TableCell<Share, String> cell) {
    Button button = new Button("Sell");
    button.getStyleClass().add("btn-sell");
    button.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseEvent::consume);
    button.setOnAction(
        event -> {
          if (cell.getTableRow() != null) {
            Share share = cell.getTableRow().getItem();
            handleSale(share, cell.getScene().getWindow());
          }
        });
    return button;
  }

  /**
   * Opens a {@link SaleWidget} dialog for the given share position anchored to the parent window.
   *
   * <p>Has no effect if either argument is {@code null}.
   *
   * @param share the share position the player intends to sell; may be {@code null}
   * @param parentWindow the window to anchor the dialog to; may be {@code null}
   */
  private void handleSale(Share share, Window parentWindow) {
    if (share == null || parentWindow == null) {
      return;
    }

    SaleWidget saleWidget = new SaleWidget(share, portfolioController);
    saleWidget.openDialog(parentWindow);
  }

  /**
   * Refreshes the holdings table and all stat-card labels from the controller.
   *
   * <p>Called automatically via the observer mechanism whenever the exchange advances a week or a
   * transaction is committed. The unrealised P&amp;L label is colour-coded green or red via {@link
   * ViewUtility#applySignStyleClass}.
   */
  @Override
  public void update() {
    portfolioTable.setItems(FXCollections.observableArrayList(portfolioController.getAllShares()));
    portfolioValueLabel.setText(FormatUtil.formatCurrency(portfolioController.getNetWorth()));
    totalInvestedLabel.setText(FormatUtil.formatCurrency(portfolioController.getTotalInvested()));

    BigDecimal pnl = portfolioController.getUnrealisedPnL();
    unrealisedPnlLabel.setText(FormatUtil.formatPriceChange(pnl));
    ViewUtility.applySignStyleClass(unrealisedPnlLabel, pnl);

    stockAmountLabel.setText(String.valueOf(portfolioController.getPositionsCount()));
  }
}
