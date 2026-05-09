package com.sportsmanager.ui;

import com.sportsmanager.core.gamesession.GameController;
import com.sportsmanager.core.model.AbstractCoach;
import com.sportsmanager.core.model.AbstractTeam;
import com.sportsmanager.core.model.CoachSpecialty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;

import java.io.IOException;
import java.net.URL;

public class CoachController {

    @FXML private Label teamnamelabel;
    @FXML private Label activelabel;
    @FXML private ListView<AbstractCoach> coachlist;
    @FXML private Label namelabel;
    @FXML private Label agelabel;
    @FXML private Label genderlabel;
    @FXML private Label specialtylabel;
    @FXML private Label levellabel;
    @FXML private Label modlabel;
    @FXML private Label specialtydesclabel;

    @FXML
    public void initialize(){
        GameController gc = GameController.getInstance();
        if (gc == null) return;
        AbstractTeam team = gc.getUserTeam();
        if (team == null) return;

        teamnamelabel.setText(team.getName());

        ObservableList<AbstractCoach> items = FXCollections.observableArrayList(team.getCoaches());
        coachlist.setItems(items);

        coachlist.setCellFactory(lv -> new ListCell<AbstractCoach>(){
            @Override
            protected void updateItem(AbstractCoach c, boolean empty){
                super.updateItem(c, empty);
                if (c == null || empty){
                    setText(null);
                    setStyle("");
                    setTooltip(null);
                } else {
                    String label = c.getName() + " — " + c.getSpecialty() + " (Lv " + c.getCoachingLevel() + ")";
                    AbstractCoach active = team.getActiveCoach();
                    if (active != null && active.equals(c)){
                        label = "★ " + label;
                        setStyle("-fx-background-color: #d0f0ff; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                    setText(label);
                    setTooltip(new Tooltip(specialtydescription(c.getSpecialty())));
                }
            }
        });

        coachlist.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) showdetails(newSel);
        });

        AbstractCoach active = team.getActiveCoach();
        if (active != null){
            coachlist.getSelectionModel().select(active);
        } else if (!team.getCoaches().isEmpty()){
            coachlist.getSelectionModel().selectFirst();
        }
        refreshactivelabel();
    }

    private void showdetails(AbstractCoach c){
        namelabel.setText(c.getName());
        agelabel.setText("Age: " + c.getAge());
        genderlabel.setText("Gender: " + c.getGender().name());
        specialtylabel.setText("Specialty: " + c.getSpecialty().name());
        levellabel.setText("Level: " + c.getCoachingLevel());
        double mod = c.getTrainingEffectiveness() * c.specialtyMultiplier();
        modlabel.setText(String.format("Train Multiplier: %.2fx", mod));
        specialtydesclabel.setText(specialtydescription(c.getSpecialty()));
    }

    private void refreshactivelabel(){
        GameController gc = GameController.getInstance();
        if (gc == null) return;
        AbstractTeam team = gc.getUserTeam();
        if (team == null) return;
        AbstractCoach active = team.getActiveCoach();
        activelabel.setText("Active Coach: " + (active != null ? active.getName() + " (" + active.getSpecialty() + ")" : "-"));
    }

    private String specialtydescription(CoachSpecialty s){
        switch (s){
            case ATTACKING:
                return "Boosts attackers (ST/LW/RW/CAM) shooting, pace and passing in training. "
                     + "Adds +2% offensive modifier per level in matches.";
            case DEFENDING:
                return "Boosts defenders (CB/LB/RB/CDM) defending and physical in training. "
                     + "Adds +2% defensive modifier per level in matches (rivals score less).";
            case FITNESS:
                return "Boosts physical and pace for every player in training. "
                     + "Reduces match injury risk by 10% per level.";
            case GENERAL:
                return "Balanced training, no positional bias. "
                     + "Adds +1% to both offensive and defensive modifiers per level in matches.";
            default:
                return "-";
        }
    }

    @FXML
    public void setactivebutton(){
        AbstractCoach selected = coachlist.getSelectionModel().getSelectedItem();
        if (selected == null){
            showalert("Coach", "No coach selected!");
            return;
        }
        GameController gc = GameController.getInstance();
        if (gc == null) return;
        AbstractTeam team = gc.getUserTeam();
        if (team == null) return;

        team.setActiveCoach(selected);
        refreshactivelabel();
        coachlist.refresh();
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
