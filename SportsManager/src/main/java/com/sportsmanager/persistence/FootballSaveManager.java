package com.sportsmanager.persistence;

import com.sportsmanager.core.model.*;
import com.sportsmanager.sport.football.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

// futbola özel save/load işlemlerini barındırıyor
// yeni bi spor eklenince bunun gibi bi sınıf daha yazılacak
public class FootballSaveManager extends AbstractSaveManager {

    public FootballSaveManager() {
        super();
    }

    public FootballSaveManager(Path saveDirectory) {
        super(saveDirectory);
    }

    @Override
    public String getSportType() {
        return "football";
    }

    @Override
    protected AbstractLeague createLeague(String name) {
        return new FootballLeague(name);
    }

    @Override
    protected AbstractTeam createTeam(String name) {
        return new FootballTeam(name);
    }

    // oyuncuyu dto'ya çeviriyor
    // kaleci mi saha oyuncusu mu diye bakıp ona göre attributeları set ediyor
    @Override
    protected GameState.PlayerData convertPlayer(AbstractPlayer player) {
        GameState.PlayerData pd = new GameState.PlayerData();
        pd.setName(player.getName());
        pd.setAge(player.getAge());
        pd.setGender(player.getGender().name());
        pd.setShirtNumber(player.getShirtNumber());
        pd.setPosition(player.getPosition());

        if (player.getAttributes() instanceof FootballAttributes fa) {
            if (fa.getPosition() == FootballPositions.GK) {
                pd.setReflexes(fa.getReflexes());
                pd.setPositioning(fa.getPositioning());
                pd.setDiving(fa.getDiving());
                pd.setHandling(fa.getHandling());
            } else {
                pd.setPace(fa.getPace());
                pd.setShooting(fa.getShooting());
                pd.setPassing(fa.getPassing());
                pd.setDefending(fa.getDefending());
                pd.setPhysical(fa.getPhysical());
            }
        }

        // sakatlığı varsa onu da kaydediyoruz
        if (player.isInjured()) {
            pd.setInjurySeverity(player.getInjury().getSeverity().name());
            pd.setInjuryGamesRemaining(player.getInjury().getGamesRemaining());
        }

        return pd;
    }

    // dto'dan oyuncu oluşturuyor
    // pozisyona göre kaleci veya saha attributelarını seçiyor
    @Override
    protected AbstractPlayer restorePlayer(GameState.PlayerData pd) {
        FootballPositions pos = FootballPositions.valueOf(pd.getPosition());

        FootballAttributes attrs;
        if (pos == FootballPositions.GK) {
            attrs = new FootballAttributes(pos,
                    safe(pd.getReflexes()), safe(pd.getPositioning()),
                    safe(pd.getDiving()), safe(pd.getHandling()));
        } else {
            attrs = new FootballAttributes(pos,
                    safe(pd.getPace()), safe(pd.getShooting()),
                    safe(pd.getPassing()), safe(pd.getDefending()),
                    safe(pd.getPhysical()));
        }

        FootballPlayer player = new FootballPlayer(
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
        return new FootballCoach(
                cd.getName(), cd.getAge(),
                Gender.valueOf(cd.getGender()),
                CoachSpecialty.valueOf(cd.getSpecialty()),
                cd.getCoachingLevel());
    }

    // taktik bilgisini dto'ya çevirirken requiredPositions'ı da kaydediyoruz
    // üst sınıftaki genel dönüşümün üstüne ekliyoruz
    @Override
    protected GameState.TacticData convertTactic(AbstractTactic tactic) {
        GameState.TacticData td = super.convertTactic(tactic);
        if (tactic instanceof FootballTactic ft) {
            Map<String, Integer> posMap = new HashMap<>();
            for (Map.Entry<FootballPositions, Integer> e : ft.getRequiredPositions().entrySet()) {
                posMap.put(e.getKey().name(), e.getValue());
            }
            td.setRequiredPositions(posMap);
        }
        return td;
    }

    // kaydedilmiş pozisyon bilgisi varsa onu kullanıyor
    // yoksa eski yöntemle factory'den oluşturuyor
    @Override
    protected AbstractTactic restoreTactic(GameState.TacticData td) {
        if (td.getRequiredPositions() != null && !td.getRequiredPositions().isEmpty()) {
            Map<FootballPositions, Integer> posMap = new HashMap<>();
            for (Map.Entry<String, Integer> e : td.getRequiredPositions().entrySet()) {
                posMap.put(FootballPositions.valueOf(e.getKey()), e.getValue());
            }
            return new FootballTactic(td.getName(), td.getAttackingWeight(), td.getPressureIntensity(), posMap);
        }

        // eski save dosyaları için geriye dönük uyumluluk
        FootballTactic tactic;
        switch (td.getName()) {
            case "4-4-2":   tactic = FootballTactic.create442();  break;
            case "4-2-3-1": tactic = FootballTactic.create4231(); break;
            case "4-3-3":   tactic = FootballTactic.create433();  break;
            case "4-2-4":   tactic = FootballTactic.create424();  break;
            case "3-5-2":   tactic = FootballTactic.create352();  break;
            case "3-4-3":   tactic = FootballTactic.create343();  break;
            case "5-3-2":   tactic = FootballTactic.create532();  break;
            case "5-4-1":   tactic = FootballTactic.create541();  break;
            default:        tactic = FootballTactic.create442();  break;
        }
        tactic.setAttackingWeight(td.getAttackingWeight());
        tactic.setPressureIntensity(td.getPressureIntensity());
        return tactic;
    }
}
