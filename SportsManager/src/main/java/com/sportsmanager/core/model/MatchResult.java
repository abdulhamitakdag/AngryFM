package com.sportsmanager.core.model;

import java.util.*;

public class MatchResult {

    private final UUID id;
    private final AbstractTeam homeTeam;
    private final AbstractTeam awayTeam;
    private final int homeScore;
    private final int awayScore;

    public MatchResult(AbstractTeam homeTeam, AbstractTeam awayTeam, int homeScore, int awayScore) {
        this.id = UUID.randomUUID();
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        
        if (homeScore < 0) {
            throw new IllegalArgumentException("Score cannot be negative");
        } else {
            this.homeScore = homeScore;
        }
        if (awayScore < 0) {
            throw new IllegalArgumentException("Score cannot be negative");
        } else {
            this.awayScore = awayScore;
        }
    }

    public AbstractTeam getHomeTeam() {
        return homeTeam;
    }

    public AbstractTeam getAwayTeam() {
        return awayTeam;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public boolean isHomeWin() {
        return homeScore > awayScore;
    }

    public boolean isDraw() {
        return homeScore == awayScore;
    }

    public boolean isAwayWin() {
        return awayScore > homeScore;
    }

    public AbstractTeam getWinner() {
        if (isHomeWin()) return homeTeam;
        if (isAwayWin()) return awayTeam;
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchResult that = (MatchResult) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
