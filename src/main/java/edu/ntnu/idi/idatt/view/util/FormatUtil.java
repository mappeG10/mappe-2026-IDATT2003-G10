package edu.ntnu.idi.idatt.view.util;

import java.math.BigDecimal;

public final class FormatUtil {
  private FormatUtil() {}

  public static String formatCurrency(BigDecimal amount) {
    return String.format("$%,.2f", amount);
  }

  public static String formatPercentage(BigDecimal value) {
    String sign = value.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
    return String.format("%s%.2f", sign, value) + "%";
  }

  public static String formatPriceChange(BigDecimal change) {
    String sign = change.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
    return String.format("%s$%,.2f", sign, change);
  }

  public static String formatBigDecimalToString(BigDecimal amount) {
    return amount.stripTrailingZeros().toPlainString();
  }
}
