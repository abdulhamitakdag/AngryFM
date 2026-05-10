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
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    public void initialize() {
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


        List<String> sortOrder = java.util.Arrays.asList(
                "GK", "LB", "CB", "RB", "CDM", "CM", "CAM", "LW", "RW", "ST",
                "PG", "SG", "SF", "PF", "C"
        );

        poscol.setComparator((pos1, pos2) -> {
            int index1 = sortOrder.indexOf(pos1);
            int index2 = sortOrder.indexOf(pos2);

            if (index1 == -1) index1 = 999;
            if (index2 == -1) index2 = 999;

            return Integer.compare(index1, index2);
        });

        shirtcol.setComparator((shirtStr1, shirtStr2) -> {

            String number1 = shirtStr1.replace("#", "").trim();
            String number2 = shirtStr2.replace("#", "").trim();

            try {
                int val1 = Integer.parseInt(number1);
                int val2 = Integer.parseInt(number2);

                return Integer.compare(val1, val2);
            } catch (NumberFormatException e) {
                return shirtStr1.compareTo(shirtStr2);
            }
        });


        // sakat oyuncu satırlarını kırmızıya boyuyoruz, fit olanlar default kalıyor
        // double clickte attributes gösteren bi pencere eklendi.
        squadtable.setRowFactory(tv -> {
            TableRow<AbstractPlayer> row = new TableRow<AbstractPlayer>() {
                @Override
                protected void updateItem(AbstractPlayer p, boolean empty) {
                    super.updateItem(p, empty);
                    if (p == null || empty) {
                        setStyle("");
                    } else if (p.isInjured()) {
                        setStyle("-fx-background-color: #ffd0d0;");
                    } else {
                        setStyle("");
                    }
                }
            };

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    AbstractPlayer rowData = row.getItem();
                    showPlayerAttributesWindow(rowData);
                }
            });

            return row;
        });

        List<AbstractPlayer> sorted = new ArrayList<>(team.getSquad());
        sorted.sort((a, b) -> Integer.compare(ovrof(b), ovrof(a)));
        squadtable.setItems(FXCollections.observableArrayList(sorted));
    }




    private void showPlayerAttributesWindow(AbstractPlayer player) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PlayerAttributesView.fxml"));
            Parent root = loader.load();

            PlayerAttributesController controller = loader.getController();
            controller.setPlayer(player);

            Stage stage = new Stage();
            stage.setTitle("Player Attributes - " + player.getName());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("PlayerAttributesView.fxml could not be found or loaded!");
        }
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
