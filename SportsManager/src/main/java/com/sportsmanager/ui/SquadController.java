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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class SquadController {

    @FXML private Label teamnamelabel;
    @FXML private Label squadcountlabel;
    @FXML private Label infolabel;

    @FXML private TableView<AbstractPlayer> startingtable;
    @FXML private TableColumn<AbstractPlayer, String> s_namecol;
    @FXML private TableColumn<AbstractPlayer, String> s_poscol;
    @FXML private TableColumn<AbstractPlayer, String> s_ovrcol;
    @FXML private TableColumn<AbstractPlayer, String> s_statuscol;

    @FXML private TableView<AbstractPlayer> benchtable;
    @FXML private TableColumn<AbstractPlayer, String> b_namecol;
    @FXML private TableColumn<AbstractPlayer, String> b_poscol;
    @FXML private TableColumn<AbstractPlayer, String> b_ovrcol;
    @FXML private TableColumn<AbstractPlayer, String> b_statuscol;

    private AbstractTeam team;
    private int playersOnField;

    @FXML
    public void initialize() {
        GameController gc = GameController.getInstance();
        if (gc == null) return;
        team = gc.getUserTeam();
        if (team == null) return;
        playersOnField = gc.getSport().getPlayersOnField();

        teamnamelabel.setText(team.getName());
        squadcountlabel.setText("Squad: " + team.getCurrentSquadSize() + "/" + team.getMaxSquadSize()
                + "  |  Starting: " + team.getStartingLineup().size() + "  |  Bench: " + team.getBench().size());

        setupColumns(s_namecol, s_poscol, s_ovrcol, s_statuscol, startingtable);
        setupColumns(b_namecol, b_poscol, b_ovrcol, b_statuscol, benchtable);

        refresh();
    }

    private void setupColumns(TableColumn<AbstractPlayer, String> nameCol,
                               TableColumn<AbstractPlayer, String> posCol,
                               TableColumn<AbstractPlayer, String> ovrCol,
                               TableColumn<AbstractPlayer, String> statusCol,
                               TableView<AbstractPlayer> table) {
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        posCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPosition()));
        ovrCol.setCellValueFactory(c -> {
            AbstractPlayerAttributes a = c.getValue().getAttributes();
            return new SimpleStringProperty(a == null ? "-" : String.valueOf(a.getOverallRating()));
        });
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(formatStatus(c.getValue())));

        table.setRowFactory(tv -> {
            TableRow<AbstractPlayer> row = new TableRow<AbstractPlayer>() {
                @Override
                protected void updateItem(AbstractPlayer p, boolean empty) {
                    super.updateItem(p, empty);
                    if (p == null || empty) setStyle("");
                    else if (p.isInjured()) setStyle("-fx-background-color: #ffd0d0;");
                    else setStyle("");
                }
            };
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showPlayerAttributesWindow(row.getItem());
                }
            });
            return row;
        });
    }

    private void refresh() {
        if (team == null) return;
        startingtable.setItems(FXCollections.observableArrayList(team.getStartingLineup()));
        benchtable.setItems(FXCollections.observableArrayList(team.getBench()));
        squadcountlabel.setText("Squad: " + team.getCurrentSquadSize() + "/" + team.getMaxSquadSize()
                + "  |  Starting: " + team.getStartingLineup().size() + "  |  Bench: " + team.getBench().size());
    }

    @FXML
    public void swapbutton() {
        AbstractPlayer out = startingtable.getSelectionModel().getSelectedItem();
        AbstractPlayer in  = benchtable.getSelectionModel().getSelectedItem();
        if (out == null || in == null) {
            showAlert("Select players", "Select one player from Starting and one from Bench.");
            return;
        }
        boolean ok = team.swapStartingWithBench(out, in);
        if (!ok) {
            showAlert("Swap failed", "Could not swap — player may be injured or already starting.");
        }
        refresh();
    }

    @FXML
    public void autosetbutton() {
        team.autoSetLineup(playersOnField);
        refresh();
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
            System.out.println("PlayerAttributesView.fxml could not be loaded.");
        }
    }

    private String formatStatus(AbstractPlayer p) {
        if (!p.isInjured()) return "Fit";
        Injury inj = p.getInjury();
        return "Out - " + inj.getSeverity() + " (" + inj.getGamesRemaining() + "w)";
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void returnbutton() throws IOException {
        URL url = getClass().getResource("/DashboardView.fxml");
        if (url == null) { System.out.println("DashboardView.fxml not found!"); return; }
        App.mainstage.setScene(new Scene(FXMLLoader.load(url)));
    }

    @FXML
    public void exitbutton() {
        System.exit(0);
    }
}
