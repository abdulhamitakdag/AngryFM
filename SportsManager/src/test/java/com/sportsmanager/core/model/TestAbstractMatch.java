package com.sportsmanager.core.model;

import org.junit.jupiter.api.Test;

import static com.sportsmanager.util.RandomGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestAbstractMatch extends BaseTest {

    static class DummyMatch extends AbstractMatch {

        private boolean injuriesApplied = false;

        public DummyMatch(AbstractTeam homeTeam, AbstractTeam awayTeam) {
            super(homeTeam, awayTeam);
        }

        @Override
        public int getTotalPeriods() {
            return 2;
        }

        @Override
        protected void validateSubstitution(AbstractTeam team, AbstractPlayer out, AbstractPlayer in) {
            if (out == null || in == null) {
                throw new IllegalArgumentException("Players cannot be null");
            }
        }

        @Override
        protected PeriodResult simulatePeriodInternal(AbstractTeam home, AbstractTeam away) {
            return new PeriodResult(1, 0);
        }

        @Override
        protected void applyInjuries(AbstractTeam home, AbstractTeam away) {
            injuriesApplied = true;
        }

        @Override
        protected int getMaxSubstitutions() {
            return 2;
        }

        public boolean isInjuriesApplied() {
            return injuriesApplied;
        }
    }

    @Test
    void constructorStoresHomeTeamCorrectly() {
        AbstractTeam home = generateTeam(DEFAULT_SPORT);
        AbstractTeam away = generateTeam(DEFAULT_SPORT);

        AbstractMatch match = new DummyMatch(home, away);

        assertEquals(home, match.getHomeTeam());
    }

    @Test
    void constructorStoresAwayTeamCorrectly() {
        AbstractTeam home = generateTeam(DEFAULT_SPORT);
        AbstractTeam away = generateTeam(DEFAULT_SPORT);

        AbstractMatch match = new DummyMatch(home, away);

        assertEquals(away, match.getAwayTeam());
    }

    @Test
    void constructorSetsInitialStateToNotStarted() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));

        assertEquals(AbstractMatch.MatchState.NOT_STARTED, match.getState());
    }

    @Test
    void constructorSetsCurrentPeriodTo0() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));

        assertEquals(0, match.getCurrentPeriod());
    }

    @Test
    void constructorThrowsExceptionWhenHomeTeamIsNull() {
        AbstractTeam away = generateTeam(DEFAULT_SPORT);

        assertThrows(IllegalArgumentException.class, () ->
                new DummyMatch(null, away));
    }

    @Test
    void constructorThrowsExceptionWhenAwayTeamIsNull() {
        AbstractTeam home = generateTeam(DEFAULT_SPORT);

        assertThrows(IllegalArgumentException.class, () ->
                new DummyMatch(home, null));
    }

    @Test
    void startChangesStateToInProgress() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));

        match.start();

        assertEquals(AbstractMatch.MatchState.IN_PROGRESS, match.getState());
    }

    @Test
    void startSetsCurrentPeriodTo1() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));

        match.start();

        assertEquals(1, match.getCurrentPeriod());
    }

    @Test
    void startThrowsExceptionWhenCalledTwice() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));

        match.start();

        assertThrows(IllegalStateException.class, match::start);
    }

    @Test
    void simulateCurrentPeriodThrowsExceptionWhenMatchNotInProgress() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));

        assertThrows(IllegalStateException.class, match::simulateCurrentPeriod);
    }

    @Test
    void simulateCurrentPeriodAddsPeriodResult() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));
        match.start();

        match.simulateCurrentPeriod();

        assertEquals(1, match.getPeriodResults().size());
    }

    @Test
    void simulateCurrentPeriodChangesStateToBetweenPeriodsAfterFirstPeriod() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));
        match.start();

        match.simulateCurrentPeriod();

        assertEquals(AbstractMatch.MatchState.BETWEEN_PERIODS, match.getState());
    }

    @Test
    void resumeAfterBreakChangesStateToInProgress() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));
        match.start();
        match.simulateCurrentPeriod();

        match.resumeAfterBreak();

        assertEquals(AbstractMatch.MatchState.IN_PROGRESS, match.getState());
    }

    @Test
    void resumeAfterBreakIncreasesCurrentPeriod() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));
        match.start();
        match.simulateCurrentPeriod();

        match.resumeAfterBreak();

        assertEquals(2, match.getCurrentPeriod());
    }

    @Test
    void resumeAfterBreakThrowsExceptionWhenNotBetweenPeriods() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));

        assertThrows(IllegalStateException.class, match::resumeAfterBreak);
    }

    @Test
    void secondSimulateCurrentPeriodFinishesMatch() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));
        match.start();
        match.simulateCurrentPeriod();
        match.resumeAfterBreak();

        match.simulateCurrentPeriod();

        assertEquals(AbstractMatch.MatchState.FINISHED, match.getState());
    }

    @Test
    void secondSimulateCurrentPeriodAppliesInjuries() {
        DummyMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));
        match.start();
        match.simulateCurrentPeriod();
        match.resumeAfterBreak();

        match.simulateCurrentPeriod();

        assertTrue(match.isInjuriesApplied());
    }

    @Test
    void substituteHomeThrowsExceptionBeforeMatchStarts_Male() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));

        assertThrows(IllegalStateException.class, () ->
                match.substituteHome(generateMalePlayer(DEFAULT_SPORT), generateMalePlayer(DEFAULT_SPORT)));
    }

    @Test
    void substituteHomeThrowsExceptionBeforeMatchStarts_Female() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));

        assertThrows(IllegalStateException.class, () ->
                match.substituteHome(generateFemalePlayer(DEFAULT_SPORT), generateFemalePlayer(DEFAULT_SPORT)));
    }

    @Test
    void substituteHomeIncreasesHomeSubCount_Male() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));
        match.start();

        match.substituteHome(generateMalePlayer(DEFAULT_SPORT), generateMalePlayer(DEFAULT_SPORT));

        assertEquals(1, match.getHomeSubCount());
    }

    @Test
    void substituteHomeIncreasesHomeSubCount_Female() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));
        match.start();

        match.substituteHome(generateFemalePlayer(DEFAULT_SPORT), generateFemalePlayer(DEFAULT_SPORT));

        assertEquals(1, match.getHomeSubCount());
    }

    @Test
    void substituteAwayIncreasesAwaySubCount_Male() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));
        match.start();

        match.substituteAway(generateMalePlayer(DEFAULT_SPORT), generateMalePlayer(DEFAULT_SPORT));

        assertEquals(1, match.getAwaySubCount());
    }

    @Test
    void substituteAwayIncreasesAwaySubCount_Female() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));
        match.start();

        match.substituteAway(generateFemalePlayer(DEFAULT_SPORT), generateFemalePlayer(DEFAULT_SPORT));

        assertEquals(1, match.getAwaySubCount());
    }

    @Test
    void substituteHomeThrowsExceptionWhenSubstitutionLimitReached() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));
        match.start();

        match.substituteHome(generateMalePlayer(DEFAULT_SPORT), generateMalePlayer(DEFAULT_SPORT));
        match.substituteHome(generateMalePlayer(DEFAULT_SPORT), generateMalePlayer(DEFAULT_SPORT));

        assertThrows(IllegalStateException.class, () ->
                match.substituteHome(generateMalePlayer(DEFAULT_SPORT), generateMalePlayer(DEFAULT_SPORT)));
    }

    @Test
    void substituteAwayThrowsExceptionWhenSubstitutionLimitReached() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));
        match.start();

        match.substituteAway(generateMalePlayer(DEFAULT_SPORT), generateMalePlayer(DEFAULT_SPORT));
        match.substituteAway(generateMalePlayer(DEFAULT_SPORT), generateMalePlayer(DEFAULT_SPORT));

        assertThrows(IllegalStateException.class, () ->
                match.substituteAway(generateMalePlayer(DEFAULT_SPORT), generateMalePlayer(DEFAULT_SPORT)));
    }

    @Test
    void substituteHomeThrowsExceptionWhenPlayerIsNull() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));
        match.start();

        assertThrows(IllegalArgumentException.class, () ->
                match.substituteHome(null, generateMalePlayer(DEFAULT_SPORT)));
    }

    @Test
    void substituteAwayThrowsExceptionWhenPlayerIsNull() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));
        match.start();

        assertThrows(IllegalArgumentException.class, () ->
                match.substituteAway(generateMalePlayer(DEFAULT_SPORT), null));
    }

    @Test
    void substituteHomeThrowsExceptionAfterMatchFinishes() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));
        match.start();
        match.simulateCurrentPeriod();
        match.resumeAfterBreak();
        match.simulateCurrentPeriod();

        assertThrows(IllegalStateException.class, () ->
                match.substituteHome(generateMalePlayer(DEFAULT_SPORT), generateMalePlayer(DEFAULT_SPORT)));
    }

    @Test
    void getMatchResultThrowsExceptionBeforeMatchFinishes() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));
        match.start();

        assertThrows(IllegalStateException.class, match::getMatchResult);
    }

    @Test
    void getMatchResultReturnsTotalScoresAfterMatchFinishes() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));
        match.start();
        match.simulateCurrentPeriod();
        match.resumeAfterBreak();
        match.simulateCurrentPeriod();

        MatchResult result = match.getMatchResult();

        assertEquals(2, result.getHomeScore());
        assertEquals(0, result.getAwayScore());
    }

    @Test
    void getPeriodResultsReturnsUnmodifiableList() {
        AbstractMatch match = new DummyMatch(generateTeam(DEFAULT_SPORT), generateTeam(DEFAULT_SPORT));
        match.start();
        match.simulateCurrentPeriod();

        assertThrows(UnsupportedOperationException.class, () ->
                match.getPeriodResults().add(new PeriodResult(1, 1)));
    }
}