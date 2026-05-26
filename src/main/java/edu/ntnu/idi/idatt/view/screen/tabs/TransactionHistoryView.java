package edu.ntnu.idi.idatt.view.screen.tabs;

import edu.ntnu.idi.idatt.controller.TransactionHistoryController;
import edu.ntnu.idi.idatt.model.transaction.Transaction;
import edu.ntnu.idi.idatt.observer.GameObserver;
import edu.ntnu.idi.idatt.view.util.FormatUtil;
import edu.ntnu.idi.idatt.view.util.TableColumnFactory;
import edu.ntnu.idi.idatt.view.util.ViewUtility;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Tab view displaying the committed transaction log, grouped by week with styled week-header
 * rows separating each group.
 *
 * <p>The layout is arranged vertically:
 * <ol>
 *   <li>A top container with a title, subtitle, and a horizontal filter bar of
 *       {@link ToggleButton}s — one for "All Weeks" and one per distinct week that
 *       has at least one committed transaction.</li>
 *   <li>A full-height table with nine columns: Week, Type, Symbol, Company, Qty, Price,
 *       Commission, Tax, and Net Total. When no week filter is active, each week group
 *       is preceded by a header row styled with the {@code week-header-row} CSS class.</li>
 * </ol>
 *
 * <p>Implements {@link GameObserver}: the filter bar and table are rebuilt automatically
 * whenever the exchange advances a week or a transaction is committed.</p>
 */
public class TransactionHistoryView extends VBox implements GameObserver {

  private final TransactionHistoryController transactionHistoryController;
  private final TableView<TransactionRow> mainTable;
  private final HBox weekFilterBar;
  private final ToggleGroup weekFilter;
  private Integer selectedWeek;

  /**
   * Flat table row that represents either a week-group header or a single transaction.
   *
   * <p>The table mixes two logical row types in one flat list so that week headings
   * can be rendered inline using a custom {@link TableRow} factory. Header rows carry
   * only the week number; data rows carry the full {@link Transaction} reference.</p>
   */
  private static class TransactionRow {

    private final boolean isHeader;
    private final int week;
    private final Transaction transaction;

    /**
     * Creates a week-group header row for the given week.
     *
     * @param week the game week number to display as a heading
     * @return a new header {@link TransactionRow}
     */
    static TransactionRow weekHeader(int week) {
      return new TransactionRow(true, week, null);
    }

    /**
     * Creates a data row wrapping the given committed transaction.
     *
     * @param tx the committed transaction to represent; must not be {@code null}
     * @return a new data {@link TransactionRow}
     */
    static TransactionRow fromTransaction(Transaction tx) {
      return new TransactionRow(false, tx.getWeek(), tx);
    }

    /**
     * Constructs a transaction row with explicit field values.
     *
     * @param isHeader    {@code true} for a week-group header row
     * @param week        the game week this row belongs to
     * @param transaction the transaction for data rows; {@code null} for header rows
     */
    private TransactionRow(boolean isHeader, int week, Transaction transaction) {
      this.isHeader = isHeader;
      this.week = week;
      this.transaction = transaction;
    }

    /**
     * Returns whether this row is a week-group header.
     *
     * @return {@code true} if this row is a header row; {@code false} for data rows
     */
    boolean isHeader() { return isHeader; }

    /**
     * Returns the game week this row belongs to.
     *
     * @return the game week number
     */
    int getWeek() { return week; }

    /**
     * Returns the transaction associated with this data row.
     *
     * @return the committed transaction, or {@code null} if this is a header row
     */
    Transaction getTransaction() { return transaction; }
  }

  /**
   * Constructs the transaction history view, builds the table and top container, registers
   * as a game observer, and performs an initial data refresh.
   *
   * @param transactionHistoryController the controller providing the transaction log;
   *                                     must not be {@code null}
   */
  public TransactionHistoryView(TransactionHistoryController transactionHistoryController) {
    this.transactionHistoryController = transactionHistoryController;
    this.mainTable = buildTable();
    this.weekFilterBar = new HBox();
    this.weekFilter = new ToggleGroup();
    this.selectedWeek = null;

    getStyleClass().add("content-view");
    VBox.setVgrow(mainTable, Priority.ALWAYS);
    getChildren().addAll(buildTopContainer(), mainTable);
    transactionHistoryController.registerObserver(this);
    update();
  }

  /**
   * Builds the nine-column transaction table with a custom row factory that applies the
   * {@code week-header-row} CSS class to header rows.
   *
   * <p>Each column uses a header-aware cell factory that renders the heading text for
   * header rows and the appropriate transaction field for data rows. The tax column
   * shows a dash ({@code -}) for purchase rows, since purchases incur no capital-gains
   * tax.</p>
   *
   * @return the fully configured transaction {@link TableView}
   */
  private TableView<TransactionRow> buildTable() {
    TableColumn<TransactionRow, String> weekCol = TableColumnFactory.createHeaderAwareColumn(
        "Week",
        TransactionRow::isHeader,
        row -> "Week " + row.getWeek(),
        row -> String.valueOf(row.getTransaction().getWeek()));

    TableColumn<TransactionRow, String> typeCol = TableColumnFactory.createHeaderAwareColumn(
        "Type",
        TransactionRow::isHeader,
        row -> "",
        row -> switch (row.getTransaction().getTransactionType()) {
          case PURCHASE -> "BUY";
          case SALE -> "SELL";
        });

    TableColumn<TransactionRow, String> symbolCol = TableColumnFactory.createHeaderAwareColumn(
        "Symbol",
        TransactionRow::isHeader,
        row -> "",
        row -> row.getTransaction().getSymbol());

    TableColumn<TransactionRow, String> companyCol = TableColumnFactory.createHeaderAwareColumn(
        "Company",
        TransactionRow::isHeader,
        row -> "",
        row -> row.getTransaction().getCompany());

    TableColumn<TransactionRow, String> quantityCol = TableColumnFactory.createHeaderAwareColumn(
        "Qty",
        TransactionRow::isHeader,
        row -> "",
        row -> FormatUtil.formatBigDecimalToString(row.getTransaction().getQuantity()));

    TableColumn<TransactionRow, String> priceCol = TableColumnFactory.createHeaderAwareColumn(
        "Price",
        TransactionRow::isHeader,
        row -> "",
        row -> FormatUtil.formatCurrency(row.getTransaction().getPurchasePrice()));

    TableColumn<TransactionRow, String> commissionCol = TableColumnFactory.createHeaderAwareColumn(
        "Commission",
        TransactionRow::isHeader,
        row -> "",
        row -> FormatUtil.formatCurrency(row.getTransaction().getCommission()));

    TableColumn<TransactionRow, String> taxCol = TableColumnFactory.createHeaderAwareColumn(
        "Tax",
        TransactionRow::isHeader,
        row -> "",
        row -> switch (row.getTransaction().getTransactionType()) {
          case PURCHASE -> "-";
          case SALE -> FormatUtil.formatCurrency(row.getTransaction().getTax());
        });

    TableColumn<TransactionRow, String> netTotalCol = TableColumnFactory.createHeaderAwareColumn(
        "Net Total",
        TransactionRow::isHeader,
        row -> "",
        row -> FormatUtil.formatCurrency(row.getTransaction().getTotalCost()));

    TableView<TransactionRow> table = new TableView<>();
    table.getColumns().addAll(weekCol, typeCol, symbolCol, companyCol, quantityCol, priceCol, commissionCol, taxCol, netTotalCol);
    table.setPlaceholder(new Label("No transactions yet. Start trading to see your history here."));

    table.setRowFactory(tv -> new TableRow<>() {
      /** {@inheritDoc} */
      @Override
      protected void updateItem(TransactionRow item, boolean empty) {
        super.updateItem(item, empty);
        getStyleClass().remove("week-header-row");
        if (item != null && item.isHeader()) {
          getStyleClass().add("week-header-row");
        }
      }
    });

    ViewUtility.applyRoundedClip(table, 12);
    return table;
  }

  /**
   * Rebuilds the week-filter toggle bar to reflect the current set of distinct weeks.
   *
   * <p>Clears all existing toggle buttons and adds a fresh "All Weeks" button followed
   * by one button per distinct week returned by the controller. If no toggle is currently
   * selected (e.g., on first load), the "All Weeks" button is pre-selected and
   * {@link #selectedWeek} is reset to {@code null}.</p>
   */
  private void refreshFilterButtons() {
    weekFilter.getToggles().clear();
    weekFilterBar.getChildren().clear();

    ToggleButton allWeeksBtn = new ToggleButton("All Weeks");
    allWeeksBtn.getStyleClass().add("week-filter-btn");
    allWeeksBtn.setToggleGroup(weekFilter);
    allWeeksBtn.setOnAction(e -> { selectedWeek = null; refreshTable(); });
    weekFilterBar.getChildren().add(allWeeksBtn);

    for (int week : transactionHistoryController.getDistinctWeeks()) {
      ToggleButton weekBtn = new ToggleButton("Week " + week);
      weekBtn.getStyleClass().add("week-filter-btn");
      weekBtn.setToggleGroup(weekFilter);
      weekBtn.setUserData(week);
      weekBtn.setOnAction(e -> { selectedWeek = week; refreshTable(); });
      weekFilterBar.getChildren().add(weekBtn);
    }

    if (weekFilter.getSelectedToggle() == null) {
      allWeeksBtn.setSelected(true);
      selectedWeek = null;
    }
  }

  /**
   * Repopulates the table based on the current {@link #selectedWeek} filter.
   *
   * <p>When {@link #selectedWeek} is {@code null} (all weeks), a {@link TransactionRow}
   * week-header is inserted before each week's transactions. When a specific week is
   * selected, only the transactions for that week are shown — without a header row.</p>
   */
  private void refreshTable() {
    List<TransactionRow> rows = new ArrayList<>();
    if (selectedWeek == null) {
      for (int week : transactionHistoryController.getDistinctWeeks()) {
        rows.add(TransactionRow.weekHeader(week));
        transactionHistoryController.getTransactions(week)
            .forEach(tx -> rows.add(TransactionRow.fromTransaction(tx)));
      }
    } else {
      transactionHistoryController.getTransactions(selectedWeek)
          .forEach(tx -> rows.add(TransactionRow.fromTransaction(tx)));
    }
    mainTable.setItems(FXCollections.observableArrayList(rows));
  }

  /**
   * Builds the top section containing the view title, subtitle, and the week-filter bar.
   *
   * @return the assembled top-section {@link VBox}
   */
  private VBox buildTopContainer() {
    Label title = new Label("Transaction History");
    title.getStyleClass().add("view-title");

    Label subTitle = new Label("All purchases and sales, grouped by week");
    subTitle.getStyleClass().add("view-subtitle");

    weekFilterBar.getStyleClass().add("week-filter-bar");

    VBox topContainer = new VBox(4);
    topContainer.getChildren().addAll(title, subTitle, weekFilterBar);
    return topContainer;
  }

  /**
   * Rebuilds the filter bar and refreshes the table from the controller.
   *
   * <p>Called automatically via the observer mechanism whenever the exchange advances a
   * week or a transaction is committed.</p>
   */
  @Override
  public void update() {
    refreshFilterButtons();
    refreshTable();
  }
}
