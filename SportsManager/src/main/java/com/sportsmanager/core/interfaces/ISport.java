package com.sportsmanager.core.interfaces;

import com.sportsmanager.core.model.AbstractPlayerAttributes;

import java.util.List;

/**
 * Sisteme eklenecek tüm spor branşlarının temel kurallarını belirler.
 */
public interface ISport {
    String getSportId();
    int getPlayersOnField();
    int getWinPoints();
    boolean allowsDraw();

    /**
     * Bu spor branşına ait maç motorunu üretir (Factory Method).
     */
    IMatchEngine createMatchEngine();

    /**
     * Belirtilen pozisyon için rastgele yetenekler (attributes) üretir.
     */
    AbstractPlayerAttributes generateRandomAttributes(String position);

    /**
     * Bu spor branşına ait geçerli saha pozisyonlarının listesini döndürür.
     */
    List<String> getValidPositions();
}

