package com.sportsmanager.ui;

import com.sportsmanager.core.gamesession.GameController;
import com.sportsmanager.core.model.AbstractTeam;
import com.sportsmanager.core.model.Fixture;
import com.sportsmanager.core.model.MatchResult;
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
import java.util.ArrayList;
import java.util.List;

public class FixtureController {

    @FXML private Label leaguenamelabel;
    @FXML private Label weeklabel;
    @FXML private TableView<Fixture> fixturetable;
    @FXML private TableColumn<Fixture, String> hometeamcol;
    @FXML private TableColumn<Fixture, String> scorecol;
    @FXML private TableColumn<Fixture, String> awayteamcol;

    private int displayedweek;
    private int totalweeks;

    @FXML
    public void initialize(){
        GameController gc = GameController.getInstance();
        if (gc == null) return;

        leaguenamelabel.setText(gc.getLeague().getName());
        totalweeks = computeTotalWeeks(gc);
        displayedweek = gc.getLeague().getCurrentWeek();
        if (displayedweek > totalweeks) displayedweek = totalweeks;
        if (displayedweek < 1) displayedweek = 1;

        hometeamcol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHomeTeam().getName()));
        awayteamcol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAwayTeam().getName()));
        scorecol.setCellValueFactory(c -> new SimpleStringProperty(formatscore(c.getValue())));

        // kullanıcının takımının olduğu maçları highlight ediyoruz
        AbstractTeam userTeam = gc.getUserTeam();
        fixturetable.setRowFactory(tv -> new TableRow<Fixture>(){
            @Override
            protected void updateItem(Fixture f, boolean empty){
                super.updateItem(f, empty);
                if (f == null || empty){
                    setStyle("");
                } else if (userTeam != null && (f.getHomeTeam().equals(userTeam) || f.getAwayTeam().equals(userTeam))){
                    setStyle("-fx-background-color: #d0f0ff; -fx-font-weight: bold;");
                } else {
                    setStyle("");
                }
            }
        });

        loadweek();
    }

    private void loadweek(){
        GameController gc = GameController.getInstance();
        if (gc == null) return;
        weeklabel.setText("Week " + displayedweek + " / " + totalweeks);
        List<Fixture> fixtures = new ArrayList<>();
        for (Fixture f : gc.getLeague().getFixtures()){
            if (f.getWeek() == displayedweek){
                fixtures.add(f);
            }
        }
        fixturetable.setItems(FXCollections.observableArrayList(fixtures));
    }

    private String formatscore(Fixture f){
        if (!f.isPlayed() || f.getResult() == null) return "vs";
        MatchResult r = f.getResult();
        return r.getHomeScore() + " - " + r.getAwayScore();
    }

    // fikstürdeki en yüksek hafta numarası = sezonun toplam haftası
    private int computeTotalWeeks(GameController gc){
        int max = 0;
        for (Fixture f : gc.getLeague().getFixtures()){
            if (f.getWeek() > max) max = f.getWeek();
        }
        return max;
    }

    @FXML
    public void prevweekbutton(){
        if (displayedweek > 1){
            displayedweek--;
            loadweek();
        }
    }

    @FXML
    public void nextweekbutton(){
        if (displayedweek < totalweeks){
            displayedweek++;
            loadweek();
        }
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
