package com.sportsmanager.core.model;

import org.junit.jupiter.api.Test;

import static com.sportsmanager.util.RandomGenerator.DEFAULT_SPORT;
import static com.sportsmanager.util.RandomGenerator.generateTeam;
import static org.junit.jupiter.api.Assertions.*;

public class TestTeamInLeagueTable extends BaseTest {

    @Test
    void addResultWinAddsWinPoints() {
        AbstractTeam team = generateTeam(DEFAULT_SPORT);
        TeamInLeagueTable entry = new TeamInLeagueTable(team);

        entry.addResult(true, false, 3, 1, 3, 1);

        assertEquals(3, entry.getPoints());
    }

    @Test
    void addResultDrawAddsDrawPoints() {
        AbstractTeam team = generateTeam(DEFAULT_SPORT);
        TeamInLeagueTable entry = new TeamInLeagueTable(team);

        entry.addResult(false, true, 2, 2, 3, 1);

        assertEquals(1, entry.getPoints());
    }

    @Test
    void addResultLossAddsNoPoints() {
        AbstractTeam team = generateTeam(DEFAULT_SPORT);
        TeamInLeagueTable entry = new TeamInLeagueTable(team);

        entry.addResult(false, false, 0, 2, 3, 1);

        assertEquals(0, entry.getPoints());
    }

    @Test
    void getGoalDifferenceReturnsPositiveValueWhenGoalsScoredMoreThanConceded() {
        AbstractTeam team = generateTeam(DEFAULT_SPORT);
        TeamInLeagueTable entry = new TeamInLeagueTable(team);

        entry.addResult(true, false, 4, 1, 3, 1);

        assertEquals(3, entry.getGoalDifference());
    }

    @Test
    void getGoalDifferenceReturns0WhenGoalsScoredEqualsGoalsConceded() {
        AbstractTeam team = generateTeam(DEFAULT_SPORT);
        TeamInLeagueTable entry = new TeamInLeagueTable(team);

        entry.addResult(false, true, 2, 2, 3, 1);

        assertEquals(0, entry.getGoalDifference());
    }

    @Test
    void getGoalDifferenceReturnsNegativeValueWhenGoalsConcededMoreThanScored() {
        AbstractTeam team = generateTeam(DEFAULT_SPORT);
        TeamInLeagueTable entry = new TeamInLeagueTable(team);

        entry.addResult(false, false, 1, 3, 3, 1);

        assertEquals(-2, entry.getGoalDifference());
    }

    @Test
    void getTeamReturnsSameTeamGivenInConstructor() {
        AbstractTeam team = generateTeam(DEFAULT_SPORT);
        TeamInLeagueTable entry = new TeamInLeagueTable(team);

        assertEquals(team, entry.getTeam());
    }

    @Test
    void compareToUsesPointsFirst() {
        AbstractTeam team1 = generateTeam(DEFAULT_SPORT);
        AbstractTeam team2 = generateTeam(DEFAULT_SPORT);

        TeamInLeagueTable better = new TeamInLeagueTable(team1);
        TeamInLeagueTable worse = new TeamInLeagueTable(team2);

        better.addResult(true, false, 1, 0, 3, 1);
        worse.addResult(false, true, 1, 1, 3, 1);

        assertTrue(better.compareTo(worse) < 0);
    }

    @Test
    void compareToUsesGoalDifferenceWhenPointsAreEqual() {
        AbstractTeam team1 = generateTeam(DEFAULT_SPORT);
        AbstractTeam team2 = generateTeam(DEFAULT_SPORT);

        TeamInLeagueTable better = new TeamInLeagueTable(team1);
        TeamInLeagueTable worse = new TeamInLeagueTable(team2);

        better.addResult(true, false, 3, 0, 3, 1);
        worse.addResult(true, false, 1, 0, 3, 1);

        assertTrue(better.compareTo(worse) < 0);
    }

    @Test
    void equalsReturnsTrueForSameObject() {
        AbstractTeam team = generateTeam(DEFAULT_SPORT);
        TeamInLeagueTable entry = new TeamInLeagueTable(team);

        assertEquals(entry, entry);
    }

    @Test
    void equalsReturnsFalseForDifferentObjectsEvenIfTeamsAreSame() {
        AbstractTeam team = generateTeam(DEFAULT_SPORT);

        TeamInLeagueTable entry1 = new TeamInLeagueTable(team);
        TeamInLeagueTable entry2 = new TeamInLeagueTable(team);

        assertNotEquals(entry1, entry2);
    }

    @Test
    void hashCodeIsSameForSameObject() {
        AbstractTeam team = generateTeam(DEFAULT_SPORT);
        TeamInLeagueTable entry = new TeamInLeagueTable(team);

        assertEquals(entry.hashCode(), entry.hashCode());
    }
}