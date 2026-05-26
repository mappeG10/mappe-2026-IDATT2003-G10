package edu.ntnu.idi.idatt.view.util;

import java.math.BigDecimal;

/**
 * Utility class providing consistent number-formatting methods for the view layer.
 *
 * <p>All methods produce locale-independent, human-readable strings suitable for
 * display in labels and table cells. This class is not instantiable; all methods
 * are static.</p>
 */
public final class FormatUtil {

  /** Prevents instantiation of this static utility class. */
  private FormatUtil() {}

  /**
   * Formats a monetary amount as a USD currency string with thousands separators.
   *
   * <p>Example: {@code 1234.5} → {@code "$1,234.50"}</p>
   *
   * @param amount the monetary value to format; must not be {@code null}
   * @return a formatted currency string prefixed with {@code $}
   */
  public static String formatCurrency(BigDecimal amount) {
    return String.format("$%,.2f", amount);
  }

  /**
   * Formats a percentage value, prepending a {@code +} sign for positive values.
   *
   * <p>Example: {@code 5.25} → {@code "+5.25%"}, {@code -3.0} → {@code "-3.00%"}</p>
   *
   * @param value the percentage value to format; must not be {@code null}
   * @return a formatted percentage string with an explicit sign for positive values
   */
  public static String formatPercentage(BigDecimal value) {
    String sign = value.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
    return String.format("%s%.2f", sign, value) + "%";
  }

  /**
   * Formats an absolute price change as a signed USD currency string.
   *
   * <p>Example: {@code 12.5} → {@code "+$12.50"}, {@code -3.0} → {@code "-$3.00"}</p>
   *
   * @param change the price change to format; must not be {@code null}
   * @return a formatted currency string with an explicit {@code +} sign for positive changes
   */
  public static String formatPriceChange(BigDecimal change) {
    String sign = change.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
    return String.format("%s$%,.2f", sign, change);
  }

  /**
   * Converts a {@link BigDecimal} to a plain decimal string with trailing zeros removed.
   *
   * <p>Example: {@code 10.50000} → {@code "10.5"}, {@code 100.00} → {@code "100"}</p>
   *
   * @param amount the value to convert; must not be {@code null}
   * @return a plain string representation with no trailing zeros
   */
  public static String formatBigDecimalToString(BigDecimal amount) {
    return amount.stripTrailingZeros().toPlainString();
  }
}
