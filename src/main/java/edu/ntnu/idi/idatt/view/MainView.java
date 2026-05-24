package edu.ntnu.idi.idatt.view;

import edu.ntnu.idi.idatt.controllers.GameController;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MainView extends BorderPane implements GameObserver {

  private final GameController gameController;
  private final Label weekLabel;
  private final Label balanceLabel;
  private final Label playerNameLabel;
  private final Label statusLabel;

  private final List<Button> sidebarBtns = new ArrayList<>();
  private Button activeBtn;

  public MainView(GameController gameController, Consumer<GameTab> onTabSelected) {
    this.gameController = gameController;

    this.weekLabel = new Label();
    this.balanceLabel = new Label();
    this.playerNameLabel = new Label();
    this.statusLabel = new Label();

    getStyleClass().add("main-view");
    setTop(buildNavbar());
    setLeft(buildSidebar(onTabSelected));
    gameController.registerObserver(this);
    update();
  }

  public void setContent(Parent content) {
    setCenter(content);
  }

  private VBox buildSidebar(Consumer<GameTab> onTabSelected) {
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

    VBox sidebar = new VBox();
    sidebar.getStyleClass().add("sidebar");
    sidebar.getChildren().addAll(dashboardBtn, marketBtn, portfolioBtn, historyBtn, settingsBtn);
    return sidebar;
  }

  private void activateSidebarBtn(Button target) {
    if (activeBtn != null) activeBtn.getStyleClass().remove("active");
    target.getStyleClass().add("active");
    activeBtn = target;
  }

  private HBox buildNavbar() {
    Label title = new Label("Millions");
    title.getStyleClass().add("navbar-title");

    weekLabel.getStyleClass().add("navbar-info");
    balanceLabel.getStyleClass().add("navbar-info");
    playerNameLabel.getStyleClass().add("navbar-info");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox navbar = new HBox();
    navbar.getStyleClass().add("navbar");
    navbar.setAlignment(Pos.CENTER_LEFT);
    navbar.getChildren().addAll(title, spacer, weekLabel, balanceLabel, playerNameLabel, statusLabel);
    return navbar;
  }

  @Override
  public void update() {
    weekLabel.setText("Week " + gameController.getCurrentWeek());
    balanceLabel.setText(ViewUtils.formatCurrency(gameController.getPlayerMoney()));
    playerNameLabel.setText(gameController.getPlayerName());
    applyStatusStyle(gameController.getPlayerStatus());
  }

  private void applyStatusStyle(String status) {
    statusLabel.setText(status.toUpperCase());
    statusLabel.getStyleClass().setAll("status-badge", "status-badge-" + status.toLowerCase());
  }

}
