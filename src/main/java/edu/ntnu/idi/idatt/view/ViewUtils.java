package edu.ntnu.idi.idatt.view;

import javafx.scene.control.Alert;

import java.math.BigDecimal;

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


  public static void showErrorAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public static String formatBigDecimalToString(BigDecimal amount) {
    return amount.stripTrailingZeros().toPlainString();
  }
}