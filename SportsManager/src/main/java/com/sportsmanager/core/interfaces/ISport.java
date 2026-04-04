package com.sportsmanager.core.interfaces;

import com.sportsmanager.core.model.AbstractPlayer;
import com.sportsmanager.core.model.AbstractPlayerAttributes;
import com.sportsmanager.core.model.Gender;

import java.util.List;

// spor branşlarının temel kurallarını belirleyen arayüz
public interface ISport {
    String getSportId();
    int getPlayersOnField();
    int getWinPoints();
    boolean allowsDraw();

    IMatchEngine createMatchEngine(); // bu branşa ait maç motoru üretir


    AbstractPlayerAttributes generateRandomAttributes(String position); // pozisyona göre rastgele yetenek üretir

    List<String> getValidPositions(); // geçerli saha pozisyonları
    //(ben Selin) RandomGenerator'da player üretmek için buraya player parametreli metodcuk ekliyorum
    AbstractPlayer createPlayer(String name, int age, Gender gender,
                                int shirtNumber, String position,
                                AbstractPlayerAttributes attributes);
    //spesifik sporlar bu metodu implementler
}
