package com.sportsmanager.ui;

import com.sportsmanager.core.gamesession.GameController;
import com.sportsmanager.core.model.AbstractMatch;
import com.sportsmanager.core.model.AbstractPlayer;
import com.sportsmanager.core.model.AbstractTactic;
import com.sportsmanager.core.model.AbstractTeam;
import com.sportsmanager.core.model.Fixture;
import com.sportsmanager.core.model.PeriodResult;
import com.sportsmanager.sport.basketball.BasketballTactic;
import com.sportsmanager.sport.football.FootballTactic;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;

public class HalftimeController {

    @FXML private Label titleLabel;
    @FXML private Label scorelabel;
    @FXML private Label firsthalfsumlabel;
    @FXML private Label currenttacticlabel;
    @FXML private Label switchTacticLabel;
    @FXML private Label subsRemainingLabel;
    @FXML private ListView<String> tacticlist;
    @FXML private ListView<AbstractPlayer> playerOutList;
    @FXML private ListView<AbstractPlayer> playerInList;
    @FXML private Button continueBtn;

    private static final String[] FOOTBALL_TACTICS = {
            "4-4-2", "4-2-3-1", "4-3-3", "4-2-4",
            "3-5-2", "3-4-3", "5-3-2", "5-4-1"
    };
    private static final String[] BASKETBALL_TACTICS = {
            "Balanced", "Offensive", "Defensive",
    };

    private AbstractTeam userTeam;
    private boolean isBasketball;

    @FXML
    public void initialize() {}

    public void setMatchInfo(Fixture fixture, PeriodResult halfScore, AbstractTeam userTeam,
                             boolean isBasketball, String breakTitle, String continueText) {
        this.userTeam = userTeam;
        this.isBasketball = isBasketball;

        titleLabel.setText(breakTitle);
        continueBtn.setText(continueText);
        if (isBasketball) switchTacticLabel.setText("Switch Tactic");

        boolean isHome = fixture.getHomeTeam().equals(userTeam);
        scorelabel.setText(fixture.getHomeTeam().getName() + "  "
                + halfScore.getHomeScore() + " - " + halfScore.getAwayScore()
                + "  " + fixture.getAwayTeam().getName());
        firsthalfsumlabel.setText(formatSummary(halfScore, isHome));

        // Tactic list
        String[] tactics = isBasketball ? BASKETBALL_TACTICS : FOOTBALL_TACTICS;
        tacticlist.setItems(FXCollections.observableArrayList(tactics));
        tacticlist.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (name == null || empty) { setText(null); return; }
                if (isBasketball) {
                    BasketballTactic t = createBasketballTactic(name);
                    setText(t != null ? name + "  Att " + pct(t.getAttackingWeight())
                            + " · Press " + pct(t.getPressureIntensity()) : name);
                } else {
                    FootballTactic t = createFootballTactic(name);
                    setText(t != null ? name + "  Off " + pctMod(t.getOffensiveModifier())
                            + " · Def " + pctMod(t.getDefensiveModifier()) : name);
                }
            }
        });
        AbstractTactic current = userTeam.getCurrentTactic();
        currenttacticlabel.setText(current != null ? current.getName() : "-");
        if (current != null) tacticlist.getSelectionModel().select(current.getName());
        else tacticlist.getSelectionModel().selectFirst();

        // Substitution lists
        refreshSubLists();
        updateSubsRemaining();
    }

    private void refreshSubLists() {
        List<AbstractPlayer> starting = userTeam.getStartingLineup();
        List<AbstractPlayer> bench    = userTeam.getBench();
        playerOutList.setItems(FXCollections.observableArrayList(starting));
        playerInList.setItems(FXCollections.observableArrayList(bench));

        playerOutList.setCellFactory(lv -> new ListCell<AbstractPlayer>() {
            @Override
            protected void updateItem(AbstractPlayer p, boolean empty) {
                super.updateItem(p, empty);
                setText((p == null || empty) ? null : formatPlayer(p));
            }
        });
        playerInList.setCellFactory(lv -> new ListCell<AbstractPlayer>() {
            @Override
            protected void updateItem(AbstractPlayer p, boolean empty) {
                super.updateItem(p, empty);
                setText((p == null || empty) ? null : formatPlayer(p));
            }
        });
    }

    private void updateSubsRemaining() {
        GameController gc = GameController.getInstance();
        AbstractMatch match = gc != null ? gc.getOngoingUserMatch() : null;
        if (match == null) { subsRemainingLabel.setText(""); return; }
        boolean isHome = match.getHomeTeam().equals(userTeam);
        int done = isHome ? match.getHomeSubCount() : match.getAwaySubCount();
        int max  = match.getMaxSubstitutions();
        if (max == Integer.MAX_VALUE) {
            subsRemainingLabel.setText("Subs: " + done + " made");
        } else {
            subsRemainingLabel.setText("Subs left: " + (max - done) + "/" + max);
        }
    }

    @FXML
    public void substitutebutton() {
        AbstractPlayer out = playerOutList.getSelectionModel().getSelectedItem();
        AbstractPlayer in  = playerInList.getSelectionModel().getSelectedItem();
        if (out == null || in == null) {
            showalert("Substitution", "Select a player to take off and one to bring on.");
            return;
        }
        GameController gc = GameController.getInstance();
        if (gc == null) return;
        try {
            gc.makeSubstitution(out, in);
            currenttacticlabel.setText(userTeam.getCurrentTactic() != null
                    ? userTeam.getCurrentTactic().getName() : "-");
            refreshSubLists();
            updateSubsRemaining();
            showalert("Substitution", in.getName() + " on for " + out.getName() + ".");
        } catch (IllegalStateException e) {
            showalert("Substitution failed", e.getMessage());
        }
    }

    @FXML
    public void applybutton() {
        String selected = tacticlist.getSelectionModel().getSelectedItem();
        if (selected == null) { showalert("Tactic", "No tactic selected!"); return; }
        if (userTeam == null) return;

        if (isBasketball) {
            BasketballTactic t = createBasketballTactic(selected);
            if (t == null) return;
            userTeam.setCurrentTactic(t);
            currenttacticlabel.setText(t.getName());
            showalert("Tactic Applied", "Switched to " + t.getName() + ".");
        } else {
            FootballTactic t = createFootballTactic(selected);
            if (t == null) return;
            int missing = t.countMissingPositions(userTeam.getSquad());
            double penalty = Math.max(0.50, 1.0 - missing * 0.05);
            t.setMismatchPenalty(penalty);
            userTeam.setCurrentTactic(t);
            currenttacticlabel.setText(t.getName());
            if (missing == 0) {
                showalert("Tactic Applied", "Switched to " + t.getName() + ".");
            } else {
                int pct = (int) Math.round((1.0 - penalty) * 100);
                showalert("Tactic Applied", "Switched to " + t.getName() + ".\n"
                        + missing + " position(s) short - modifiers reduced by " + pct + "%.");
            }
        }
    }

    @FXML
    public void continuebutton() {
        Stage stage = (Stage) currenttacticlabel.getScene().getWindow();
        stage.close();
    }

    private String formatPlayer(AbstractPlayer p) {
        int ovr = p.getAttributes() != null ? p.getAttributes().getOverallRating() : 0;
        return p.getName() + " (" + p.getPosition() + ", " + ovr + ")";
    }

    private String formatSummary(PeriodResult r, boolean isHome) {
        int us   = isHome ? r.getHomeScore() : r.getAwayScore();
        int them = isHome ? r.getAwayScore()  : r.getHomeScore();
        if (us > them) return "You're ahead at the break.";
        if (us < them) return "You're trailing at the break.";
        return "Level at the break.";
    }

    private String pct(double val) {
        return (int) Math.round(val * 100) + "%";
    }

    private String pctMod(double mod) {
        int p = (int) Math.round((mod - 1.0) * 100);
        return (p >= 0 ? "+" : "") + p + "%";
    }

    private FootballTactic createFootballTactic(String name) {
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

    private BasketballTactic createBasketballTactic(String name) {
        switch (name) {
            case "Balanced":       return BasketballTactic.createBalanced();
            case "Offensive":      return BasketballTactic.createOffensive();
            case "Defensive":      return BasketballTactic.createDefensive();
            default:               return null;
        }
    }

    private void showalert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
