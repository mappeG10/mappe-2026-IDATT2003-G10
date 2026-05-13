package edu.ntnu.idi.idatt.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;

import java.math.BigDecimal;

public class ViewUtils {
  private ViewUtils() {}

  public static String formatCurrency(BigDecimal amount) {
    return String.format("$%.2f", amount);
  }

  //TODO: A format quantity, since you should'nt be able to buy fractional shares?

  public static String formatPercentage(BigDecimal value) {
    String sign = value.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
    return String.format("%s%.2f", sign, value) + "%";
    //TODO: Return as a negative or positive javafx element?
  }

  public static String formatStockPriceChange(BigDecimal change) {
    String sign = change.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
    return String.format("%s$%.2f", sign, change);
    //TODO: Return as a negative or positive javafx element?
  }


  public static void showErrorAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setContentText(message);
    alert.showAndWait();
  }
}