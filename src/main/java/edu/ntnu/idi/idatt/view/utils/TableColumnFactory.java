package edu.ntnu.idi.idatt.view.utils;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public final class TableColumnFactory {

  private TableColumnFactory() {}

  public static <S> TableColumn<S, String> createTextColumn(
      String title, Function<S, String> extractor) {
    TableColumn<S, String> col = new TableColumn<>(title);
    col.setCellValueFactory(data ->
        new SimpleStringProperty(extractor.apply(data.getValue())));
    return col;
  }

  public static <S> TableColumn<S, String> createSymbolColumn(
      Function<S, String> symbolExtractor) {
    return createTextColumn("Symbol", symbolExtractor);
  }

  public static <S> TableColumn<S, String> createCompanyColumn(
      Function<S, String> companyExtractor) {
    return createTextColumn("Company", companyExtractor);
  }

  public static <S> TableColumn<S, String> createPriceColumn(
      String title, Function<S, BigDecimal> valueExtractor) {
    TableColumn<S, String> col = new TableColumn<>(title);
    col.setCellValueFactory(data ->
        new SimpleStringProperty(
            ViewUtils.formatCurrency(valueExtractor.apply(data.getValue()))));
    return col;
  }

  public static <S> TableColumn<S, String> createColoredChangeColumn(
      String title, Function<S, String> formattedExtractor) {
    TableColumn<S, String> col = new TableColumn<>(title);
    col.setCellValueFactory(data ->
        new SimpleStringProperty(formattedExtractor.apply(data.getValue())));
    col.setCellFactory(ViewUtils.coloredStringCellFactory());
    return col;
  }

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

  public static <S> void addSymbolAndCompanyColToTable(
      TableView<S> table, Function<S, String> symbolFn, Function<S, String> companyFn) {
    table.getColumns().add(createSymbolColumn(symbolFn));
    table.getColumns().add(createCompanyColumn(companyFn));

  }
}
