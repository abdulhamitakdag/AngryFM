package com.sportsmanager.sport.football;

import com.sportsmanager.core.model.AbstractLeague;
import com.sportsmanager.core.model.AbstractMatch;
import com.sportsmanager.core.model.AbstractTeam;

public class FootballLeague extends AbstractLeague {

    private final int pointsForWin = 3;
    private final int pointsForDraw = 1;

    public FootballLeague(String name) {
        super(name);
    }

    public int getPointsForWin() {
        return pointsForWin;
    }
    public int getPointsForDraw() {
        return pointsForDraw;
    }

    @Override
    protected int getWinPoints() {
        return pointsForWin;
    }

    @Override
    protected int getDrawPoints() {
        return pointsForDraw;
    }

    @Override
    protected AbstractMatch createMatch(AbstractTeam home, AbstractTeam away) {
        return new FootballMatch(home, away);
    }
}