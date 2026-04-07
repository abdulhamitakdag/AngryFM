package com.sportsmanager.core.model;

import java.util.*;

public class TeamInLeagueTable implements Comparable<TeamInLeagueTable> {

    private final UUID id;
    private final AbstractTeam team;
    private int points;
    private int goalsScored;
    private int goalsConceded;
    private int wins;
    private int draws;
    private int losses;

    public TeamInLeagueTable(AbstractTeam team) {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null.");
        }
        this.id = UUID.randomUUID();
        this.team = team;
    }

    public void addResult(boolean isWin, boolean isDraw, int goalsScored, int goalsConceded, int winPoints, int drawPoints) {

        this.goalsScored += goalsScored;
        this.goalsConceded += goalsConceded;

        if (isWin) {
            this.wins++;
            this.points += winPoints;
        } else if (isDraw) {
            this.draws++;
            this.points += drawPoints;
        } else {
            this.losses++;
        }
    }

    public int getGoalDifference() {
        return goalsScored - goalsConceded;
    }

    public int getPoints() {
        return points;
    }

    public AbstractTeam getTeam() {
        return team;
    }

    public int getWins() {
        return wins;
    }
    public int getDraws() {
        return draws;
    }
    public int getLosses() {
        return losses;
    }
    public int getGoalsScored() {
        return goalsScored;
    }
    public int getGoalsConceded() {
        return goalsConceded;
    }

    @Override
    public int compareTo(TeamInLeagueTable other) {
        if (this.points != other.points) {
            return Integer.compare(other.points, this.points);
        }
        return Integer.compare(other.getGoalDifference(), this.getGoalDifference());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamInLeagueTable that = (TeamInLeagueTable) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}