package com.sportsmanager.ui;

import com.sportsmanager.core.gamesession.GameController;
import com.sportsmanager.persistence.FootballSaveManager;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResumeMenuController {

    @FXML
    private ListView<String> saveslist;

    // maps display label → actual save name
    private final Map<String, String> displayToSaveName = new LinkedHashMap<>();

    @FXML
    public void initialize(){
        refreshlist();
    }

    private void refreshlist(){
        displayToSaveName.clear();
        try{
            FootballSaveManager savemanager = new FootballSaveManager();
            List<String> saves = savemanager.listSaves();
            for (String saveName : saves) {
                String sportType = savemanager.getSportTypeForSave(saveName);
                String label = formatSportLabel(sportType);
                String display = saveName + "  [" + label + "]";
                displayToSaveName.put(display, saveName);
            }
            saveslist.setItems(FXCollections.observableArrayList(displayToSaveName.keySet()));
        } catch (IOException e){
            System.out.println("Could not list saves: " + e.getMessage());
        }
    }

    private String formatSportLabel(String sportType) {
        if ("basketball".equalsIgnoreCase(sportType)) return "Basketball";
        if ("football".equalsIgnoreCase(sportType))   return "Football";
        return sportType;
    }

    @FXML
    public void continuebutton() throws IOException{
        String selected = saveslist.getSelectionModel().getSelectedItem();
        if (selected==null){
            shownosavealert();
            return;
        }
        String saveName = displayToSaveName.getOrDefault(selected, selected);
        try{
            GameController.loadGame(saveName);
        } catch (IOException e){
            System.out.println("Could not load save: " + e.getMessage());
            return;
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
    public void deletebutton(){
        String selected = saveslist.getSelectionModel().getSelectedItem();
        if (selected==null){
            shownosavealert();
            return;
        }
        String saveName = displayToSaveName.getOrDefault(selected, selected);
        try{
            FootballSaveManager savemanager = new FootballSaveManager();
            savemanager.delete(saveName);
        } catch (IOException e){
            System.out.println("Could not delete save: " + e.getMessage());
            return;
        }
        refreshlist();
    }

    @FXML
    public void returnbutton() throws IOException{
        URL maingameurl = getClass().getResource("/MainMenuView.fxml");
        if (maingameurl==null){
            System.out.println("MainMenuView.fxml not found!");
            return;
        }
        Parent maingameroot = FXMLLoader.load(maingameurl);
        Scene maingamescene = new Scene(maingameroot);

        App.mainstage.setScene(maingamescene);
    }

    @FXML
    public void exitbutton(){
        System.out.println("Game has been closed");
        System.exit(0);
    }

    private void shownosavealert(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Resume");
        alert.setHeaderText(null);
        alert.setContentText("No save selected!");
        alert.showAndWait();
    }
}
