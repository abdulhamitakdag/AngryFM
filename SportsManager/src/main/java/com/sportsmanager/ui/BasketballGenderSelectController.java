package com.sportsmanager.ui;

import com.sportsmanager.core.gamesession.GameController;
import com.sportsmanager.core.model.Gender;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;

public class BasketballGenderSelectController {
    @FXML
    public void malebutton() throws IOException {
        GameController.startNew("basketball", Gender.MALE, 16);
        URL teamurl = getClass().getResource("/TeamSelectView.fxml");
        if (teamurl == null) {
            System.out.println("TeamSelectView.fxml not found!");
            return;
        }
        Parent teamroot = FXMLLoader.load(teamurl);
        Scene teamscene = new Scene(teamroot);
        App.mainstage.setScene(teamscene);
    }

    @FXML
    public void femalebutton() throws IOException {
        GameController.startNew("basketball", Gender.FEMALE, 16);
        URL teamurl = getClass().getResource("/TeamSelectView.fxml");
        if (teamurl == null) {
            System.out.println("TeamSelectView.fxml not found!");
            return;
        }
        Parent teamroot = FXMLLoader.load(teamurl);
        Scene teamscene = new Scene(teamroot);
        App.mainstage.setScene(teamscene);
    }

    @FXML
    public void returnbutton() throws IOException {
        URL newgameurl = getClass().getResource("/NewGameView.fxml");
        if (newgameurl == null) {
            System.out.println("NewGameView.fxml not found!");
            return;
        }
        Parent newgameroot = FXMLLoader.load(newgameurl);
        Scene newgamescene = new Scene(newgameroot);
        App.mainstage.setScene(newgamescene);
    }

    @FXML
    public void exitbutton() {
        System.out.println("Game has been closed");
        System.exit(0);
    }
}
