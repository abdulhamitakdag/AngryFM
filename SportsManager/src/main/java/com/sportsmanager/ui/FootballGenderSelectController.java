package com.sportsmanager.ui;

import com.sportsmanager.core.gamesession.GameController;
import com.sportsmanager.core.model.Gender;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;

public class FootballGenderSelectController {
    @FXML
    public void malebutton()throws IOException{
        Gender male=Gender.MALE;
        GameController starter=GameController.startNew("football",male,18);
        URL dashboardurl=getClass().getResource("/DashboardView.fxml");
        if (dashboardurl==null){
            System.out.println("DashboardView.fxml not found!");
            return;
        }
        Parent dashboardroot=FXMLLoader.load(dashboardurl);
        Scene dashboardscene= new Scene(dashboardroot);
        App.mainstage.setScene(dashboardscene);
    } @FXML
    public void femalebutton()throws IOException{
        Gender female=Gender.FEMALE;
        GameController.startNew("football",female,18);
        URL dashboardurl=getClass().getResource("/DashboardView.fxml");
        if (dashboardurl==null){
            System.out.println("DashboardView.fxml not found!");
            return;
        }
        Parent dashboardroot=FXMLLoader.load(dashboardurl);
        Scene dashboardscene= new Scene(dashboardroot);
        App.mainstage.setScene(dashboardscene);

    }

    public void returnbutton() throws IOException {
        URL newgameurl = getClass().getResource("/NewGameView.fxml");
        if (newgameurl==null){
            System.out.println("NewGameView.fxml not found!");
            return;
        }
        Parent newgameroot = FXMLLoader.load(newgameurl);
        Scene newgamescene=new Scene(newgameroot);

        App.mainstage.setScene(newgamescene);
    }
    @FXML
    public void exitbutton(){
        System.out.println("Game has been closed");
        System.exit(0);
    }
}
