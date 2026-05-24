package edu.ntnu.idi.idatt.view.util;

import edu.ntnu.idi.idatt.view.component.ErrorWidget;
import java.math.BigDecimal;
import javafx.scene.control.Labeled;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;
import javafx.util.Callback;

public class ViewUtility {
  private ViewUtility() {}

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

  public static void applyRoundedClip(Region node, double arcRadius) {
    Rectangle clip = new Rectangle();
    clip.setArcWidth(arcRadius * 2);
    clip.setArcHeight(arcRadius * 2);
    node.widthProperty().addListener((obs, old, w) -> clip.setWidth(w.doubleValue()));
    node.heightProperty().addListener((obs, old, h) -> clip.setHeight(h.doubleValue()));
    node.setClip(clip);
  }

  public static void showErrorAlert(String title, String message) {
    Window owner = Window.getWindows().stream()
        .filter(Window::isShowing)
        .reduce((a, b) -> b)
        .orElse(null);
    new ErrorWidget(title, message).openDialog(owner);
  }
}
