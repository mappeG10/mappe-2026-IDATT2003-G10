package edu.ntnu.idi.idatt.view.util;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Factory for creating pre-configured {@link TableColumn} instances used in the view layer.
 *
 * <p>Each factory method accepts extractor functions that are applied to the row item
 * to produce the cell text, keeping column configuration logic out of individual view classes.
 * This class is not instantiable; all methods are static.</p>
 */
public final class TableColumnFactory {

  private TableColumnFactory() {}

  /**
   * Creates a text column that extracts its cell value using the given function.
   *
   * @param <S>       the type of the table row item
   * @param title     the column header text
   * @param extractor a function that maps a row item to the string displayed in the cell
   * @return a configured {@link TableColumn}
   */
  public static <S> TableColumn<S, String> createTextColumn(
      String title, Function<S, String> extractor) {
    TableColumn<S, String> col = new TableColumn<>(title);
    col.setCellValueFactory(data ->
        new SimpleStringProperty(extractor.apply(data.getValue())));
    return col;
  }

  /**
   * Creates a text column with the header "Symbol" using the given extractor.
   *
   * @param <S>             the type of the table row item
   * @param symbolExtractor a function that maps a row item to the ticker symbol string
   * @return a configured {@link TableColumn} with header "Symbol"
   */
  public static <S> TableColumn<S, String> createSymbolColumn(
      Function<S, String> symbolExtractor) {
    return createTextColumn("Symbol", symbolExtractor);
  }

  /**
   * Creates a text column with the header "Company" using the given extractor.
   *
   * @param <S>              the type of the table row item
   * @param companyExtractor a function that maps a row item to the company name string
   * @return a configured {@link TableColumn} with header "Company"
   */
  public static <S> TableColumn<S, String> createCompanyColumn(
      Function<S, String> companyExtractor) {
    return createTextColumn("Company", companyExtractor);
  }

  /**
   * Creates a currency column that formats its value using {@link FormatUtil#formatCurrency}.
   *
   * @param <S>            the type of the table row item
   * @param title          the column header text
   * @param valueExtractor a function that maps a row item to the {@link BigDecimal} to format
   * @return a configured {@link TableColumn} displaying currency-formatted values
   */
  public static <S> TableColumn<S, String> createPriceColumn(
      String title, Function<S, BigDecimal> valueExtractor) {
    TableColumn<S, String> col = new TableColumn<>(title);
    col.setCellValueFactory(data ->
        new SimpleStringProperty(
            FormatUtil.formatCurrency(valueExtractor.apply(data.getValue()))));
    return col;
  }

  /**
   * Creates a text column whose cells are coloured green or red based on whether the text
   * contains a {@code +} or {@code -} sign.
   *
   * @param <S>               the type of the table row item
   * @param title             the column header text
   * @param formattedExtractor a function that maps a row item to a pre-formatted signed string
   * @return a configured {@link TableColumn} with colour-aware cell rendering
   */
  public static <S> TableColumn<S, String> createColoredChangeColumn(
      String title, Function<S, String> formattedExtractor) {
    TableColumn<S, String> col = new TableColumn<>(title);
    col.setCellValueFactory(data ->
        new SimpleStringProperty(formattedExtractor.apply(data.getValue())));
    col.setCellFactory(ViewUtility.coloredStringCellFactory());
    return col;
  }

  /**
   * Creates a column that renders differently depending on whether a row is a section header
   * or a data row.
   *
   * <p>Used by the transaction history table to insert week-header rows into the same
   * {@link TableView} as regular transaction rows.</p>
   *
   * @param <S>             the type of the table row item (must support both header and data rows)
   * @param title           the column header text
   * @param isHeaderPredicate a predicate that returns {@code true} if the row is a header row
   * @param headerExtractor   a function that maps a header row to the string it should display
   * @param dataExtractor     a function that maps a data row to the string it should display
   * @return a configured {@link TableColumn} with header-aware cell value logic
   */
  public static <S> TableColumn<S, String> createHeaderAwareColumn(
      String title,
      Predicate<S> isHeaderPredicate,
      Function<S, String> headerExtractor,
      Function<S, String> dataExtractor) {
    TableColumn<S, String> col = new TableColumn<>(title);
    col.setCellValueFactory(data -> {
      S item = data.getValue();
      if (isHeaderPredicate.test(item)) {
        return new SimpleStringProperty(headerExtractor.apply(item));
      }
      return new SimpleStringProperty(dataExtractor.apply(item));
    });
    return col;
  }

  /**
   * Adds pre-built "Symbol" and "Company" columns to the given table.
   *
   * @param <S>       the type of the table row item
   * @param table     the table to add the columns to
   * @param symbolFn  a function that maps a row item to its ticker symbol
   * @param companyFn a function that maps a row item to its company name
   */
  public static <S> void addSymbolAndCompanyColToTable(
      TableView<S> table, Function<S, String> symbolFn, Function<S, String> companyFn) {
    table.getColumns().add(createSymbolColumn(symbolFn));
    table.getColumns().add(createCompanyColumn(companyFn));

  }
}
