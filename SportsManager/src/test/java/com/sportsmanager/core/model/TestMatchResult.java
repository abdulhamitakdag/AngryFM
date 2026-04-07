package com.sportsmanager.core.model;

import org.junit.jupiter.api.Test;

import static com.sportsmanager.util.RandomGenerator.DEFAULT_SPORT;
import static com.sportsmanager.util.RandomGenerator.generateTeam;
import static org.junit.jupiter.api.Assertions.*;

public class TestMatchResult extends BaseTest {
    //MatchResult getter setterlar eksik ve bir sıkıntı var parametrelerde

    @Test
    void isHomeWinReturnsTrueWhenHomeScoreIsHigher() {
        AbstractTeam home = generateTeam(DEFAULT_SPORT);
        AbstractTeam away = generateTeam(DEFAULT_SPORT);

        MatchResult result = new MatchResult(home, away, 2, 1);

        assertTrue(result.isHomeWin());
        assertFalse(result.isAwayWin());
        assertFalse(result.isDraw());
    }

    @Test
    void isAwayWinReturnsTrueWhenAwayScoreIsHigher() {
        AbstractTeam home = generateTeam(DEFAULT_SPORT);
        AbstractTeam away = generateTeam(DEFAULT_SPORT);

        MatchResult result = new MatchResult(home, away, 1, 3);

        assertFalse(result.isHomeWin());
        assertTrue(result.isAwayWin());
        assertFalse(result.isDraw());
    }

    @Test
    void isDrawReturnsTrueWhenScoresAreEqual() {
        AbstractTeam home = generateTeam(DEFAULT_SPORT);
        AbstractTeam away = generateTeam(DEFAULT_SPORT);

        MatchResult result = new MatchResult(home, away, 2, 2);

        assertFalse(result.isHomeWin());
        assertFalse(result.isAwayWin());
        assertTrue(result.isDraw());
    }

    @Test
    void getWinnerReturnsHomeTeamWhenHomeWins() {
        AbstractTeam home = generateTeam(DEFAULT_SPORT);
        AbstractTeam away = generateTeam(DEFAULT_SPORT);

        MatchResult result = new MatchResult(home, away, 4, 1);

        assertEquals(home, result.getWinner());
    }

    @Test
    void getWinnerReturnsNullWhenDraw() {
        AbstractTeam home = generateTeam(DEFAULT_SPORT);
        AbstractTeam away = generateTeam(DEFAULT_SPORT);

        MatchResult result = new MatchResult(home, away, 0, 0);

        assertNull(result.getWinner());
    }
}