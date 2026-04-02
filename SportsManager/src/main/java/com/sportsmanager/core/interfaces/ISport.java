package com.sportsmanager.core.interfaces;

import com.sportsmanager.core.model.AbstractPlayerAttributes;

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
}
