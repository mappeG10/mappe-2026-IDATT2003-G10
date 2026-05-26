package edu.ntnu.idi.idatt.view.screen.tabs;

import edu.ntnu.idi.idatt.controller.MarketController;
import edu.ntnu.idi.idatt.model.Stock;
import edu.ntnu.idi.idatt.observer.GameObserver;
import edu.ntnu.idi.idatt.view.component.PurchaseWidget;
import edu.ntnu.idi.idatt.view.component.StockChartWidget;
import edu.ntnu.idi.idatt.view.util.FormatUtil;
import edu.ntnu.idi.idatt.view.util.TableColumnFactory;
import edu.ntnu.idi.idatt.view.util.ViewUtility;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 * Tab view listing all available market stocks with a live search filter and per-row
 * buy buttons.
 *
 * <p>The layout is arranged vertically:
 * <ol>
 *   <li>A top container with a title, subtitle, and a search {@link TextField} that
 *       filters the table in real time by symbol or company name.</li>
 *   <li>A full-height table displaying every stock with price, absolute change,
 *       percentage change, and a "Buy" action button per row.</li>
 * </ol>
 *
 * <p>Clicking "Buy" opens a {@link PurchaseWidget} modal for the selected stock.
 * Double-clicking any row opens a {@link StockChartWidget} showing the stock's full
 * price history.</p>
 *
 * <p>Implements {@link GameObserver}: the table is refreshed automatically whenever
 * the exchange advances a week or a transaction is committed.</p>
 */
public class MarketView extends VBox implements GameObserver {

  /** Controller providing stock data and purchase operations. */
  private final MarketController marketController;

  /** Table displaying all available stocks. */
  private final TableView<Stock> marketTable;

  /** Text field used to filter the market table by symbol or company name. */
  private final TextField searchField;

  /**
   * Constructs the market view, builds the table and top container, registers as a game
   * observer, and performs an initial data refresh.
   *
   * @param marketController the controller providing stock data and purchase operations;
   *                         must not be {@code null}
   */
  public MarketView(MarketController marketController) {
    this.marketController = marketController;
    this.marketTable = buildMarketTable();
    this.searchField = new TextField();

    getStyleClass().add("content-view");

    Label tableTitle = new Label("Available Stocks");
    tableTitle.getStyleClass().add("table-container-title");

    VBox tableContainer = new VBox(12);
    tableContainer.getStyleClass().add("table-container");
    VBox.setVgrow(marketTable, Priority.ALWAYS);
    tableContainer.getChildren().addAll(tableTitle, marketTable);
    VBox.setVgrow(tableContainer, Priority.ALWAYS);

    getChildren().add(buildTopContainer());
    getChildren().add(tableContainer);
    marketController.registerObserver(this);
    update();
  }

  /**
   * Builds the market table with five columns: symbol/company, price, absolute change,
   * percentage change, and a buy-action button.
   *
   * <p>The buy-action column renders a "Buy" button per row. Clicking the button opens a
   * {@link PurchaseWidget} for that row's stock; the click event is consumed so it does not
   * also trigger the row-selection handler. Double-clicking a row opens a
   * {@link StockChartWidget}.</p>
   *
   * @return the fully configured market {@link TableView}
   */
  private TableView<Stock> buildMarketTable() {
    TableView<Stock> marketTable = new TableView<>();
    TableColumnFactory.addSymbolAndCompanyColToTable(marketTable, Stock::getSymbol, Stock::getCompany);

    TableColumn<Stock, String> priceCol = TableColumnFactory.createPriceColumn("Price", Stock::getSalesPrice);
    TableColumn<Stock, String> changeCol = TableColumnFactory.<Stock>createColoredChangeColumn(
        "Change", s -> FormatUtil.formatPriceChange(s.getLatestPriceChange()));
    TableColumn<Stock, String> changePercentCol = TableColumnFactory.<Stock>createColoredChangeColumn(
        "Change %", s -> FormatUtil.formatPercentage(s.getLatestPriceChangePercent()));

    TableColumn<Stock, String> buyButtonCol = new TableColumn<>("Action");
    buyButtonCol.setCellFactory(param -> new TableCell<>() {
      private final Button button = buildBuyButton(this);

      @Override
      protected void updateItem(String s, boolean b) {
        super.updateItem(s, b);
        setGraphic((b || getTableRow().getItem() == null) ? null : button);
      }
    });

    marketTable.getColumns().addAll(priceCol, changeCol, changePercentCol, buyButtonCol);
    marketTable.setRowFactory(ViewUtility.doubleClickRowFactory(StockChartWidget::open));

    ViewUtility.applyRoundedClip(marketTable, 12);
    return marketTable;
  }

  /**
   * Builds the top section containing the view title, subtitle, and the live search field.
   *
   * <p>The search field's text property is listened to; each change triggers
   * {@link #refreshTable(String)} to filter the market table in real time.</p>
   *
   * @return the assembled top-section {@link VBox}
   */
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

  /**
   * Filters the market table to only show stocks whose symbol or company name matches
   * the given search term.
   *
   * <p>If {@code searchTerm} is {@code null} or blank, the full list of stocks is
   * restored.</p>
   *
   * @param searchTerm the text to filter by; may be {@code null} or blank to show all stocks
   */
  private void refreshTable(String searchTerm) {
    if (searchTerm == null || searchTerm.isBlank()) {
      marketTable.setItems(FXCollections.observableArrayList(marketController.getAllStocks()));
      return;
    }
    marketTable.setItems(FXCollections.observableArrayList(marketController.findStocks(searchTerm)));
  }

  /**
   * Creates a "Buy" button bound to the given table cell.
   *
   * <p>The button's click event is consumed via an event filter so that the underlying
   * table-row selection event is not fired simultaneously. The action handler resolves
   * the current row item at click time rather than at construction time to handle
   * table virtualisation correctly.</p>
   *
   * @param cell the table cell that provides the row context for the button; must not
   *             be {@code null}
   * @return the configured buy {@link Button}
   */
  private Button buildBuyButton(TableCell<Stock, String> cell) {
    Button button = new Button("Buy");
    button.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseEvent::consume);

    button.setOnAction(event -> {
      if (cell.getTableRow() != null) {
        handlePurchase(cell.getTableRow().getItem(), cell.getScene().getWindow());
      }
    });

    return button;
  }

  /**
   * Opens a {@link PurchaseWidget} dialog for the given stock anchored to the parent window.
   *
   * <p>Has no effect if either argument is {@code null}.</p>
   *
   * @param stock        the stock the player intends to buy; may be {@code null}
   * @param parentWindow the window to anchor the dialog to; may be {@code null}
   */
  private void handlePurchase(Stock stock, Window parentWindow) {
    if (stock == null || parentWindow == null) {
      return;
    }

    PurchaseWidget purchaseWidget = new PurchaseWidget(stock, marketController);
    purchaseWidget.openDialog(parentWindow);
  }

  /**
   * Refreshes the market table with the latest stock data from the controller.
   *
   * <p>Called automatically via the observer mechanism whenever the exchange advances a
   * week or a transaction is committed.</p>
   */
  @Override
  public void update() {
    marketTable.refresh();
    marketTable.setItems(FXCollections.observableArrayList(marketController.getAllStocks()));
  }
}
