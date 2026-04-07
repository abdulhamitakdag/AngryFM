package com.sportsmanager.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestInjury extends BaseTest {

    @Test
    void decrementGamesRemainingReducesCountFrom1To0() {
        Injury injury = new Injury(Injury.Severity.MINOR, 1);
        injury.decrementGamesRemaining();
        assertEquals(0, injury.getGamesRemaining());
    }

    @Test
    void decrementGamesRemainingReducesCountFrom3To2() {
        Injury injury = new Injury(Injury.Severity.MODERATE, 3);
        injury.decrementGamesRemaining();
        assertEquals(2, injury.getGamesRemaining());
    }

    @Test
    void decrementGamesRemainingThrowsExceptionWhenAlready0() {
        Injury injury = new Injury(Injury.Severity.SERIOUS, 0);
        assertThrows(IllegalStateException.class, injury::decrementGamesRemaining);
    }

    @Test
    void severityIsStoredCorrectly() {
        Injury injury = new Injury(Injury.Severity.MODERATE, 4);
        assertEquals(Injury.Severity.MODERATE, injury.getSeverity());
    }

    @Test
    void gamesRemainingIsStoredCorrectly() {
        Injury injury = new Injury(Injury.Severity.MINOR, 5);
        assertEquals(5, injury.getGamesRemaining());
    }
}