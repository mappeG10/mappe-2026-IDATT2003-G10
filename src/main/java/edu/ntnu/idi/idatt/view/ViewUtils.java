package edu.ntnu.idi.idatt.view;

import edu.ntnu.idi.idatt.view.widgets.ErrorWidget;
import java.math.BigDecimal;
import javafx.scene.control.Labeled;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Window;
import javafx.util.Callback;

public class ViewUtils {
  private ViewUtils() {}

  public static String formatCurrency(BigDecimal amount) {
    return String.format("$%.2f", amount);
  }

  public static String formatPercentage(BigDecimal value) {
    String sign = value.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
    return String.format("%s%.2f", sign, value) + "%";
  }

  public static String formatPriceChange(BigDecimal change) {
    String sign = change.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
    return String.format("%s$%.2f", sign, change);
  }

  public static String formatBigDecimalToString(BigDecimal amount) {
    return amount.stripTrailingZeros().toPlainString();
  }

  public static void applySignStyleClass(Labeled node, BigDecimal value) {
    node.getStyleClass().removeAll("text-positive", "text-negative");
    int cmp = value.compareTo(BigDecimal.ZERO);
    if (cmp > 0) node.getStyleClass().add("text-positive");
    else if (cmp < 0) node.getStyleClass().add("text-negative");
  }

  public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> coloredStringCellFactory() {
    return col -> new TableCell<>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        getStyleClass().removeAll("text-positive", "text-negative");
        if (empty || item == null) {
          setText(null);
        } else {
          setText(item);
          if (item.contains("+")) {
            getStyleClass().add("text-positive");
          } else if (item.contains("-")) {
            getStyleClass().add("text-negative");
          }
        }
      }
    };
  }

  public static void showErrorAlert(String title, String message) {
    Window owner = Window.getWindows().stream()
        .filter(Window::isShowing)
        .findFirst()
        .orElse(null);
    new ErrorWidget(title, message).openDialog(owner);
  }
}
