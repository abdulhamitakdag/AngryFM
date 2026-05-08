package com.sportsmanager.ui;

import com.sportsmanager.core.gamesession.GameController;
import com.sportsmanager.core.model.AbstractTeam;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;

public class TeamSelectController {

    @FXML
    private ListView<String> teamslist;

    @FXML
    public void initialize(){
        GameController gc = GameController.getInstance();
        if (gc == null) return;
        ObservableList<String> items = FXCollections.observableArrayList();
        for (AbstractTeam t : gc.getTeams()) {
            items.add(t.getName());
        }
        teamslist.setItems(items);
    }

    @FXML
    public void continuebutton() throws IOException{
        String selected = teamslist.getSelectionModel().getSelectedItem();
        if (selected==null){
            shownoteamalert();
            return;
        }
        GameController gc = GameController.getInstance();
        if (gc == null) return;
        for (AbstractTeam t : gc.getTeams()) {
            if (t.getName().equals(selected)) {
                gc.setUserTeam(t);
                break;
            }
        }
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
    public void returnbutton() throws IOException{
        URL genderurl = getClass().getResource("/FootballGenderSelect.fxml");
        if (genderurl==null){
            System.out.println("FootballGenderSelect.fxml not found!");
            return;
        }
        Parent genderroot = FXMLLoader.load(genderurl);
        Scene genderscene = new Scene(genderroot);
        App.mainstage.setScene(genderscene);
    }

    @FXML
    public void exitbutton(){
        System.out.println("Game has been closed");
        System.exit(0);
    }

    private void shownoteamalert(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("New Game");
        alert.setHeaderText(null);
        alert.setContentText("No team selected!");
        alert.showAndWait();
    }
}
