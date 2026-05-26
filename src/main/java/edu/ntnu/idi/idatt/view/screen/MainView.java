package edu.ntnu.idi.idatt.view.screen;

import edu.ntnu.idi.idatt.controller.GameController;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import edu.ntnu.idi.idatt.observer.GameObserver;
import edu.ntnu.idi.idatt.view.GameTab;
import edu.ntnu.idi.idatt.view.component.FinishGameWidget;
import edu.ntnu.idi.idatt.view.util.FormatUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * The primary shell of the game screen, providing a top navigation bar and a left sidebar.
 *
 * <p>Extends {@link BorderPane}: the top region holds a navbar with the current week,
 * cash balance, player name, and status badge; the left region holds a sidebar with
 * tab navigation buttons and the "Finish Game" action. The centre region is swapped
 * by {@link #setContent(Parent)} each time the active tab changes.</p>
 *
 * <p>Implements {@link GameObserver} so the navbar labels refresh automatically whenever
 * the exchange advances a week or a transaction is committed.</p>
 */
public class MainView extends BorderPane implements GameObserver {

  private final GameController gameController;
  private final Label weekLabel;
  private final Label balanceLabel;
  private final Label playerNameLabel;
  private final Label statusLabel;

  private final List<Button> sidebarBtns = new ArrayList<>();
  private Button activeBtn;

  /**
   * Constructs the main game view and registers it as a game observer.
   *
   * @param gameController the top-level controller for this session; must not be {@code null}
   * @param onTabSelected  a callback invoked with the selected {@link GameTab} when the
   *                       player clicks a sidebar navigation button
   * @param onFinish       a callback invoked when the player clicks "Finish Game" and
   *                       confirms the action in the resulting dialog
   */
  public MainView(GameController gameController, Consumer<GameTab> onTabSelected, Runnable onFinish) {
    this.gameController = gameController;

    this.weekLabel = new Label();
    this.balanceLabel = new Label();
    this.playerNameLabel = new Label();
    this.statusLabel = new Label();

    getStyleClass().add("main-view");
    setTop(buildNavbar());
    setLeft(buildSidebar(onTabSelected, onFinish));
    gameController.registerObserver(this);
    update();
  }

  /**
   * Replaces the centre content with the given panel, unregistering the previous panel
   * as an observer if it implemented {@link GameObserver}.
   *
   * @param content the new content panel to display; must not be {@code null}
   */
  public void setContent(Parent content) {
    Node current = getCenter();
    if (current instanceof GameObserver observer) {
      gameController.unregisterObserver(observer);
    }
    setCenter(content);
  }

  /**
   * Builds the left sidebar containing tab navigation buttons and the finish-game button.
   *
   * @param onTabSelected a callback for tab selection events
   * @param onFinish      a callback triggered by the finish-game confirmation
   * @return the assembled sidebar {@link VBox}
   */
  private VBox buildSidebar(Consumer<GameTab> onTabSelected, Runnable onFinish) {
    Button dashboardBtn = new Button("Dashboard");
    Button marketBtn    = new Button("Market");
    Button portfolioBtn = new Button("Portfolio");
    Button historyBtn   = new Button("History");
    Button settingsBtn  = new Button("Settings");

    sidebarBtns.addAll(List.of(dashboardBtn, marketBtn, portfolioBtn, historyBtn, settingsBtn));
    sidebarBtns.forEach(b -> b.getStyleClass().add("sidebar-btn"));

    dashboardBtn.setOnAction(e -> { activateSidebarBtn(dashboardBtn); onTabSelected.accept(GameTab.DASHBOARD); });
    marketBtn.setOnAction(e ->    { activateSidebarBtn(marketBtn);    onTabSelected.accept(GameTab.MARKET);    });
    portfolioBtn.setOnAction(e -> { activateSidebarBtn(portfolioBtn); onTabSelected.accept(GameTab.PORTFOLIO); });
    historyBtn.setOnAction(e ->   { activateSidebarBtn(historyBtn);   onTabSelected.accept(GameTab.HISTORY);   });
    settingsBtn.setOnAction(e ->  { activateSidebarBtn(settingsBtn);  onTabSelected.accept(GameTab.SETTINGS);  });

    activateSidebarBtn(marketBtn);

    Button finishBtn = new Button("Finish Game");
    finishBtn.getStyleClass().addAll("sidebar-btn", "sidebar-btn-secondary");
    finishBtn.setOnAction(e -> new FinishGameWidget(onFinish).openDialog(getScene().getWindow()));

    Region spacer = new Region();
    VBox.setVgrow(spacer, Priority.ALWAYS);

    VBox sidebar = new VBox();
    sidebar.getStyleClass().add("sidebar");
    sidebar.getChildren().addAll(dashboardBtn, marketBtn, portfolioBtn, historyBtn, spacer, settingsBtn, finishBtn);
    return sidebar;
  }

  /**
   * Marks the given sidebar button as active and deactivates the previously active button.
   *
   * @param target the button to activate
   */
  private void activateSidebarBtn(Button target) {
    if (activeBtn != null) {
      activeBtn.getStyleClass().remove("active");
    }
    target.getStyleClass().add("active");
    activeBtn = target;
  }

  /**
   * Builds the top navigation bar displaying session metadata.
   *
   * @return the assembled navbar {@link HBox}
   */
  private HBox buildNavbar() {
    Label title = new Label("Millions");
    title.getStyleClass().add("navbar-title");

    weekLabel.getStyleClass().add("navbar-info");
    balanceLabel.getStyleClass().add("navbar-info");
    playerNameLabel.getStyleClass().add("navbar-info");

    Label weekDesc = new Label("WEEK");
    weekDesc.getStyleClass().add("navbar-info-desc");
    VBox weekContainer = new VBox(2);
    weekContainer.getStyleClass().add("navbar-info-container");
    weekContainer.getChildren().addAll(weekDesc, weekLabel);

    Label balanceDesc = new Label("BALANCE");
    balanceDesc.getStyleClass().add("navbar-info-desc");
    VBox balanceContainer = new VBox(2);
    balanceContainer.getStyleClass().add("navbar-info-container");
    balanceContainer.getChildren().addAll(balanceDesc, balanceLabel);

    Label playerDesc = new Label("PLAYER");
    playerDesc.getStyleClass().add("navbar-info-desc");
    VBox playerContainer = new VBox(2);
    playerContainer.getStyleClass().add("navbar-info-container");
    playerContainer.getChildren().addAll(playerDesc, playerNameLabel);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox navbar = new HBox();
    navbar.getStyleClass().add("navbar");
    navbar.setAlignment(Pos.CENTER_LEFT);
    HBox.setMargin(statusLabel, new Insets(0, 0, 0, 16));
    navbar.getChildren().addAll(title, spacer, weekContainer, balanceContainer, playerContainer, statusLabel);
    return navbar;
  }

  /**
   * Refreshes all dynamic navbar labels from the controller.
   *
   * <p>Called automatically whenever the game state changes via the observer mechanism.</p>
   */
  @Override
  public void update() {
    weekLabel.setText("Week " + gameController.getCurrentWeek());
    balanceLabel.setText(FormatUtil.formatCurrency(gameController.getPlayerMoney()));
    playerNameLabel.setText(gameController.getPlayerName());
    applyStatusStyle(gameController.getPlayerStatus());
  }

  /**
   * Updates the status badge text and CSS class to reflect the player's current status.
   *
   * @param status the lowercase-compatible name of the current
   *               {@link edu.ntnu.idi.idatt.model.Player.Status}
   */
  private void applyStatusStyle(String status) {
    statusLabel.setText(status.toUpperCase());
    statusLabel.getStyleClass().setAll("status-badge", "status-badge-" + status.toLowerCase());
  }

}
