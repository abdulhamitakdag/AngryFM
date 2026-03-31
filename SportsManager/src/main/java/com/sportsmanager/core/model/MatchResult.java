package com.sportsmanager.core.model;

import java.util.*;

public class MatchResult {

    private final UUID id;
    private final int homeScore;
    private final int awayScore;

    public MatchResult(int homeScore, int awayScore) {
        this.id = UUID.randomUUID();
        if (homeScore < 0) {
            this.homeScore = 0;
        } else {
            this.homeScore = homeScore;
        }
        if (awayScore < 0) {
            this.awayScore = 0;
        } else {
            this.awayScore = awayScore;
        }
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
