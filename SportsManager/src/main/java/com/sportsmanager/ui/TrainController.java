package com.sportsmanager.ui;

import com.sportsmanager.core.gamesession.GameController;
import com.sportsmanager.core.gamesession.GameController.TrainingReport;
import com.sportsmanager.core.model.AbstractCoach;
import com.sportsmanager.core.model.AbstractPlayer;
import com.sportsmanager.core.model.AbstractPlayerAttributes;
import com.sportsmanager.core.model.AbstractTeam;
import com.sportsmanager.core.model.Injury;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class TrainController {

    @FXML private Label teamnamelabel;
    @FXML private Label sessionslabel;
    @FXML private Label coachlabel;
    @FXML private Label coachmodlabel;
    @FXML private Slider intensityslider;
    @FXML private Label intensitylabel;
    @FXML private Label effectlabel;
    @FXML private Button runbutton;
    @FXML private Label resultheaderlabel;
    @FXML private Label trainedlabel;
    @FXML private Label skippedlabel;
    @FXML private Label ovrbeforelabel;
    @FXML private Label ovrafterlabel;
    @FXML private Label ovrdeltalabel;
    @FXML private Label newinjuredlabel;

    @FXML
    public void initialize(){
        GameController gc = GameController.getInstance();
        if (gc == null) return;
        AbstractTeam team = gc.getUserTeam();
        if (team == null) return;

        teamnamelabel.setText(team.getName());
        refreshsessions();
        refreshcoach();

        intensityslider.valueProperty().addListener((obs, oldv, newv) -> refreshpreview());
        refreshpreview();
    }

    private void refreshsessions(){
        GameController gc = GameController.getInstance();
        if (gc == null) return;
        int left = gc.getTrainingsLeft();
        sessionslabel.setText("Sessions left this week: " + left + " / " + GameController.MAX_TRAININGS_PER_WEEK
                + "  (Week " + gc.getLeague().getCurrentWeek() + ")");
        runbutton.setDisable(!gc.canTrain());
    }

    private void refreshcoach(){
        GameController gc = GameController.getInstance();
        if (gc == null) return;
        AbstractTeam team = gc.getUserTeam();
        if (team == null) return;

        AbstractCoach coach = team.getActiveCoach();
        if (coach == null){
            coachlabel.setText("Coach: none — players self-train");
            coachmodlabel.setText("Multiplier: 0.80x");
        } else {
            coachlabel.setText("Coach: " + coach.getName() + " (Lv " + coach.getCoachingLevel()
                    + " · " + coach.getSpecialty() + ")");
            double mod = coachmod(coach);
            coachmodlabel.setText(String.format("Multiplier: %.2fx", mod));
        }
    }

    private void refreshpreview(){
        double intensity = intensityslider.getValue();
        intensitylabel.setText(Math.round(intensity * 100) + "%");
        double mod = currentcoachmod();
        double effective = intensity * mod;
        double youngBoost  = effective * 1.3 * 0.5;
        double normalBoost = effective * 1.0 * 0.5;
        double oldBoost    = effective * 0.7 * 0.5;
        double riskPerPlayer = intensity * intensity * 0.05;
        int healthy = healthycount();
        double expectedInjuries = riskPerPlayer * healthy;
        effectlabel.setText(String.format(
                "Effective intensity: %.2fx%n" +
                "Boost per attribute:%n" +
                "  young (<25):  +%.2f%n" +
                "  normal:       +%.2f%n" +
                "  veteran (>31): +%.2f%n" +
                "Injury risk per player: %.1f%%%n" +
                "Expected injuries:      ~%.2f",
                effective, youngBoost, normalBoost, oldBoost,
                riskPerPlayer * 100, expectedInjuries));
    }

    private int healthycount(){
        GameController gc = GameController.getInstance();
        if (gc == null) return 0;
        AbstractTeam team = gc.getUserTeam();
        if (team == null) return 0;
        return team.getAvailablePlayers().size();
    }

    private double currentcoachmod(){
        GameController gc = GameController.getInstance();
        if (gc == null) return 0.80;
        AbstractTeam team = gc.getUserTeam();
        if (team == null || team.getActiveCoach() == null) return 0.80;
        return coachmod(team.getActiveCoach());
    }

    private double coachmod(AbstractCoach coach){
        return coach.getTrainingEffectiveness() * coach.specialtyMultiplier();
    }

    @FXML
    public void runbutton(){
        GameController gc = GameController.getInstance();
        if (gc == null) return;
        AbstractTeam team = gc.getUserTeam();
        if (team == null) return;

        if (!gc.canTrain()){
            showalert("Training", "No sessions left this week.");
            return;
        }

        double beforeAvg = avgOverall(team.getSquad());
        int healthy = team.getAvailablePlayers().size();
        int injured = team.getInjuredPlayers().size();

        TrainingReport report;
        try {
            report = gc.trainTeam(intensityslider.getValue());
        } catch (IllegalStateException e) {
            showalert("Training", e.getMessage());
            return;
        }

        double afterAvg = avgOverall(team.getSquad());
        double delta = afterAvg - beforeAvg;

        resultheaderlabel.setText("Training session complete.");
        trainedlabel.setText(healthy + " healthy player(s)");
        skippedlabel.setText(injured + " injured player(s)");
        ovrbeforelabel.setText(String.format("Before: %.2f", beforeAvg));
        ovrafterlabel.setText(String.format("After:  %.2f", afterAvg));
        ovrdeltalabel.setText(String.format("Δ %+.2f OVR", delta));
        newinjuredlabel.setText(formatinjurylist(report.getNewlyInjured()));

        refreshsessions();
    }

    private String formatinjurylist(List<AbstractPlayer> injured){
        if (injured.isEmpty()) return "None — everyone safe.";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < injured.size(); i++){
            AbstractPlayer p = injured.get(i);
            Injury inj = p.getInjury();
            if (i > 0) sb.append("\n");
            sb.append(p.getName())
              .append(" (").append(inj.getSeverity())
              .append(", ").append(inj.getGamesRemaining()).append("w)");
        }
        return sb.toString();
    }

    private double avgOverall(List<AbstractPlayer> players){
        if (players.isEmpty()) return 0.0;
        double total = 0.0;
        for (AbstractPlayer p : players){
            AbstractPlayerAttributes a = p.getAttributes();
            if (a != null) total += a.getOverallRating();
        }
        return total / players.size();
    }

    @FXML
    public void returnbutton() throws IOException{
        URL dashboardurl = getClass().getResource("/DashboardView.fxml");
        if (dashboardurl==null){
            System.out.println("DashboardView.fxml not found!");
            return;
        }
        Parent dashboardroot = FXMLLoader.load(dashboardurl);
        Scene dashboardscene = new Scene(dashboardroot);
        App.mainstage.setScene(dashboardscene);
    }

    @FXML
    public void exitbutton(){
        System.out.println("Game has been closed");
        System.exit(0);
    }

    private void showalert(String title, String content){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
