package com.sportsmanager.ui;

import com.sportsmanager.core.gamesession.GameController;
import com.sportsmanager.core.model.AbstractTactic;
import com.sportsmanager.core.model.AbstractTeam;
import com.sportsmanager.sport.basketball.BasketballPositions;
import com.sportsmanager.sport.basketball.BasketballTactic;
import com.sportsmanager.sport.football.FootballPositions;
import com.sportsmanager.sport.football.FootballTactic;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TacticController {

    @FXML private Label teamnamelabel;
    @FXML private Label currenttacticlabel;
    @FXML private ListView<String> tacticlist;
    @FXML private Label formationlabel;
    @FXML private Label attackweightlabel;
    @FXML private Label defenseweightlabel;
    @FXML private Label pressureweightlabel;
    @FXML private Label offmodlabel;
    @FXML private Label defmodlabel;
    @FXML private Label pressmodlabel;
    @FXML private Label positionslabel;

    private static final String[] FOOTBALL_TACTIC_NAMES = {
            "4-4-2", "4-2-3-1", "4-3-3", "4-2-4",
            "3-5-2", "3-4-3", "5-3-2", "5-4-1"
    };

    private static final String[] BASKETBALL_TACTIC_NAMES = {
            "Balanced", "Offensive", "Defensive",
    };

    private boolean isBasketball;

    @FXML
    public void initialize() {
        GameController gc = GameController.getInstance();
        if (gc == null) return;
        AbstractTeam team = gc.getUserTeam();
        if (team == null) return;

        isBasketball = "basketball".equals(gc.getSport().getSportId());

        teamnamelabel.setText(team.getName());
        AbstractTactic current = team.getCurrentTactic();
        currenttacticlabel.setText("Current: " + (current != null ? current.getName() : "-"));

        String[] names = isBasketball ? BASKETBALL_TACTIC_NAMES : FOOTBALL_TACTIC_NAMES;
        tacticlist.setItems(FXCollections.observableArrayList(names));

        tacticlist.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) showdetails(newSel);
        });

        if (current != null) {
            tacticlist.getSelectionModel().select(current.getName());
        } else {
            tacticlist.getSelectionModel().selectFirst();
        }
    }

    private void showdetails(String tacticname) {
        AbstractTactic t = createbyname(tacticname);
        if (t == null) return;

        formationlabel.setText("Formation: " + t.getFormationString());
        attackweightlabel.setText("Attack: " + percent(t.getAttackingWeight()));
        defenseweightlabel.setText("Defense: " + percent(t.getDefensiveWeight()));
        pressureweightlabel.setText("Pressing: " + percent(t.getPressureIntensity()));
        offmodlabel.setText("Offensive: " + formatmod(t.getOffensiveModifier()));
        defmodlabel.setText("Defensive: " + formatmod(t.getDefensiveModifier()));
        pressmodlabel.setText("Pressing: " + formatmod(t.getPressureModifier()));

        if (isBasketball && t instanceof BasketballTactic bt) {
            positionslabel.setText(formatBasketballPositions(bt.getRequiredPositions()));
        } else if (!isBasketball && t instanceof FootballTactic ft) {
            positionslabel.setText(formatFootballPositions(ft.getRequiredPositions()));
        } else {
            positionslabel.setText("");
        }
    }

    @FXML
    public void applybutton() {
        String selected = tacticlist.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showalert("Tactic", "No tactic selected!");
            return;
        }
        GameController gc = GameController.getInstance();
        if (gc == null) return;
        AbstractTeam team = gc.getUserTeam();
        if (team == null) return;

        AbstractTactic t = createbyname(selected);
        if (t == null) return;

        try {
            t.validateForSquad(team.getSquad());
        } catch (IllegalArgumentException e) {
            showalert("Tactic Error", e.getMessage());
            return;
        }

        team.setCurrentTactic(t);
        currenttacticlabel.setText("Current: " + t.getName());
        showalert("Tactic Applied", "Your team will now play " + t.getName() + ".");
    }

    private AbstractTactic createbyname(String name) {
        if (isBasketball) {
            switch (name) {
                case "Offensive":     return BasketballTactic.createOffensive();
                case "Defensive":     return BasketballTactic.createDefensive();
                case "Balanced":      return BasketballTactic.createBalanced();
                default:              return null;
            }
        } else {
            switch (name) {
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
    }

    private String percent(double v) {
        return Math.round(v * 100) + "%";
    }

    private String formatmod(double mod) {
        int pct = (int) Math.round((mod - 1.0) * 100);
        String sign = pct >= 0 ? "+" : "";
        return sign + pct + "% (" + String.format("%.2fx", mod) + ")";
    }

    private String formatFootballPositions(Map<FootballPositions, Integer> positions) {
        List<String> parts = new ArrayList<>();
        for (Map.Entry<FootballPositions, Integer> e : positions.entrySet()) {
            parts.add(e.getKey() + ":" + e.getValue());
        }
        return String.join(" · ", parts);
    }

    private String formatBasketballPositions(Map<BasketballPositions, Integer> positions) {
        List<String> parts = new ArrayList<>();
        for (Map.Entry<BasketballPositions, Integer> e : positions.entrySet()) {
            parts.add(e.getKey() + ":" + e.getValue());
        }
        return String.join(" · ", parts);
    }

    @FXML
    public void returnbutton() throws IOException {
        URL dashboardurl = getClass().getResource("/DashboardView.fxml");
        if (dashboardurl == null) {
            System.out.println("DashboardView.fxml not found!");
            return;
        }
        Parent dashboardroot = FXMLLoader.load(dashboardurl);
        Scene dashboardscene = new Scene(dashboardroot);
        App.mainstage.setScene(dashboardscene);
    }

    @FXML
    public void exitbutton() {
        System.out.println("Game has been closed");
        System.exit(0);
    }

    private void showalert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
