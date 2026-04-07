package com.sportsmanager.core.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestPeriodResult extends BaseTest {
    //PeriodResult getter setter ve constructor eksik

    @Test
    void negativeHomeScoreIsClampedTo0() {
        PeriodResult result = new PeriodResult(-1, 2, new ArrayList<>());

        assertEquals(0, result.getHomeScore());
        assertEquals(2, result.getAwayScore());
    }

    @Test
    void negativeAwayScoreIsClampedTo0() {
        PeriodResult result = new PeriodResult(3, -4, new ArrayList<>());

        assertEquals(3, result.getHomeScore());
        assertEquals(0, result.getAwayScore());
    }

    @Test
    void bothNegativeScoresAreClampedTo0() {
        PeriodResult result = new PeriodResult(-5, -8, new ArrayList<>());

        assertEquals(0, result.getHomeScore());
        assertEquals(0, result.getAwayScore());
    }

    @Test
    void eventsListIsUnmodifiable() {
        List<String> events = new ArrayList<>();
        events.add("Goal by home team");

        PeriodResult result = new PeriodResult(1, 0, events);

        assertThrows(UnsupportedOperationException.class, () -> result.getEvents().add("Another event"));
    }

    @Test
    void scoresAreStoredCorrectlyWhenNonNegative() {
        PeriodResult result = new PeriodResult(2, 1, new ArrayList<>());

        assertEquals(2, result.getHomeScore());
        assertEquals(1, result.getAwayScore());
    }
}