package com.sportsmanager.ui;

import com.sportsmanager.core.model.AbstractPlayer;
import com.sportsmanager.core.model.AbstractPlayerAttributes;
import com.sportsmanager.sport.football.FootballAttributes;
import com.sportsmanager.sport.basketball.BasketballAttributes;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PlayerAttributesController {

    @FXML private Label playerDetailsLabel;
    @FXML private VBox attributesContainer;

    public void setPlayer(AbstractPlayer player) {
        AbstractPlayerAttributes a = player.getAttributes();
        attributesContainer.getChildren().clear();

        if (a == null) {
            attributesContainer.getChildren().add(new Text("Attributes not available."));
            return;
        }
        playerDetailsLabel.setText(player.getName() + " (Age: " + player.getAge() + ", Pos: " + player.getPosition() + ") " + "OVR: " + a.getOverallRating());

        if (a instanceof FootballAttributes) {
            FootballAttributes fa = (FootballAttributes) a;

            if (player.getPosition().equals("GK")) {
                addGKAttributes(fa);
            } else {
                addPlayerAttributes(fa);
            }
        } else if (a instanceof BasketballAttributes) {
            BasketballAttributes ba = (BasketballAttributes) a;
            addPlayerAttributes(ba);
        }
    }

    private void addPlayerAttributes(FootballAttributes fa) {
        addAttributeRow("PAC (Pace)", fa.getPace());
        addAttributeRow("SHO (Shooting)", fa.getShooting());
        addAttributeRow("PAS (Passing)", fa.getPassing());
        addAttributeRow("DEF (Defending)", fa.getDefending());
        addAttributeRow("PHY (Physical)", fa.getPhysical());
    }

    private void addGKAttributes(FootballAttributes fa) {
        addAttributeRow("REF (Reflexes)", fa.getReflexes());
        addAttributeRow("POS (Positioning)", fa.getPositioning());
        addAttributeRow("DIV (Diving)", fa.getDiving());
        addAttributeRow("HAN (Handling)", fa.getHandling());
    }

    private void addPlayerAttributes(BasketballAttributes ba) {
        addAttributeRow("SHO (Shooting)", ba.getShooting());
        addAttributeRow("PLY (Playmaking)", ba.getPlaymaking());
        addAttributeRow("DEF (Defending)", ba.getDefending());
        addAttributeRow("RBD (Rebounding)", ba.getRebounding());
        addAttributeRow("PHY (Physical)", ba.getPhysical());
    }

    private void addAttributeRow(String name, double value) {
        Text attributeText = new Text(String.format("- %s: %.1f", name, value));
        attributesContainer.getChildren().add(attributeText);
    }

    @FXML
    public void okButtonHandler() {
        Stage stage = (Stage) playerDetailsLabel.getScene().getWindow();
        stage.close();
    }
}