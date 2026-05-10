package com.sportsmanager.sport.basketball;

import com.sportsmanager.core.model.AbstractPlayer;
import com.sportsmanager.core.model.AbstractTeam;
import com.sportsmanager.sport.football.FootballPlayer;

import java.util.List;

public class BasketballTeam extends AbstractTeam {

    public BasketballTeam(String name) {
        super(name);
        this.setCurrentTactic(BasketballTactic.createBalanced());
    }
    @Override
    public int getMaxSquadSize() {
        return 12;
    }

    @Override
    public void validateLineup(List<AbstractPlayer> lineup) {
        if (lineup == null || lineup.size() != 5) {
            throw new IllegalArgumentException("Basketball lineup must contain exactly 5 players.");
        }

        for (AbstractPlayer player : lineup) {
            if (player.isInjured()) {
                throw new IllegalArgumentException("Squad error! " + player.getName() + " is injured and can not play in the next match.");
            }
            if (!(player instanceof BasketballPlayer)) {
                throw new IllegalArgumentException("Squad error! No other players than basketball can be added.");
            }
        }

        boolean hasPG = false;
        boolean hasSG = false;
        boolean hasSF = false;
        boolean hasPF = false;
        boolean hasC  = false;

        for (AbstractPlayer p : lineup) {
            if (BasketballPositions.PG.name().equals(p.getPosition())) hasPG = true;
            if (BasketballPositions.SG.name().equals(p.getPosition())) hasSG = true;
            if (BasketballPositions.SF.name().equals(p.getPosition())) hasSF = true;
            if (BasketballPositions.PF.name().equals(p.getPosition())) hasPF = true;
            if (BasketballPositions.C.name().equals(p.getPosition()))  hasC  = true;
        }
        if (!hasPG) throw new IllegalArgumentException("Lineup must contain at least 1 PG.");
        if (!hasSG) throw new IllegalArgumentException("Lineup must contain at least 1 SG.");
        if (!hasSF) throw new IllegalArgumentException("Lineup must contain at least 1 SF.");
        if (!hasPF) throw new IllegalArgumentException("Lineup must contain at least 1 PF.");
        if (!hasC)  throw new IllegalArgumentException("Lineup must contain at least 1 C.");


    }
}
