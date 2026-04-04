package com.sportsmanager.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestInjury extends BaseTest{
    @Test
    void createInjuryWhenInputValid() {
        Injury injury = new Injury(Injury.Severity.MODERATE, 3);
        assertNotNull(injury.getId());
        assertEquals(Injury.Severity.MODERATE, injury.getSeverity());
        assertEquals(3, injury.getGamesRemaining());
    }

    @Test
    void decrementGamesRemainingShouldNotGoBelow0() {
        Injury injury = new Injury(Injury.Severity.MINOR, 1);
        injury.decrementGamesRemaining();
        injury.decrementGamesRemaining();
        assertEquals(0, injury.getGamesRemaining());
    }

    @Test
    void decrementGamesRemainingShouldReduceValueBy1() {
        Injury injury = new Injury(Injury.Severity.MINOR, 2);
        injury.decrementGamesRemaining();
        assertEquals(1, injury.getGamesRemaining());
    }

    @Test
    void throwWhenLessThan1GameRemains() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Injury(Injury.Severity.MINOR, 0)
        );
        assertEquals("gamesRemaining must be at least 1!", ex.getMessage());
    }

    @Test
    void multipleInjuriesCanExist() {
        Injury injury1 = new Injury(Injury.Severity.MINOR, 2);
        Injury injury2 = new Injury(Injury.Severity.MINOR, 2);
        assertEquals(injury1, injury1);
        assertNotEquals(injury1, injury2);
    }

    @Test
    void throwWhenSeverityNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Injury(null, 3)
        );
        assertEquals("Severity cannot be null!", ex.getMessage());
    }

}
