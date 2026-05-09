package com.sportsmanager.ui;

import com.sportsmanager.core.gamesession.GameController;
import com.sportsmanager.core.model.AbstractCoach;
import com.sportsmanager.core.model.AbstractTeam;
import com.sportsmanager.core.model.CoachSpecialty;
import com.sportsmanager.core.model.Fixture;
import com.sportsmanager.core.model.MatchResult;
import com.sportsmanager.core.model.PeriodResult;
import com.sportsmanager.core.model.TeamInLeagueTable;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextInputDialog;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DashboardController {

    @FXML private Label userTeamLabel;
    @FXML private Label weekLabel;
    @FXML private Label totalWeeksLabel;
    @FXML private Label opponentLabel;
    @FXML private Label matchTypeLabel;
    @FXML private Label nextMatchTitleLabel;
    @FXML private Label opponentPositionLabel;
    @FXML private Label opponentRecordLabel;
    @FXML private Label userPositionLabel;
    @FXML private Label userRecordLabel;
    @FXML private Label opponentOvrLabel;
    @FXML private Label opponentMatchOvrLabel;
    @FXML private Label userOvrLabel;
    @FXML private Label userMatchOvrLabel;
    @FXML private Label coachLabel;
    @FXML private Label coachBoostLabel;
    @FXML private ProgressBar seasonProgress;

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

        int week = gc.getLeague().getCurrentWeek();
        int totalWeeks = computeTotalWeeks(gc);
        weekLabel.setText(String.valueOf(week));
        totalWeeksLabel.setText(String.valueOf(totalWeeks));
        seasonProgress.setProgress(totalWeeks > 0 ? Math.min(1.0, (week - 1.0) / totalWeeks) : 0.0);

        AbstractTeam userTeam = gc.getUserTeam();
        int onField = gc.getSport().getPlayersOnField();
        int userOvr = userTeam != null ? userTeam.getAvgOvr(onField) : 0;

        if (gc.getLeague().isSeasonOver()) {
            AbstractTeam champion = gc.getLeague().getChampion();
            nextMatchTitleLabel.setText("SEASON OVER");
            matchTypeLabel.setText("Champion: " + (champion != null ? champion.getName() : "?"));
            matchTypeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #b8860b; -fx-font-size: 12;");
            opponentLabel.setText("-");
            opponentPositionLabel.setText("");
            opponentRecordLabel.setText("");
            opponentOvrLabel.setText("");
            opponentMatchOvrLabel.setText("");
            userPositionLabel.setText("Final position: " + ordinal(positionOf(gc, userTeam)));
            userRecordLabel.setText("Form: " + formatRecord(findStat(gc, userTeam)));
            userOvrLabel.setText("OVR: " + userOvr);
            fillMatchOvr(userMatchOvrLabel, userTeam);
            fillCoachInfo(userTeam, userOvr);
            seasonProgress.setProgress(1.0);
            return;
        }

        nextMatchTitleLabel.setText("NEXT MATCH");
        Fixture f = gc.getUserFixture();
        if (f != null) {
            boolean isHome = f.getHomeTeam().equals(userTeam);
            AbstractTeam opp = isHome ? f.getAwayTeam() : f.getHomeTeam();
            matchTypeLabel.setText(isHome ? "HOME" : "AWAY");
            matchTypeLabel.setStyle(isHome
                    ? "-fx-font-weight: bold; -fx-text-fill: #009487; -fx-font-size: 12;"
                    : "-fx-font-weight: bold; -fx-text-fill: #c44; -fx-font-size: 12;");
            opponentLabel.setText(opp.getName());
            opponentPositionLabel.setText("Position: " + ordinal(positionOf(gc, opp)));
            opponentRecordLabel.setText("Form: " + formatRecord(findStat(gc, opp)));
            opponentOvrLabel.setText("OVR: " + opp.getAvgOvr(onField));
            fillMatchOvr(opponentMatchOvrLabel, opp);
        } else {
            matchTypeLabel.setText("BYE");
            matchTypeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #777; -fx-font-size: 12;");
            opponentLabel.setText("-");
            opponentPositionLabel.setText("");
            opponentRecordLabel.setText("");
            opponentOvrLabel.setText("");
            opponentMatchOvrLabel.setText("");
        }
        userPositionLabel.setText("Position: " + ordinal(positionOf(gc, userTeam)));
        userRecordLabel.setText("Form: " + formatRecord(findStat(gc, userTeam)));
        userOvrLabel.setText("OVR: " + userOvr);
        fillMatchOvr(userMatchOvrLabel, userTeam);
        fillCoachInfo(userTeam, userOvr);
    }

    private void fillMatchOvr(Label label, AbstractTeam team) {
        if (team == null) {
            label.setText("");
            return;
        }
        int injured = team.getInjuredPlayers().size();
        if (injured == 0) {
            label.setText("");
            return;
        }
        GameController gc = GameController.getInstance();
        int onField = gc != null ? gc.getSport().getPlayersOnField() : 11;
        int matchOvr = team.getAvailableAvgOvr(onField);
        label.setText("Match OVR: " + matchOvr + " (" + injured + " injured)");
    }

    private void fillCoachInfo(AbstractTeam team, int teamOvr) {
        if (team == null) {
            coachLabel.setText("-");
            coachBoostLabel.setText("");
            return;
        }
        AbstractCoach coach = team.getActiveCoach();
        if (coach == null) {
            coachLabel.setText("None — players self-train");
            coachBoostLabel.setText("");
            return;
        }
        coachLabel.setText(coach.getName() + " (" + coach.getSpecialty() + " L" + coach.getCoachingLevel() + ")");
        coachBoostLabel.setText(formatCoachBoost(coach, teamOvr));
    }

    private String formatCoachBoost(AbstractCoach coach, int teamOvr) {
        int lvl = coach.getCoachingLevel();
        CoachSpecialty s = coach.getSpecialty();
        if (s == CoachSpecialty.ATTACKING) {
            int bonus = (int) Math.round(teamOvr * lvl * 0.02);
            return "+" + bonus + " OVR offensive";
        }
        if (s == CoachSpecialty.DEFENDING) {
            int bonus = (int) Math.round(teamOvr * lvl * 0.02);
            return "+" + bonus + " OVR defensive";
        }
        if (s == CoachSpecialty.FITNESS) {
            return "-" + (lvl * 10) + "% match injury risk";
        }
        if (s == CoachSpecialty.GENERAL) {
            int bonus = (int) Math.round(teamOvr * lvl * 0.01);
            return "+" + bonus + " OVR both ways";
        }
        return "";
    }

    private int computeTotalWeeks(GameController gc) {
        int max = 0;
        for (Fixture f : gc.getLeague().getFixtures()) {
            if (f.getWeek() > max) max = f.getWeek();
        }
        return max;
    }

    private int positionOf(GameController gc, AbstractTeam team) {
        if (team == null) return -1;
        List<TeamInLeagueTable> standings = gc.getLeague().getStandings();
        for (int i = 0; i < standings.size(); i++) {
            if (standings.get(i).getTeam().equals(team)) return i + 1;
        }
        return -1;
    }

    private TeamInLeagueTable findStat(GameController gc, AbstractTeam team) {
        if (team == null) return null;
        for (TeamInLeagueTable s : gc.getLeague().getStandings()) {
            if (s.getTeam().equals(team)) return s;
        }
        return null;
    }

    private String formatRecord(TeamInLeagueTable s) {
        if (s == null) return "0W 0D 0L";
        return s.getWins() + "W " + s.getDraws() + "D " + s.getLosses() + "L";
    }

    private String ordinal(int n) {
        if (n < 1) return "-";
        int rem100 = n % 100;
        if (rem100 >= 11 && rem100 <= 13) return n + "th";
        int rem10 = n % 10;
        String suffix = (rem10 == 1) ? "st" : (rem10 == 2) ? "nd" : (rem10 == 3) ? "rd" : "th";
        return n + suffix;
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
        List<MatchResult> allResults = new ArrayList<>();

        Fixture userFixture = gc.getUserFixture();
        MatchResult userResult = null;
        if (userFixture != null) {
            PeriodResult firstHalf = gc.startUserMatch();
            if (firstHalf != null) {
                showHalftimeDialog(userFixture, firstHalf, gc.getUserTeam());
            }
            userResult = gc.finishUserMatch();
        }

        List<MatchResult> otherResults = gc.simulateOtherMatchesThisWeek();
        if (userResult != null) allResults.add(userResult);
        allResults.addAll(otherResults);
        gc.advanceWeek();

        StringBuilder sb = new StringBuilder();
        for (MatchResult r : allResults) {
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

    private void showHalftimeDialog(Fixture fixture, PeriodResult firstHalf, AbstractTeam userTeam) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HalftimeView.fxml"));
            Parent root = loader.load();
            HalftimeController controller = loader.getController();
            controller.setMatchInfo(fixture, firstHalf, userTeam);
            Stage stage = new Stage();
            stage.setTitle("Half Time");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            System.out.println("HalftimeView.fxml could not be loaded: " + e.getMessage());
        }
    }

    @FXML
    public void exitbutton() {
        System.out.println("Game has been closed");
        System.exit(0);
    }

    @FXML
    public void squadbutton() throws IOException {
        URL squadurl = getClass().getResource("/SquadView.fxml");
        if (squadurl == null){
            System.out.println("SquadView.fxml not found!");
            return;
        }
        Parent squadroot = FXMLLoader.load(squadurl);
        Scene squadscene = new Scene(squadroot);
        App.mainstage.setScene(squadscene);
    }

    @FXML
    public void pointstablebutton() throws IOException {
        URL pointsurl = getClass().getResource("/PointsTableView.fxml");
        if (pointsurl == null){
            System.out.println("PointsTableView.fxml not found!");
            return;
        }
        Parent pointsroot = FXMLLoader.load(pointsurl);
        Scene pointsscene = new Scene(pointsroot);
        App.mainstage.setScene(pointsscene);
    }

    @FXML
    public void fixturebutton() throws IOException {
        URL fixtureurl = getClass().getResource("/FixtureView.fxml");
        if (fixtureurl == null){
            System.out.println("FixtureView.fxml not found!");
            return;
        }
        Parent fixtureroot = FXMLLoader.load(fixtureurl);
        Scene fixturescene = new Scene(fixtureroot);
        App.mainstage.setScene(fixturescene);
    }

    @FXML
    public void tacticbutton() throws IOException {
        URL tacticurl = getClass().getResource("/TacticView.fxml");
        if (tacticurl == null){
            System.out.println("TacticView.fxml not found!");
            return;
        }
        Parent tacticroot = FXMLLoader.load(tacticurl);
        Scene tacticscene = new Scene(tacticroot);
        App.mainstage.setScene(tacticscene);
    }

    @FXML
    public void trainbutton() throws IOException {
        URL trainurl = getClass().getResource("/TrainView.fxml");
        if (trainurl == null){
            System.out.println("TrainView.fxml not found!");
            return;
        }
        Parent trainroot = FXMLLoader.load(trainurl);
        Scene trainscene = new Scene(trainroot);
        App.mainstage.setScene(trainscene);
    }

    @FXML
    public void coachbutton() throws IOException {
        URL coachurl = getClass().getResource("/CoachView.fxml");
        if (coachurl == null){
            System.out.println("CoachView.fxml not found!");
            return;
        }
        Parent coachroot = FXMLLoader.load(coachurl);
        Scene coachscene = new Scene(coachroot);
        App.mainstage.setScene(coachscene);
    }

    @FXML
    public void saveandexitbutton() {
        GameController gc = GameController.getInstance();
        if (gc == null) { System.exit(0); return; }

        String savename = gc.getCurrentSaveName();
        if (savename == null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Save Game");
            dialog.setHeaderText("Enter a name for your save");
            dialog.setContentText("Save name:");
            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty() || result.get().isBlank()) {
                return;
            }
            savename = result.get().trim();
        }

        try {
            gc.saveGame(savename);
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
