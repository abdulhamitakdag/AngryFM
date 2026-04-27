package com.sportsmanager.ui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception{

        Group root =new Group();
        Scene scene =new Scene(root,400,500, Color.AQUA);
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
        // stage.setFullScreen(true); this might be optinal
       // stage.setFullScreenExitHint("If the full size is not convinient,\nPress escape");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
