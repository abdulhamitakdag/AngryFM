package com.sportsmanager.sport.football;

import com.sportsmanager.core.model.AbstractPlayer;
import com.sportsmanager.core.model.AbstractTeam;

import java.util.List;

public class FootballTeam extends AbstractTeam {

    public FootballTeam(String name) {
        super(name);
    }

    @Override
    public int getMaxSquadSize() {
        return 23;
    }

    @Override
    public void validateLineup(List<AbstractPlayer> lineup) {

        if (lineup == null || lineup.size() != 11) {
            throw new IllegalArgumentException("Squad error! There must be 11 players on the field. Current number is " +
                    (lineup == null ? 0 : lineup.size()));
        }
        int gk = 0;

        for (AbstractPlayer player : lineup) {
            if (player.isInjured()) {
                throw new IllegalArgumentException("Squad error! " + player.getName() + " is injured and can not play in the next match.");
            }
            if (!(player instanceof FootballPlayer)) {
                throw new IllegalArgumentException("Squad error! No other players than football can be added.");
            }
        }
        for (AbstractPlayer player : lineup) {
            FootballPlayer fp = (FootballPlayer) player;
            if (fp.getFootballPosition() == FootballPositions.GK) {
                gk++;
            }
        }
        if (gk != 1) {
            throw new IllegalArgumentException("Squad error: There must be exactly 1 goalkeeper on the field. Current number is " + gk);
        }
    }
}