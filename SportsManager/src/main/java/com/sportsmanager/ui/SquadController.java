package com.sportsmanager.ui;

import com.sportsmanager.core.gamesession.GameController;
import com.sportsmanager.core.model.AbstractPlayer;
import com.sportsmanager.core.model.AbstractPlayerAttributes;
import com.sportsmanager.core.model.AbstractTeam;
import com.sportsmanager.core.model.Injury;
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

public class SquadController {

    @FXML private Label teamnamelabel;
    @FXML private Label squadcountlabel;
    @FXML private TableView<AbstractPlayer> squadtable;
    @FXML private TableColumn<AbstractPlayer, String> shirtcol;
    @FXML private TableColumn<AbstractPlayer, String> namecol;
    @FXML private TableColumn<AbstractPlayer, String> agecol;
    @FXML private TableColumn<AbstractPlayer, String> poscol;
    @FXML private TableColumn<AbstractPlayer, String> ovrcol;
    @FXML private TableColumn<AbstractPlayer, String> statuscol;

    @FXML
    public void initialize(){
        GameController gc = GameController.getInstance();
        if (gc == null) return;
        AbstractTeam team = gc.getUserTeam();
        if (team == null) return;

        teamnamelabel.setText(team.getName());
        squadcountlabel.setText("Squad: " + team.getCurrentSquadSize() + "/" + team.getMaxSquadSize());

        shirtcol.setCellValueFactory(c -> new SimpleStringProperty("#" + c.getValue().getShirtNumber()));
        namecol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        agecol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getAge())));
        poscol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPosition()));
        ovrcol.setCellValueFactory(c -> {
            AbstractPlayerAttributes a = c.getValue().getAttributes();
            return new SimpleStringProperty(a == null ? "-" : String.valueOf(a.getOverallRating()));
        });
        statuscol.setCellValueFactory(c -> new SimpleStringProperty(formatstatus(c.getValue())));

        // sakat oyuncu satırlarını kırmızıya boyuyoruz, fit olanlar default kalıyor
        squadtable.setRowFactory(tv -> new TableRow<AbstractPlayer>(){
            @Override
            protected void updateItem(AbstractPlayer p, boolean empty){
                super.updateItem(p, empty);
                if (p == null || empty){
                    setStyle("");
                } else if (p.isInjured()){
                    setStyle("-fx-background-color: #ffd0d0;");
                } else {
                    setStyle("");
                }
            }
        });

        // OVR'ye göre yüksekten düşüğe sırala
        List<AbstractPlayer> sorted = new ArrayList<>(team.getSquad());
        sorted.sort((a, b) -> Integer.compare(ovrof(b), ovrof(a)));
        squadtable.setItems(FXCollections.observableArrayList(sorted));
    }

    private String formatstatus(AbstractPlayer p){
        if (!p.isInjured()) return "Fit";
        Injury inj = p.getInjury();
        return "Out — " + inj.getSeverity() + " (" + inj.getGamesRemaining() + "w)";
    }

    private int ovrof(AbstractPlayer p){
        AbstractPlayerAttributes a = p.getAttributes();
        return a == null ? 0 : a.getOverallRating();
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
