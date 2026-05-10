package com.sportsmanager.ui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
public class MainMenuController {
    @FXML
    public void exitbutton(){
        System.out.println("Game has been closed");
        System.exit(0);
    }
 @FXML
    public void newgamebutton()throws IOException{
        URL newgameurl = getClass().getResource("/NewGameView.fxml");
        if (newgameurl==null){
            System.out.println("NewGameView.fxml not found!");
            return;
        }
        Parent  newgameroot = FXMLLoader.load(newgameurl);
        Scene newgamescene=new Scene(newgameroot);

     App.mainstage.setScene(newgamescene);

    }
    @FXML
    public void resumebutton()throws IOException{
        URL resumeurl = getClass().getResource("/ResumeView.fxml");
        if (resumeurl==null){
            System.out.println("ResumeView.fxml not found!");
            return;
        }
        Parent  resumeroot = FXMLLoader.load(resumeurl);
        Scene resumescene=new Scene(resumeroot);

        App.mainstage.setScene(resumescene);
    }


}
