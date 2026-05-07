package com.sportsmanager.ui;

import com.sportsmanager.core.gamesession.GameController;
import com.sportsmanager.core.model.Fixture;
import com.sportsmanager.core.model.MatchResult;
import com.sportsmanager.core.model.TeamInLeagueTable;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.util.List;

public class DashboardController {

    @FXML private Label userTeamLabel;
    @FXML private Label weekLabel;
    @FXML private Label opponentLabel;
    @FXML private Label matchTypeLabel;

    // FXML yüklenince otomatik çağrılır
    public void initialize() {
        refresh();
    }

    private void refresh() {
        GameController gc = GameController.getInstance();
        if (gc == null) return;

        if (gc.getUserTeam() != null) {
            userTeamLabel.setText(gc.getUserTeam().getName());
        }

        weekLabel.setText("Week " + gc.getLeague().getCurrentWeek());

        Fixture f = gc.getUserFixture();
        if (f != null) {
            boolean isHome = f.getHomeTeam().equals(gc.getUserTeam());
            opponentLabel.setText(isHome ? f.getAwayTeam().getName() : f.getHomeTeam().getName());
            matchTypeLabel.setText(isHome ? "HOME" : "AWAY");
        } else if (gc.getLeague().isSeasonOver()) {
            opponentLabel.setText("Season Over");
            matchTypeLabel.setText("Champion: " + gc.getLeague().getChampion().getName());
        } else {
            opponentLabel.setText("-");
            matchTypeLabel.setText("");
        }
    }

    @FXML
    public void playbutton() {
        GameController gc = GameController.getInstance();
        if (gc == null) return;

        if (gc.getLeague().isSeasonOver()) {
            List<TeamInLeagueTable> standings = gc.getLeague().getStandings();
            String champion = standings.isEmpty() ? "?" : standings.get(0).getTeam().getName();
            showAlert("Season Over!", "Champion: " + champion, "");
            return;
        }

        int playedWeek = gc.getLeague().getCurrentWeek();
        List<MatchResult> results = gc.simulateFullWeek();

        StringBuilder sb = new StringBuilder();
        for (MatchResult r : results) {
            boolean isUserMatch = gc.getUserTeam() != null &&
                    (r.getHomeTeam().equals(gc.getUserTeam()) || r.getAwayTeam().equals(gc.getUserTeam()));

            sb.append(r.getHomeTeam().getName())
              .append("  ")
              .append(r.getHomeScore())
              .append(" - ")
              .append(r.getAwayScore())
              .append("  ")
              .append(r.getAwayTeam().getName());

            if (isUserMatch) sb.append("  ◀ YOUR MATCH");
            sb.append("\n");
        }

        showAlert("Week " + playedWeek + " Results", null, sb.toString());
        refresh();
    }

    @FXML
    public void exitbutton() {
        System.out.println("Game has been closed");
        System.exit(0);
    }

    @FXML
    public void saveandexitbutton() {
        GameController gc = GameController.getInstance();
        if (gc == null) { System.exit(0); return; }
        try {
            gc.saveGame("autosave");
            System.exit(0);
        } catch (Exception e) {
            showAlert("Save Error", "Could not save the game", e.getMessage());
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
