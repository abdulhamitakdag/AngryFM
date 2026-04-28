package com.sportsmanager.ui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
public class UIController {
    @FXML
    public void exitbutton(){
        System.out.println("Game has been closed");
        System.exit(0);
    }
}
