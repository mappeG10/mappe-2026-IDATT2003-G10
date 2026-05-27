package edu.ntnu.idi.idatt.view.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 * Modal dialog widget that explains the game rules and rank system to the player.
 *
 * <p>Displayed as a read-only informational overlay with four sections: objective, how the game
 * works, the rank promotion thresholds, and a short tips line. Opened via {@link #open(Window)}.
 */
public class HowToPlayWidget extends BaseModal<Void> {

  private static final String DESCRIPTION =
      "Grow your net worth by buying and selling stocks. "
          + "Advance weeks from the Dashboard to move prices.";

  private static final String STEPS =
      "Buy shares from the Market tab.\n"
          + "Sell positions from the Portfolio tab.\n"
          + "Track your performance on the Dashboard.";

  private static final String TIP =
      "Tip: diversify your holdings and save regularly from Settings.";

  private static final String RANKS =
      "Novice — Starting rank\n"
          + "Investor — 10+ weeks traded · 20%+ total profit\n"
          + "Speculator — 20+ weeks traded · 100%+ total profit";

  /** Constructs and immediately lays out the how-to-play dialog. */
  public HowToPlayWidget() {
    super(null);
    setupUi();
  }

  /**
   * Opens a new {@link HowToPlayWidget} dialog anchored to the given owner window.
   *
   * @param owner the parent window to anchor the dialog to; may be {@code null}
   */
  public static void open(Window owner) {
    new HowToPlayWidget().openDialog(owner);
  }

  /** Builds the how-to-play layout with game rules, rank thresholds, and a tip. */
  @Override
  protected void setupUi() {
    getStyleClass().add("widget-root");
    setSpacing(0);

    Label badgeLabel = new Label("i");
    badgeLabel
        .getStyleClass()
        .addAll("alert-icon-badge", "alert-icon-badge-info", "alert-badge-text");

    this.titleLabel = new Label("How to Play");
    this.titleLabel.getStyleClass().add("alert-title");

    HBox header = new HBox(12, badgeLabel, titleLabel);
    header.getStyleClass().addAll("alert-header", "alert-header-info");

    Label descLabel = new Label(DESCRIPTION);
    descLabel.getStyleClass().add("alert-message");
    descLabel.setWrapText(true);

    Label stepsLabel = new Label(STEPS);
    stepsLabel.getStyleClass().add("alert-message");

    Label ranksHeader = new Label("── Ranks ──");
    ranksHeader.getStyleClass().add("alert-message");

    Label ranksLabel = new Label(RANKS);
    ranksLabel.getStyleClass().add("alert-message");

    Label tipLabel = new Label(TIP);
    tipLabel.getStyleClass().add("alert-message");
    tipLabel.setWrapText(true);

    VBox body = new VBox(8, descLabel, stepsLabel, ranksHeader, ranksLabel, tipLabel);
    body.getStyleClass().add("alert-body");

    Region divider = new Region();
    divider.getStyleClass().add("alert-divider");
    divider.setMaxWidth(Double.MAX_VALUE);

    this.closeButton = new Button("Got it");
    this.closeButton.getStyleClass().add("btn-alert-ok-info");
    this.closeButton.setMaxWidth(Double.MAX_VALUE);

    HBox btnRow = new HBox(this.closeButton);
    btnRow.setAlignment(Pos.CENTER);
    btnRow.setPadding(new Insets(16, 24, 20, 24));

    getChildren().addAll(header, body, divider, btnRow);
  }
}
