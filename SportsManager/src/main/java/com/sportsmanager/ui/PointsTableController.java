package com.sportsmanager.ui;

import com.sportsmanager.core.gamesession.GameController;
import com.sportsmanager.core.model.AbstractTeam;
import com.sportsmanager.core.model.TeamInLeagueTable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class PointsTableController {

    @FXML private Label leaguenamelabel;
    @FXML private Label weeklabel;
    @FXML private TableView<TeamInLeagueTable> standingstable;
    @FXML private TableColumn<TeamInLeagueTable, String> rankcol;
    @FXML private TableColumn<TeamInLeagueTable, String> teamcol;
    @FXML private TableColumn<TeamInLeagueTable, String> pcol;
    @FXML private TableColumn<TeamInLeagueTable, String> wcol;
    @FXML private TableColumn<TeamInLeagueTable, String> dcol;
    @FXML private TableColumn<TeamInLeagueTable, String> lcol;
    @FXML private TableColumn<TeamInLeagueTable, String> gfcol;
    @FXML private TableColumn<TeamInLeagueTable, String> gacol;
    @FXML private TableColumn<TeamInLeagueTable, String> gdcol;
    @FXML private TableColumn<TeamInLeagueTable, String> ptscol;

    @FXML
    public void initialize(){
        GameController gc = GameController.getInstance();
        if (gc == null) return;

        leaguenamelabel.setText(gc.getLeague().getName());
        weeklabel.setText("Week " + gc.getLeague().getCurrentWeek());

        List<TeamInLeagueTable> standings = gc.getLeague().getStandings();

        // standings indeksini yakaladığımız için rank sütunu için satır indeksi gerek;
        // setItems sonrası getIndex ile alacağız
        rankcol.setCellValueFactory(c -> {
            int idx = standings.indexOf(c.getValue());
            return new SimpleStringProperty(String.valueOf(idx + 1));
        });
        teamcol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTeam().getName()));
        pcol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(played(c.getValue()))));
        wcol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getWins())));
        dcol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getDraws())));
        lcol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getLosses())));
        gfcol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getGoalsScored())));
        gacol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getGoalsConceded())));
        gdcol.setCellValueFactory(c -> new SimpleStringProperty(formatdiff(c.getValue().getGoalDifference())));
        ptscol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getPoints())));

        // kullanıcının takımını highlight ediyoruz
        AbstractTeam userTeam = gc.getUserTeam();
        standingstable.setRowFactory(tv -> new TableRow<TeamInLeagueTable>(){
            @Override
            protected void updateItem(TeamInLeagueTable row, boolean empty){
                super.updateItem(row, empty);
                if (row == null || empty){
                    setStyle("");
                } else if (userTeam != null && row.getTeam().equals(userTeam)){
                    setStyle("-fx-background-color: #d0f0ff; -fx-font-weight: bold;");
                } else {
                    setStyle("");
                }
            }
        });

        standingstable.setItems(FXCollections.observableArrayList(standings));
    }

    private int played(TeamInLeagueTable t){
        return t.getWins() + t.getDraws() + t.getLosses();
    }

    private String formatdiff(int diff){
        return diff > 0 ? "+" + diff : String.valueOf(diff);
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
}
