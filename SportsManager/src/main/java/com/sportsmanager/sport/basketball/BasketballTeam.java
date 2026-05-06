package com.sportsmanager.sport.basketball;

import com.sportsmanager.core.model.AbstractPlayer;
import com.sportsmanager.core.model.AbstractTeam;

import java.util.List;

// TODO (Person B): validateLineup'ı tamamla — 5 oyuncu, en az 1 PG ve 1 C zorunlu
public class BasketballTeam extends AbstractTeam {

    public BasketballTeam(String name) {
        super(name);
    }

    @Override
    public void validateLineup(List<AbstractPlayer> lineup) {
        if (lineup == null || lineup.size() != 5) {
            throw new IllegalArgumentException("Basketbol lineup'ı tam 5 oyuncu içermelidir.");
        }
        boolean hasPG = false;
        boolean hasC  = false;
        for (AbstractPlayer p : lineup) {
            if (BasketballPositions.PG.name().equals(p.getPosition())) hasPG = true;
            if (BasketballPositions.C.name().equals(p.getPosition()))  hasC  = true;
        }
        if (!hasPG) throw new IllegalArgumentException("Lineup en az 1 PG içermelidir.");
        if (!hasC)  throw new IllegalArgumentException("Lineup en az 1 C içermelidir.");
    }

    @Override
    public int getMaxSquadSize() {
        return 12;
    }
}
