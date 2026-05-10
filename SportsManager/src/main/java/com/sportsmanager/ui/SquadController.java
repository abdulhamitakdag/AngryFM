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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SquadController {

    private static final List<String> POS_ORDER = Arrays.asList(
            "GK", "LB", "CB", "RB", "CDM", "CM", "LW", "CAM", "RW", "ST",
            "PG", "SG", "SF", "PF", "C"
    );

    @FXML private Label teamnamelabel;
    @FXML private Label squadcountlabel;
    @FXML private Label infolabel;

    @FXML private TableView<AbstractPlayer> startingtable;
    @FXML private TableColumn<AbstractPlayer, String> s_shirtcol;
    @FXML private TableColumn<AbstractPlayer, String> s_namecol;
    @FXML private TableColumn<AbstractPlayer, String> s_poscol;
    @FXML private TableColumn<AbstractPlayer, String> s_ovrcol;
    @FXML private TableColumn<AbstractPlayer, String> s_statuscol;

    @FXML private TableView<AbstractPlayer> benchtable;
    @FXML private TableColumn<AbstractPlayer, String> b_shirtcol;
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
        updateCounts();

        setupColumns(s_shirtcol, s_namecol, s_poscol, s_ovrcol, s_statuscol, startingtable);
        setupColumns(b_shirtcol, b_namecol, b_poscol, b_ovrcol, b_statuscol, benchtable);

        refresh();
    }

    private void setupColumns(TableColumn<AbstractPlayer, String> shirtCol,
                              TableColumn<AbstractPlayer, String> nameCol,
                              TableColumn<AbstractPlayer, String> posCol,
                              TableColumn<AbstractPlayer, String> ovrCol,
                              TableColumn<AbstractPlayer, String> statusCol,
                              TableView<AbstractPlayer> table) {

        shirtCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getShirtNumber())));
        shirtCol.setComparator((s1, s2) -> {
            try { return Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2)); }
            catch (NumberFormatException e) { return s1.compareTo(s2); }
        });

        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        nameCol.setCellFactory(col -> new TableCell<AbstractPlayer, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    AbstractPlayer p = getTableView().getItems().get(getIndex());
                    if (p != null && p.isInjured()) {
                        setStyle("-fx-text-fill: #cc0000; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        posCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPosition()));
        posCol.setComparator((pos1, pos2) -> {
            int index1 = POS_ORDER.indexOf(pos1.toUpperCase());
            int index2 = POS_ORDER.indexOf(pos2.toUpperCase());
            if (index1 == -1) index1 = 999;
            if (index2 == -1) index2 = 999;
            return Integer.compare(index1, index2);
        });

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
                    else if (p.isInjured()) setStyle("-fx-background-color: #ffb3b3;");
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
        List<AbstractPlayer> starting = team.getStartingLineup();
        startingtable.setItems(FXCollections.observableArrayList(starting));

        // Sakat olanlar dahil starting dışındaki tüm kadro
        List<AbstractPlayer> rest = new ArrayList<>();
        for (AbstractPlayer p : team.getSquad()) {
            if (!starting.contains(p)) rest.add(p);
        }
        benchtable.setItems(FXCollections.observableArrayList(rest));
        updateCounts();
    }

    private void updateCounts() {
        long injured = team.getSquad().stream().filter(AbstractPlayer::isInjured).count();
        squadcountlabel.setText("Squad: " + team.getCurrentSquadSize() + "/" + team.getMaxSquadSize()
                + "  |  Starting: " + team.getStartingLineup().size()
                + "  |  Bench: " + team.getBench().size()
                + (injured > 0 ? "  |  Injured: " + injured : ""));
    }


    @FXML
    public void swapbutton() {
        AbstractPlayer out = startingtable.getSelectionModel().getSelectedItem();
        AbstractPlayer in  = benchtable.getSelectionModel().getSelectedItem();

        if (out == null && in != null && team.getStartingLineup().size() < playersOnField) {
            boolean ok = team.swapStartingWithBench(null, in);
            if (!ok) showAlert("Error", "The player could not be added, may be injured.");
            refresh();
            return;
        }

        if (out == null || in == null) {
            showAlert("Missing Selection", "\"Select one player from Starting and one from Bench.");
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