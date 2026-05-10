package com.sportsmanager.core.model;

import com.sportsmanager.sport.basketball.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestBasketballMatch {

    @Test
    void fullMatchPlaysFourQuarters() {
        BasketballMatch match = new BasketballMatch(buildTeam("H"), buildTeam("A"));
        match.start();
        while (match.getState() != AbstractMatch.MatchState.FINISHED) {
            if (match.getState() == AbstractMatch.MatchState.IN_PROGRESS) {
                match.simulateCurrentPeriod();
            } else if (match.getState() == AbstractMatch.MatchState.BETWEEN_PERIODS) {
                match.resumeAfterBreak();
            }
        }
        assertTrue(match.getPeriodResults().size() >= 4);
        assertEquals(AbstractMatch.MatchState.FINISHED, match.getState());
    }

    @Test
    void substitutionsAreUnlimited() {
        BasketballMatch match = new BasketballMatch(buildTeam("H"), buildTeam("A"));
        assertEquals(Integer.MAX_VALUE, match.getMaxSubstitutions());
    }

    @Test
    void matchResultIsAvailableAfterFinish() {
        BasketballMatch match = new BasketballMatch(buildTeam("H"), buildTeam("A"));
        match.start();
        while (match.getState() != AbstractMatch.MatchState.FINISHED) {
            if (match.getState() == AbstractMatch.MatchState.IN_PROGRESS) {
                match.simulateCurrentPeriod();
            } else {
                match.resumeAfterBreak();
            }
        }
        MatchResult result = match.getMatchResult();
        assertNotNull(result);
        assertTrue(result.getHomeScore() + result.getAwayScore() > 0);
    }

    @Test
    void overtimeFlagIsCorrectlyInitialized() {
        BasketballMatch match = new BasketballMatch(buildTeam("H"), buildTeam("A"));
        assertFalse(match.isOvertimePlayed());
    }

    private BasketballTeam buildTeam(String name) {
        BasketballTeam team = new BasketballTeam(name);
        BasketballPositions[] positions = BasketballPositions.values();
        for (int i = 0; i < 5; i++) {
            BasketballAttributes attrs = new BasketballAttributes(positions[i % positions.length], 70, 70, 70, 70, 70);
            team.addPlayer(new BasketballPlayer("P" + i, 25, Gender.MALE, i + 1, positions[i % positions.length], attrs));
        }
        return team;
    }
}