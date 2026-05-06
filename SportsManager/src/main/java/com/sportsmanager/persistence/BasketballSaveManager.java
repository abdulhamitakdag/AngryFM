package com.sportsmanager.persistence;

import com.sportsmanager.core.model.*;
import com.sportsmanager.sport.basketball.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

// basketbola özel save/load — FootballSaveManager ile aynı yapı
public class BasketballSaveManager extends AbstractSaveManager {

    public BasketballSaveManager() {
        super();
    }

    public BasketballSaveManager(Path saveDirectory) {
        super(saveDirectory);
    }

    @Override
    public String getSportType() {
        return "basketball";
    }

    @Override
    protected AbstractLeague createLeague(String name) {
        return new BasketballLeague(name);
    }

    @Override
    protected AbstractTeam createTeam(String name) {
        return new BasketballTeam(name);
    }

    // BasketballAttributes alanlarını PlayerData'ya yazar
    // shooting ve physical GameState'te zaten var
    // defense → defending alanını paylaşıyor
    // playmaking ve rebounding yeni eklenen basketbol alanları
    @Override
    protected GameState.PlayerData convertPlayer(AbstractPlayer player) {
        GameState.PlayerData pd = new GameState.PlayerData();
        pd.setName(player.getName());
        pd.setAge(player.getAge());
        pd.setGender(player.getGender().name());
        pd.setShirtNumber(player.getShirtNumber());
        pd.setPosition(player.getPosition());

        if (player.getAttributes() instanceof BasketballAttributes ba) {
            pd.setShooting(ba.getShooting());
            pd.setPlaymaking(ba.getPlaymaking());
            pd.setDefending(ba.getDefense());
            pd.setRebounding(ba.getRebounding());
            pd.setPhysical(ba.getPhysical());
        }

        if (player.isInjured()) {
            pd.setInjurySeverity(player.getInjury().getSeverity().name());
            pd.setInjuryGamesRemaining(player.getInjury().getGamesRemaining());
        }

        return pd;
    }

    @Override
    protected AbstractPlayer restorePlayer(GameState.PlayerData pd) {
        BasketballPositions pos = BasketballPositions.valueOf(pd.getPosition());
        BasketballAttributes attrs = new BasketballAttributes(
                pos,
                safe(pd.getShooting()),
                safe(pd.getPlaymaking()),
                safe(pd.getDefending()),   // defense alanı
                safe(pd.getRebounding()),
                safe(pd.getPhysical()));

        BasketballPlayer player = new BasketballPlayer(
                pd.getName(), pd.getAge(),
                Gender.valueOf(pd.getGender()),
                pd.getShirtNumber(), pos, attrs);

        if (pd.getInjurySeverity() != null) {
            Injury.Severity severity = Injury.Severity.valueOf(pd.getInjurySeverity());
            player.setInjury(new Injury(severity, pd.getInjuryGamesRemaining()));
        }

        return player;
    }

    @Override
    protected AbstractCoach restoreCoach(GameState.CoachData cd) {
        return new BasketballCoach(
                cd.getName(), cd.getAge(),
                Gender.valueOf(cd.getGender()),
                CoachSpecialty.valueOf(cd.getSpecialty()),
                cd.getCoachingLevel());
    }

    // taktik adından fabrika metodunu seçiyor
    // requiredPositions basketbolda kullanılmıyor — formasyon adı yeterli
    @Override
    protected AbstractTactic restoreTactic(GameState.TacticData td) {
        BasketballTactic tactic;
        switch (td.getName()) {
            case "2-1-2": tactic = BasketballTactic.createZone();     break;
            case "1-3-1": tactic = BasketballTactic.createTrap();     break;
            case "2-3":   tactic = BasketballTactic.createBig();      break;
            default:      tactic = BasketballTactic.createStandard(); break;
        }
        tactic.setAttackingWeight(td.getAttackingWeight());
        tactic.setPressureIntensity(td.getPressureIntensity());
        return tactic;
    }
}
