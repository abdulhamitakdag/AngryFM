package com.sportsmanager.core.model;

import org.junit.jupiter.api.Test;

import static com.sportsmanager.util.RandomGenerator.DEFAULT_SPORT;
import static com.sportsmanager.util.RandomGenerator.generateTeam;
import static org.junit.jupiter.api.Assertions.*;

public class TestFixture extends BaseTest {

    @Test
    void isPlayedReturnsFalseBeforeResultIsSet() {
        AbstractTeam home = generateTeam(DEFAULT_SPORT);
        AbstractTeam away = generateTeam(DEFAULT_SPORT);

        Fixture fixture = new Fixture(1, home, away);

        assertFalse(fixture.isPlayed());
    }

    @Test
    void isPlayedReturnsTrueAfterResultIsSet() {
        AbstractTeam home = generateTeam(DEFAULT_SPORT);
        AbstractTeam away = generateTeam(DEFAULT_SPORT);

        Fixture fixture = new Fixture(1, home, away);
        MatchResult result = new MatchResult(home, away, 2, 1);

        fixture.setResult(result);

        assertTrue(fixture.isPlayed());
    }

    @Test
    void setResultStoresResultCorrectly() {
        AbstractTeam home = generateTeam(DEFAULT_SPORT);
        AbstractTeam away = generateTeam(DEFAULT_SPORT);

        Fixture fixture = new Fixture(1, home, away);
        MatchResult result = new MatchResult(home, away, 3, 0);

        fixture.setResult(result);

        assertEquals(result, fixture.getResult());
    }

    @Test
    void setResultSecondTimeThrowsException() {
        AbstractTeam home = generateTeam(DEFAULT_SPORT);
        AbstractTeam away = generateTeam(DEFAULT_SPORT);

        Fixture fixture = new Fixture(1, home, away);
        MatchResult first = new MatchResult(home, away, 1, 0);
        MatchResult second = new MatchResult(home, away, 2, 2);

        fixture.setResult(first);

        assertThrows(IllegalStateException.class, () -> fixture.setResult(second));
    }

    @Test
    void weekIsStoredCorrectly() {
        AbstractTeam home = generateTeam(DEFAULT_SPORT);
        AbstractTeam away = generateTeam(DEFAULT_SPORT);

        Fixture fixture = new Fixture(7, home, away);

        assertEquals(7, fixture.getWeek());
    }
}