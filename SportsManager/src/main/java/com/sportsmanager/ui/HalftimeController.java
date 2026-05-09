package com.sportsmanager.ui;

import com.sportsmanager.core.model.AbstractTactic;
import com.sportsmanager.core.model.AbstractTeam;
import com.sportsmanager.core.model.Fixture;
import com.sportsmanager.core.model.PeriodResult;
import com.sportsmanager.sport.football.FootballTactic;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class HalftimeController {

    @FXML private Label scorelabel;
    @FXML private Label firsthalfsumlabel;
    @FXML private Label currenttacticlabel;
    @FXML private ListView<String> tacticlist;

    private static final String[] TACTIC_NAMES = {
            "4-4-2", "4-2-3-1", "4-3-3", "4-2-4",
            "3-5-2", "3-4-3", "5-3-2", "5-4-1"
    };

    private AbstractTeam userTeam;

    @FXML
    public void initialize(){
        tacticlist.setItems(FXCollections.observableArrayList(TACTIC_NAMES));
        tacticlist.setCellFactory(lv -> new ListCell<String>(){
            @Override
            protected void updateItem(String name, boolean empty){
                super.updateItem(name, empty);
                if (name == null || empty){
                    setText(null);
                } else {
                    FootballTactic t = createbyname(name);
                    if (t != null){
                        setText(name + "   Off " + pct(t.getOffensiveModifier())
                                + " · Def " + pct(t.getDefensiveModifier())
                                + " · Press " + pct(t.getPressureModifier()));
                    } else {
                        setText(name);
                    }
                }
            }
        });
    }

    private String pct(double mod){
        int p = (int) Math.round((mod - 1.0) * 100);
        return (p >= 0 ? "+" : "") + p + "%";
    }

    public void setMatchInfo(Fixture fixture, PeriodResult firstHalf, AbstractTeam userTeam){
        this.userTeam = userTeam;
        boolean isHome = fixture.getHomeTeam().equals(userTeam);
        scorelabel.setText(fixture.getHomeTeam().getName() + "  "
                + firstHalf.getHomeScore() + " - " + firstHalf.getAwayScore()
                + "  " + fixture.getAwayTeam().getName());
        firsthalfsumlabel.setText(formatSummary(firstHalf, isHome));

        AbstractTactic current = userTeam.getCurrentTactic();
        currenttacticlabel.setText(current != null ? current.getName() : "-");
        if (current != null){
            tacticlist.getSelectionModel().select(current.getName());
        } else {
            tacticlist.getSelectionModel().selectFirst();
        }
    }

    private String formatSummary(PeriodResult r, boolean isHome){
        int us = isHome ? r.getHomeScore() : r.getAwayScore();
        int them = isHome ? r.getAwayScore() : r.getHomeScore();
        if (us > them)  return "You're ahead at the break.";
        if (us < them)  return "You're trailing at the break.";
        return "Level at the break.";
    }

    @FXML
    public void applybutton(){
        String selected = tacticlist.getSelectionModel().getSelectedItem();
        if (selected == null){
            showalert("Tactic", "No tactic selected!");
            return;
        }
        if (userTeam == null) return;

        FootballTactic t = createbyname(selected);
        if (t == null) return;

        int missing = t.countMissingPositions(userTeam.getSquad());
        double penalty = Math.max(0.50, 1.0 - missing * 0.05);
        t.setMismatchPenalty(penalty);

        userTeam.setCurrentTactic(t);
        currenttacticlabel.setText(t.getName());

        String msg;
        if (missing == 0) {
            msg = "Switched to " + t.getName() + " for the 2nd half.";
        } else {
            int pct = (int) Math.round((1.0 - penalty) * 100);
            msg = "Switched to " + t.getName() + ".\n"
                + missing + " position(s) short — modifiers reduced by " + pct + "%.";
        }
        showalert("Tactic Applied", msg);
    }

    @FXML
    public void continuebutton(){
        Stage stage = (Stage) currenttacticlabel.getScene().getWindow();
        stage.close();
    }

    private FootballTactic createbyname(String name){
        switch (name){
            case "4-4-2":   return FootballTactic.create442();
            case "4-2-3-1": return FootballTactic.create4231();
            case "4-3-3":   return FootballTactic.create433();
            case "4-2-4":   return FootballTactic.create424();
            case "3-5-2":   return FootballTactic.create352();
            case "3-4-3":   return FootballTactic.create343();
            case "5-3-2":   return FootballTactic.create532();
            case "5-4-1":   return FootballTactic.create541();
            default:        return null;
        }
    }

    private void showalert(String title, String content){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
