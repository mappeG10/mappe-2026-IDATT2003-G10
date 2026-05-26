package edu.ntnu.idi.idatt.view.screen;

import edu.ntnu.idi.idatt.controller.dto.GameSummary;
import edu.ntnu.idi.idatt.view.util.FormatUtil;
import edu.ntnu.idi.idatt.view.util.ViewUtility;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * End-game summary screen displaying the player's final performance statistics.
 *
 * <p>Shown after the player confirms the "Finish Game" action. Displays a centred card containing
 * the player's name, final status badge, and four stat cards (starting capital, final balance,
 * total gain/loss, and weeks played). "Back To Start" resets the application to the start screen;
 * "Exit" terminates the JavaFX application.
 */
public class SummaryView extends StackPane {

  /**
   * Constructs the summary view from the given end-game statistics.
   *
   * @param summary the player's final performance data; must not be {@code null}
   * @param onBackToStart a callback invoked when the player clicks "Back To Start"
   */
  public SummaryView(GameSummary summary, Runnable onBackToStart) {
    getStyleClass().add("start-view");
    getChildren().add(buildCard(summary, onBackToStart));
  }

  /**
   * Builds the centred summary card.
   *
   * @param summary the performance data to display
   * @param onBackToStart the back-to-start callback
   * @return the assembled card {@link VBox}
   */
  private VBox buildCard(GameSummary summary, Runnable onBackToStart) {
    Region headerSpacer = new Region();
    headerSpacer.setPrefHeight(16);

    VBox card = new VBox();
    card.getStyleClass().add("summary-card");
    card.setAlignment(Pos.CENTER);
    card.setMaxHeight(Region.USE_PREF_SIZE);
    card.getChildren()
        .addAll(
            buildHeader(summary),
            headerSpacer,
            buildStatRow(summary),
            buildSecondaryInfo(summary),
            buildActionButtons(onBackToStart));
    return card;
  }

  /**
   * Builds the header section showing the player's name and earned status badge.
   *
   * @param summary the performance data to display
   * @return the assembled header {@link VBox}
   */
  private VBox buildHeader(GameSummary summary) {
    Label title = new Label("Session Complete");
    title.getStyleClass().add("start-title");
    title.setMaxWidth(Double.MAX_VALUE);
    title.setAlignment(Pos.CENTER);

    Label playerName = new Label(summary.playerName());
    playerName.getStyleClass().add("view-title");

    Label statusBadge = new Label(summary.finalStatus().name());
    statusBadge
        .getStyleClass()
        .setAll("status-badge", "status-badge-" + summary.finalStatus().name().toLowerCase());

    HBox nameRow = new HBox(12, playerName, statusBadge);
    nameRow.setAlignment(Pos.CENTER);

    VBox header = new VBox(8, title, nameRow);
    header.setAlignment(Pos.CENTER);
    return header;
  }

  /**
   * Builds the row of four summary stat cards.
   *
   * @param summary the performance data to display
   * @return the assembled stat-cards {@link HBox}
   */
  private HBox buildStatRow(GameSummary summary) {
    VBox startCard =
        buildStatCard(
            "Starting Capital", FormatUtil.formatCurrency(summary.startingCapital()), null);
    VBox finalCard =
        buildStatCard("Final Balance", FormatUtil.formatCurrency(summary.finalBalance()), null);

    Label gainLossLabel = new Label(FormatUtil.formatPriceChange(summary.totalGainLoss()));
    gainLossLabel.getStyleClass().add("stat-card-value");
    ViewUtility.applySignStyleClass(gainLossLabel, summary.totalGainLoss());
    Label gainLossSub =
        new Label(FormatUtil.formatPercentage(summary.totalGainLossPercent()) + " all time");
    gainLossSub.getStyleClass().add("stat-card-sub");
    ViewUtility.applySignStyleClass(gainLossSub, summary.totalGainLossPercent());
    VBox gainLossCard = new VBox(4);
    gainLossCard.getStyleClass().add("stat-card");
    Label gainLossTitle = new Label("Total Gain/Loss");
    gainLossTitle.getStyleClass().add("stat-card-title");
    gainLossCard.getChildren().addAll(gainLossTitle, gainLossLabel, gainLossSub);

    VBox weeksCard = buildStatCard("Weeks Played", String.valueOf(summary.weeksPlayed()), null);

    HBox row = new HBox(16, startCard, finalCard, gainLossCard, weeksCard);
    HBox.setHgrow(startCard, Priority.ALWAYS);
    HBox.setHgrow(finalCard, Priority.ALWAYS);
    HBox.setHgrow(gainLossCard, Priority.ALWAYS);
    HBox.setHgrow(weeksCard, Priority.ALWAYS);
    row.getStyleClass().add("stat-cards-row");
    return row;
  }

  /**
   * Builds a single labelled stat card.
   *
   * @param title the card title
   * @param value the primary value to display
   * @param sub an optional sub-label, or {@code null} to omit it
   * @return the assembled stat card {@link VBox}
   */
  private VBox buildStatCard(String title, String value, String sub) {
    VBox card = new VBox(4);
    card.getStyleClass().add("stat-card");

    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add("stat-card-title");

    Label valueLabel = new Label(value);
    valueLabel.getStyleClass().add("stat-card-value");

    card.getChildren().addAll(titleLabel, valueLabel);
    if (sub != null) {
      Label subLabel = new Label(sub);
      subLabel.getStyleClass().add("stat-card-sub");
      card.getChildren().add(subLabel);
    }
    return card;
  }

  /**
   * Builds the secondary info label showing active weeks vs. total weeks played.
   *
   * @param summary the performance data to display
   * @return the assembled info {@link Label}
   */
  private Label buildSecondaryInfo(GameSummary summary) {
    Label label =
        new Label("Active weeks: " + summary.activeWeeks() + " of " + summary.weeksPlayed());
    label.getStyleClass().add("view-subtitle");
    return label;
  }

  /**
   * Builds the action button row with "Exit" and "Back To Start" buttons.
   *
   * @param onBackToStart the callback invoked when the player clicks "Back To Start"
   * @return the assembled button row {@link HBox}
   */
  private HBox buildActionButtons(Runnable onBackToStart) {
    Button exitBtn = new Button("Exit");
    exitBtn.getStyleClass().add("btn-secondary");
    exitBtn.setMaxWidth(Double.MAX_VALUE);
    exitBtn.setOnAction(e -> Platform.exit());

    Button backBtn = new Button("Back To Start");
    backBtn.getStyleClass().add("btn-primary");
    backBtn.setMaxWidth(Double.MAX_VALUE);
    backBtn.setOnAction(e -> onBackToStart.run());

    HBox.setHgrow(exitBtn, Priority.ALWAYS);
    HBox.setHgrow(backBtn, Priority.ALWAYS);

    HBox buttons = new HBox(12, exitBtn, backBtn);
    VBox.setMargin(buttons, new Insets(8, 0, 0, 0));
    return buttons;
  }
}
