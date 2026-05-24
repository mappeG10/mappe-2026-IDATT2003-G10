package edu.ntnu.idi.idatt.view.viewcontent.tabs;

import edu.ntnu.idi.idatt.controllers.TransactionHistoryController;
import edu.ntnu.idi.idatt.models.transaction.Transaction;
import edu.ntnu.idi.idatt.observer.GameObserver;
import edu.ntnu.idi.idatt.view.utils.TableColumnFactory;
import edu.ntnu.idi.idatt.view.utils.ViewUtils;
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

public class TransactionHistoryView extends VBox implements GameObserver {

  private final TransactionHistoryController transactionHistoryController;
  private final TableView<TransactionRow> mainTable;
  private final HBox weekFilterBar;
  private final ToggleGroup weekFilter;
  private Integer selectedWeek;

  private static class TransactionRow {
    private final boolean isHeader;
    private final int week;
    private final Transaction transaction;

    static TransactionRow weekHeader(int week) {
      return new TransactionRow(true, week, null);
    }

    static TransactionRow fromTransaction(Transaction tx) {
      return new TransactionRow(false, tx.getWeek(), tx);
    }

    private TransactionRow(boolean isHeader, int week, Transaction transaction) {
      this.isHeader = isHeader;
      this.week = week;
      this.transaction = transaction;
    }

    boolean isHeader() { return isHeader; }
    int getWeek() { return week; }
    Transaction getTransaction() { return transaction; }
  }

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
        row -> ViewUtils.formatBigDecimalToString(row.getTransaction().getQuantity()));

    TableColumn<TransactionRow, String> priceCol = TableColumnFactory.createHeaderAwareColumn(
        "Price",
        TransactionRow::isHeader,
        row -> "",
        row -> ViewUtils.formatCurrency(row.getTransaction().getPurchasePrice()));

    TableColumn<TransactionRow, String> commissionCol = TableColumnFactory.createHeaderAwareColumn(
        "Commission",
        TransactionRow::isHeader,
        row -> "",
        row -> ViewUtils.formatCurrency(row.getTransaction().getCommission()));

    TableColumn<TransactionRow, String> taxCol = TableColumnFactory.createHeaderAwareColumn(
        "Tax",
        TransactionRow::isHeader,
        row -> "",
        row -> switch (row.getTransaction().getTransactionType()) {
          case PURCHASE -> "-";
          case SALE -> ViewUtils.formatCurrency(row.getTransaction().getTax());
        });

    TableColumn<TransactionRow, String> netTotalCol = TableColumnFactory.createHeaderAwareColumn(
        "Net Total",
        TransactionRow::isHeader,
        row -> "",
        row -> ViewUtils.formatCurrency(row.getTransaction().getTotalCost()));

    TableView<TransactionRow> table = new TableView<>();
    table.getColumns().addAll(weekCol, typeCol, symbolCol, companyCol, quantityCol, priceCol, commissionCol, taxCol, netTotalCol);
    table.setPlaceholder(new Label("No transactions yet. Start trading to see your history here."));

    table.setRowFactory(tv -> new TableRow<>() {
      @Override
      protected void updateItem(TransactionRow item, boolean empty) {
        super.updateItem(item, empty);
        getStyleClass().remove("week-header-row");
        if (item != null && item.isHeader()) {
          getStyleClass().add("week-header-row");
        }
      }
    });

    ViewUtils.applyRoundedClip(table, 12);
    return table;
  }

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
      selectedWeek= null;
    }
  }

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

  @Override
  public void update() {
    refreshFilterButtons();
    refreshTable();
  }
}
