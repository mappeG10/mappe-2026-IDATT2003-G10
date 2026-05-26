package edu.ntnu.idi.idatt.view.component;

import edu.ntnu.idi.idatt.model.Share;
import edu.ntnu.idi.idatt.model.Stock;
import edu.ntnu.idi.idatt.view.util.FormatUtil;
import javafx.stage.Window;
import java.math.BigDecimal;
import java.util.List;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Modal dialog displaying a stock's complete price history as an area chart.
 *
 * <p>Shows current, all-time high, and all-time low prices in a stat row above the chart.
 * The horizontal axis represents the game week (starting at 1), and the vertical axis
 * represents the price in USD.</p>
 *
 * <p>Two static factory methods are provided for convenience: {@link #open(Stock, Window)}
 * for use from the market view, and {@link #open(Share, Window)} for use from the portfolio
 * or dashboard view.</p>
 */
public class StockChartWidget extends BaseModal<Stock> {

  /**
   * Opens a {@code StockChartWidget} for the given stock anchored to the specified window.
   *
   * <p>Has no effect if either argument is {@code null}.</p>
   *
   * @param stock the stock whose price history to display; must not be {@code null}
   * @param owner the parent window to anchor the dialog to; must not be {@code null}
   */
  public static void open(Stock stock, Window owner) {
    if (stock == null || owner == null) {
      return;
    }
    new StockChartWidget(stock).openDialog(owner);
  }

  /**
   * Opens a {@code StockChartWidget} for the stock underlying the given share position.
   *
   * <p>Has no effect if either argument is {@code null}.</p>
   *
   * @param share the share position whose stock's price history to display; must not be
   *              {@code null}
   * @param owner the parent window to anchor the dialog to; must not be {@code null}
   */
  public static void open(Share share, Window owner) {
    if (share == null || owner == null) {
      return;
    }
    new StockChartWidget(share.getStock()).openDialog(owner);
  }

  /**
   * Constructs and immediately lays out a chart dialog for the given stock.
   *
   * @param target the stock whose price history to visualise; must not be {@code null}
   */
  public StockChartWidget(Stock target) {
    super(target);
    setupUI();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setupUI() {
    getStyleClass().addAll("widget-root", "stock-chart-widget");

    this.titleLabel = new Label(target.getSymbol() + " — " + target.getCompany());
    this.titleLabel.getStyleClass().add("widget-title");

    this.closeButton = new Button("Close");
    this.closeButton.getStyleClass().add("btn-cancel");

    setSpacing(16);
    getChildren().addAll(
        titleLabel,
        buildStatsRow(),
        buildChart(),
        closeButton
    );
  }

  /**
   * Builds the stats row showing current price, all-time high, and all-time low.
   *
   * @return an {@link HBox} containing the three stat cards
   */
  private HBox buildStatsRow() {
    HBox row = new HBox(24);
    row.getStyleClass().add("widget-summary");
    row.getChildren().addAll(
        buildStatCard("Current", target.getSalesPrice()),
        buildStatCard("High",    target.getHighestPrice()),
        buildStatCard("Low",     target.getLowestPrice())
    );
    return row;
  }

  /**
   * Builds a single labelled stat card displaying a price value.
   *
   * @param label the descriptor shown above the value
   * @param value the price to display in formatted currency
   * @return a {@link VBox} containing the label and formatted value
   */
  private VBox buildStatCard(String label, BigDecimal value) {
    Label keyLabel = new Label(label);
    keyLabel.getStyleClass().add("widget-label-key");

    Label valueLabel = new Label(FormatUtil.formatCurrency(value));
    valueLabel.getStyleClass().add("widget-label-value");

    return new VBox(2, keyLabel, valueLabel);
  }

  /**
   * Builds an area chart plotting the stock's complete price history against game weeks.
   *
   * @return a configured {@link AreaChart} containing one data series for this stock
   */
  private AreaChart<Number, Number> buildChart() {
    NumberAxis xAxis = new NumberAxis();
    xAxis.setLabel("Week");
    xAxis.setAutoRanging(false);
    xAxis.setLowerBound(1);
    xAxis.setUpperBound(Math.max(target.getHistoricalPrices().size(), 2));
    xAxis.setTickUnit(1);
    xAxis.setMinorTickVisible(false);

    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Price ($)");

    AreaChart<Number, Number> chart = new AreaChart<>(xAxis, yAxis);
    chart.setLegendVisible(false);
    chart.setAnimated(false);
    chart.setCreateSymbols(false);
    chart.setPrefSize(580, 340);

    XYChart.Series<Number, Number> series = new XYChart.Series<>();
    List<BigDecimal> prices = target.getHistoricalPrices();
    for (int i = 0; i < prices.size(); i++) {
      series.getData().add(new XYChart.Data<>(i + 1, prices.get(i).doubleValue()));
    }
    chart.getData().add(series);

    return chart;
  }
}
