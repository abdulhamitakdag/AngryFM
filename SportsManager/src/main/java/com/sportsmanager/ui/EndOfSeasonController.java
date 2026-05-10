package com.sportsmanager.ui;

import com.sportsmanager.core.gamesession.GameController;
import com.sportsmanager.core.model.AbstractTeam;
import com.sportsmanager.core.model.TeamInLeagueTable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
        seasonTitleLabel.setText(gc.getLeague().getName() + " — Season Over");

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
        gc.startNewSeason();

        URL url = getClass().getResource("/DashboardView.fxml");
        if (url == null) { System.out.println("DashboardView.fxml not found!"); return; }
        Parent root = FXMLLoader.load(url);
        App.mainstage.setScene(new Scene(root));
    }

    @FXML
    public void mainMenuButton() throws IOException {
        URL url = getClass().getResource("/MainMenuView.fxml");
        if (url == null) { System.out.println("MainMenuView.fxml not found!"); return; }
        Parent root = FXMLLoader.load(url);
        App.mainstage.setScene(new Scene(root));
    }
}
