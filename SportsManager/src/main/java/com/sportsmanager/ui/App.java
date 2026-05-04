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
    public static Stage mainstage;
    @Override
    public void start(Stage stage) throws Exception{
        mainstage=stage;
        java.net.URL fxmlLocation =getClass().getResource("/MainMenuView.fxml");
        if (fxmlLocation==null){
            throw  new IllegalStateException("fxml file has not been found");
        }
        Parent root = FXMLLoader.load(fxmlLocation);
        Scene scene =new Scene(root);
        mainstage.setTitle("ANGRY BIRDS");
        java.io.InputStream iconstream =getClass().getResourceAsStream("/icon.png");
        if (iconstream!=null){
            Image icon =new Image(iconstream);
            mainstage.getIcons().add((icon));
        } else{
            System.out.println("Icon file not found! check src/main/resources/icon.png");
        }

        mainstage.setHeight(720);
        mainstage.setWidth(900);
        mainstage.setMinHeight(300);
        mainstage.setMinWidth(600);
        // stage.setFullScreen(true); this might be optinal
       // stage.setFullScreenExitHint("If the full size is not convinient,\nPress escape");
        mainstage.setScene(scene);
        mainstage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
