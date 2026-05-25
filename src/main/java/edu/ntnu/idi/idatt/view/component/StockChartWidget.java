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

public class StockChartWidget extends BaseModal<Stock> {

  public static void open(Stock stock, Window owner) {
    if (stock == null || owner == null) return;
    new StockChartWidget(stock).openDialog(owner);
  }

  public static void open(Share share, Window owner) {
    if (share == null || owner == null) return;
    new StockChartWidget(share.getStock()).openDialog(owner);
  }

  public StockChartWidget(Stock target) {
    super(target);
    setupUI();
  }

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

  private VBox buildStatCard(String label, BigDecimal value) {
    Label keyLabel = new Label(label);
    keyLabel.getStyleClass().add("widget-label-key");

    Label valueLabel = new Label(FormatUtil.formatCurrency(value));
    valueLabel.getStyleClass().add("widget-label-value");

    return new VBox(2, keyLabel, valueLabel);
  }

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
