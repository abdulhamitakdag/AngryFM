package com.sportsmanager.ui;

import com.sportsmanager.core.gamesession.GameController;
import com.sportsmanager.core.model.AbstractPlayer;
import com.sportsmanager.core.model.AbstractTeam;
import com.sportsmanager.core.model.TeamInLeagueTable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class EndOfSeasonController {

    @FXML private Label championLabel;
    @FXML private Label seasonTitleLabel;
    @FXML private TableView<TeamInLeagueTable> standingsTable;
    @FXML private TableColumn<TeamInLeagueTable, String> colTeam;
    @FXML private TableColumn<TeamInLeagueTable, Integer> colPts;
    @FXML private TableColumn<TeamInLeagueTable, Integer> colW;
    @FXML private TableColumn<TeamInLeagueTable, Integer> colD;
    @FXML private TableColumn<TeamInLeagueTable, Integer> colL;
    @FXML private TableColumn<TeamInLeagueTable, Integer> colGD;

    @FXML
    public void initialize() {
        GameController gc = GameController.getInstance();
        if (gc == null) return;

        AbstractTeam champion = gc.getLeague().getChampion();
        championLabel.setText(champion != null ? champion.getName() : "Unknown");
        seasonTitleLabel.setText(gc.getLeague().getName() + " — Season " + gc.getSeasonNumber() + " Over");

        colTeam.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTeam().getName()));
        colPts.setCellValueFactory(new PropertyValueFactory<>("points"));
        colW.setCellValueFactory(new PropertyValueFactory<>("wins"));
        colD.setCellValueFactory(new PropertyValueFactory<>("draws"));
        colL.setCellValueFactory(new PropertyValueFactory<>("losses"));
        colGD.setCellValueFactory(new PropertyValueFactory<>("goalDifference"));

        List<TeamInLeagueTable> standings = gc.getLeague().getStandings();
        standingsTable.setItems(FXCollections.observableArrayList(standings));
    }

    @FXML
    public void newSeasonButton() throws IOException {
        GameController gc = GameController.getInstance();
        if (gc == null) return;

        GameController.SeasonTransitionResult result = gc.startNewSeason();

        showSeasonTransitionReport(result, gc);

        URL url = getClass().getResource("/DashboardView.fxml");
        if (url == null) { System.out.println("DashboardView.fxml not found!"); return; }
        Parent root = FXMLLoader.load(url);
        App.mainstage.setScene(new Scene(root));
    }

    private void showSeasonTransitionReport(GameController.SeasonTransitionResult result, GameController gc) {
        StringBuilder sb = new StringBuilder();
        sb.append("Welcome to Season ").append(result.getNewSeasonNumber()).append("!\n\n");

        sb.append("All players aged +1 year. Attributes updated based on age.\n\n");

        List<AbstractPlayer> userRetired = result.getUserRetiredPlayers();
        List<AbstractPlayer> userRegen = result.getUserRegenPlayers();
        int otherRetiredCount = result.getRetiredPlayers().size() - userRetired.size();

        if (!userRetired.isEmpty()) {
            sb.append("YOUR RETIRED PLAYERS:\n");
            for (AbstractPlayer p : userRetired) {
                sb.append("  ").append(p.getName())
                  .append(" (").append(p.getPosition())
                  .append(", age ").append(p.getAge()).append(")\n");
            }
            sb.append("\n");
        }

        if (otherRetiredCount > 0) {
            sb.append("OTHER RETIREMENTS: ").append(otherRetiredCount).append(" players\n\n");
        }

        if (userRetired.isEmpty() && otherRetiredCount == 0) {
            sb.append("No retirements this off-season.\n\n");
        }

        if (!userRegen.isEmpty()) {
            sb.append("NEW SIGNINGS (your team):\n");
            for (AbstractPlayer p : userRegen) {
                sb.append("  ").append(p.getName())
                  .append(" (").append(p.getPosition())
                  .append(", age ").append(p.getAge())
                  .append(", OVR ").append(p.getAttributes().getOverallRating()).append(")\n");
            }
        }

        TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(450, 300);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Season Transition");
        alert.setHeaderText("Season " + result.getNewSeasonNumber());
        alert.getDialogPane().setContent(new VBox(textArea));
        alert.getDialogPane().setPrefSize(500, 380);
        alert.showAndWait();
    }

    @FXML
    public void mainMenuButton() throws IOException {
        URL url = getClass().getResource("/MainMenuView.fxml");
        if (url == null) { System.out.println("MainMenuView.fxml not found!"); return; }
        Parent root = FXMLLoader.load(url);
        App.mainstage.setScene(new Scene(root));
    }
}
