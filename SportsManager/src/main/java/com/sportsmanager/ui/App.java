package com.sportsmanager.ui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception{

        java.net.URL fxmlLocation =getClass().getResource("/MainMenuView.fxml");
        if (fxmlLocation==null){
            throw  new IllegalStateException("fxml file has not been found");
        }
        Parent root = FXMLLoader.load(fxmlLocation);
        Scene scene =new Scene(root);
        stage.setTitle("ANGRY BIRDS");
        java.io.InputStream iconstream =getClass().getResourceAsStream("/icon.png");
        if (iconstream!=null){
            Image icon =new Image(iconstream);
            stage.getIcons().add((icon));
        } else{
            System.out.println("Icon file not found! check src/main/resources/icon.png");
        }

        stage.setHeight(720);
        stage.setWidth(900);
        stage.setMinHeight(300);
        stage.setMinWidth(600);
        // stage.setFullScreen(true); this might be optinal
       // stage.setFullScreenExitHint("If the full size is not convinient,\nPress escape");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
