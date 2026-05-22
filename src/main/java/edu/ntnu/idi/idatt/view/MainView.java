package edu.ntnu.idi.idatt.view;

import edu.ntnu.idi.idatt.controllers.GameController;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class MainView extends BorderPane implements GameObserver {

  private final GameController gameController;
  private final Label weekLabel;
  private final Label balanceLabel;
  private final Label playerNameLabel;
  private final Label statusLabel;


  public MainView(GameController gameController, Consumer<GameTab> onTabSelected) {
    this.gameController = gameController;


    this.weekLabel = new Label();
    this.balanceLabel = new Label();
    this.playerNameLabel = new Label();
    this.statusLabel = new Label();


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
    Button marketBtn = new Button("Market");
    Button portfolioBtn = new Button("Portfolio");
    Button historyBtn = new Button("History");

    dashboardBtn.setOnAction(event -> onTabSelected.accept(GameTab.DASHBOARD));
    marketBtn.setOnAction(event -> onTabSelected.accept(GameTab.MARKET));
    portfolioBtn.setOnAction(event -> onTabSelected.accept(GameTab.PORTFOLIO));
    historyBtn.setOnAction(event -> onTabSelected.accept(GameTab.HISTORY));

    VBox sidebar = new VBox();



    sidebar.getChildren().addAll(dashboardBtn, marketBtn, portfolioBtn, historyBtn);

    return sidebar;
  }

  private HBox buildNavbar() {

    Label title = new Label("Millions");

    HBox navbar = new HBox();
    navbar.getChildren().addAll(title, weekLabel, balanceLabel, playerNameLabel, statusLabel);

    return navbar;

  }


  @Override
  public void update() {
    weekLabel.setText("Week \n" + gameController.getCurrentWeek());
    balanceLabel.setText("Balance \n" + ViewUtils.formatCurrency(gameController.getPlayerMoney()));
    playerNameLabel.setText("Player \n" + gameController.getPlayerName());
    applyStatusStyle(gameController.getPlayerStatus());
  }

  private void applyStatusStyle(String status) {
    statusLabel.setText(status);
    statusLabel.getStyleClass().setAll("status-badge", "status-badge-" + status.toLowerCase());
  }

}
