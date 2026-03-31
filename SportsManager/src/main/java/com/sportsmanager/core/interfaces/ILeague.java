package com.sportsmanager.core.interfaces;

import com.sportsmanager.core.model.AbstractPlayerAttributes;
import com.sportsmanager.core.model.TeamInLeagueTable;
import com.sportsmanager.core.model.MatchResult;
import com.sportsmanager.core.model.AbstractTeam;

import java.util.List;

/**
 * Antrenman yapılabilen (gelişime açık) varlıkların yeteneklerini tanımlar.
 */
public interface ITrainable {
    /**
     * Varlığa antrenman uygular.
     * @param intensity Antrenman yoğunluğu (0.0 ile 1.0 arası)
     */
    void train(double intensity);

    /**
     * @return Antrenman verimliliği katsayısı
     */
    double getTrainingEffectiveness();
}

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
}

/**
 * Lig yönetimi için gerekli sözleşmeyi sağlar.
 */
public interface ILeague {
    List<TeamInLeagueTable> getStandings();
    void recordResult(MatchResult result);
    void generateFixtures(List<AbstractTeam> teams);
    boolean isSeasonOver();
}

/**
 * Maçların simülasyon mantığını barındıran motor arayüzü.
 */
public interface IMatchEngine {
    // Not: PeriodResult, AbstractTeam ve Injury sınıfları B kişisi ve diğer tasklardan gelecek
    Object simulatePeriod(AbstractTeam home, AbstractTeam away, Object tacticModifier);
    List<Object> determineInjuries(AbstractTeam home, AbstractTeam away);
    double calculatePlayerRating(Object player);
}