package com.sportsmanager.ui;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;
public class NewGameController {


@FXML
    public void returnbutton() throws IOException{
        URL maingameurl = getClass().getResource("/MainMenuView.fxml");
        if (maingameurl==null){
            System.out.println("MainMenuView.fxml not found!");
            return;
        }
        Parent  maingameroot = FXMLLoader.load(maingameurl);
        Scene maingamescene=new Scene(maingameroot);

        App.mainstage.setScene(maingamescene);
    }
    @FXML
    public void exitbutton(){
        System.out.println("Game has been closed");
        System.exit(0);
    }
    @FXML
    public void footballbutton()throws IOException{
    URL footballurl= getClass().getResource("/FootballGenderSelect.fxml");
    if (footballurl==null) {
        System.out.println("FootballGenderSelect.fxml not found!");
        return;
    }
    Parent footballroot= FXMLLoader.load(footballurl);
    Scene footballscene =new Scene(footballroot);
    App.mainstage.setScene(footballscene);
    }
    }






