package com.sportsmanager.core.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.sportsmanager.util.RandomGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestAbstractTeam extends BaseTest {

    static class DummyTeam extends AbstractTeam {

        public DummyTeam(String name) {
            super(name);
        }

        @Override
        public void validateLineup(List<AbstractPlayer> lineup) {
            if (lineup == null) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public int getMaxSquadSize() {
            return 2;
        }
    }

    static class DummyCoach extends AbstractCoach {

        public DummyCoach() {
            super("Coach", 40, Gender.MALE, CoachSpecialty.ATTACKING, 3);
        }

        @Override
        public double specialtyMultiplier() {
            return 1.0;
        }

        @Override
        public void conductTraining(AbstractTeam team, double intensity) {
            // do nothing (override to avoid side effects)
        }
    }

    @Test
    void constructorStoresNameCorrectly() {
        AbstractTeam team = new DummyTeam("Test Team");

        assertEquals("Test Team", team.getName());
    }

    @Test
    void constructorThrowsExceptionWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new DummyTeam(null));
    }

    @Test
    void constructorThrowsExceptionWhenNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                new DummyTeam(" "));
    }

    @Test
    void addPlayerAddsPlayer_Male() {
        AbstractTeam team = new DummyTeam("Team");
        AbstractPlayer player = generateMalePlayer(DEFAULT_SPORT);

        team.addPlayer(player);

        assertTrue(team.getSquad().contains(player));
    }

    @Test
    void addPlayerAddsPlayer_Female() {
        AbstractTeam team = new DummyTeam("Team");
        AbstractPlayer player = generateFemalePlayer(DEFAULT_SPORT);

        team.addPlayer(player);

        assertTrue(team.getSquad().contains(player));
    }

    @Test
    void addPlayerThrowsExceptionWhenPlayerIsNull() {
        AbstractTeam team = new DummyTeam("Team");

        assertThrows(IllegalArgumentException.class, () ->
                team.addPlayer(null));
    }

    @Test
    void addPlayerThrowsExceptionWhenDuplicate() {
        AbstractTeam team = new DummyTeam("Team");
        AbstractPlayer player = generateMalePlayer(DEFAULT_SPORT);

        team.addPlayer(player);

        assertThrows(IllegalStateException.class, () ->
                team.addPlayer(player));
    }

    @Test
    void addPlayerThrowsExceptionWhenSquadFull() {
        AbstractTeam team = new DummyTeam("Team");

        team.addPlayer(generateMalePlayer(DEFAULT_SPORT));
        team.addPlayer(generateMalePlayer(DEFAULT_SPORT));

        assertThrows(IllegalStateException.class, () ->
                team.addPlayer(generateMalePlayer(DEFAULT_SPORT)));
    }

    @Test
    void removePlayerRemovesPlayer() {
        AbstractTeam team = new DummyTeam("Team");
        AbstractPlayer player = generateMalePlayer(DEFAULT_SPORT);

        team.addPlayer(player);

        assertTrue(team.removePlayer(player));
        assertFalse(team.getSquad().contains(player));
    }

    @Test
    void getSquadReturnsUnmodifiableList() {
        AbstractTeam team = new DummyTeam("Team");

        assertThrows(UnsupportedOperationException.class, () ->
                team.getSquad().add(generateMalePlayer(DEFAULT_SPORT)));
    }

    @Test
    void getAvailablePlayersExcludesInjuredPlayers() {
        AbstractTeam team = new DummyTeam("Team");

        AbstractPlayer healthy = generateMalePlayer(DEFAULT_SPORT);
        AbstractPlayer injured = generateMalePlayer(DEFAULT_SPORT);

        injured.setInjury(new Injury(Injury.Severity.MINOR, 2));

        team.addPlayer(healthy);
        team.addPlayer(injured);

        List<AbstractPlayer> available = team.getAvailablePlayers();

        assertTrue(available.contains(healthy));
        assertFalse(available.contains(injured));
    }

    @Test
    void getInjuredPlayersReturnsOnlyInjured() {
        AbstractTeam team = new DummyTeam("Team");

        AbstractPlayer healthy = generateMalePlayer(DEFAULT_SPORT);
        AbstractPlayer injured = generateMalePlayer(DEFAULT_SPORT);

        injured.setInjury(new Injury(Injury.Severity.MINOR, 2));

        team.addPlayer(healthy);
        team.addPlayer(injured);

        List<AbstractPlayer> injuredList = team.getInjuredPlayers();

        assertTrue(injuredList.contains(injured));
        assertFalse(injuredList.contains(healthy));
    }

    @Test
    void addCoachAddsCoach() {
        AbstractTeam team = new DummyTeam("Team");

        AbstractCoach coach = new DummyCoach();
        team.addCoach(coach);

        assertTrue(team.getCoaches().contains(coach));
    }

    @Test
    void addCoachThrowsExceptionWhenNull() {
        AbstractTeam team = new DummyTeam("Team");

        assertThrows(IllegalArgumentException.class, () ->
                team.addCoach(null));
    }

    @Test
    void getCoachesReturnsUnmodifiableList() {
        AbstractTeam team = new DummyTeam("Team");

        assertThrows(UnsupportedOperationException.class, () ->
                team.getCoaches().add(new DummyCoach()));
    }

    @Test
    void runTrainingSessionDoesNothingWhenNoPlayers() {
        AbstractTeam team = new DummyTeam("Team");

        assertDoesNotThrow(() ->
                team.runTrainingSession(1.0));
    }

    @Test
    void runTrainingSessionUsesReducedIntensityWithoutCoach() {
        AbstractTeam team = new DummyTeam("Team");

        AbstractPlayer player = generateMalePlayer(DEFAULT_SPORT);
        team.addPlayer(player);

        int before = player.getAttributes().computeOverallRating();

        team.runTrainingSession(1.0);

        int after = player.getAttributes().computeOverallRating();

        assertTrue(after >= before);
    }

    @Test
    void runTrainingSessionUsesCoachWhenAvailable() {
        AbstractTeam team = new DummyTeam("Team");

        team.addPlayer(generateMalePlayer(DEFAULT_SPORT));
        team.addCoach(new DummyCoach());

        assertDoesNotThrow(() ->
                team.runTrainingSession(1.0));
    }

    @Test
    void recordResultUpdatesWins() {
        AbstractTeam team = new DummyTeam("Team");

        team.recordResult(true, false, 2, 1);

        assertEquals(1, team.getWins());
    }

    @Test
    void recordResultUpdatesDraws() {
        AbstractTeam team = new DummyTeam("Team");

        team.recordResult(false, true, 1, 1);

        assertEquals(1, team.getDraws());
    }

    @Test
    void recordResultUpdatesLosses() {
        AbstractTeam team = new DummyTeam("Team");

        team.recordResult(false, false, 0, 2);

        assertEquals(1, team.getLosses());
    }

    @Test
    void recordResultUpdatesGoals() {
        AbstractTeam team = new DummyTeam("Team");

        team.recordResult(true, false, 3, 1);

        assertEquals(3, team.getGoalsScored());
        assertEquals(1, team.getGoalsConceded());
    }

    @Test
    void resetSeasonStatsResetsAllValues() {
        AbstractTeam team = new DummyTeam("Team");

        team.recordResult(true, false, 3, 1);
        team.resetSeasonStats();

        assertEquals(0, team.getWins());
        assertEquals(0, team.getDraws());
        assertEquals(0, team.getLosses());
        assertEquals(0, team.getGoalsScored());
        assertEquals(0, team.getGoalsConceded());
    }

    @Test
    void getGoalDifferenceReturnsCorrectValue() {
        AbstractTeam team = new DummyTeam("Team");

        team.recordResult(true, false, 3, 1);

        assertEquals(2, team.getGoalDifference());
    }

    @Test
    void getMatchesPlayedReturnsCorrectValue() {
        AbstractTeam team = new DummyTeam("Team");

        team.recordResult(true, false, 3, 1);
        team.recordResult(false, true, 1, 1);

        assertEquals(2, team.getMatchesPlayed());
    }

    @Test
    void setCurrentTacticStoresTactic() {
        AbstractTeam team = new DummyTeam("Team");

        AbstractTactic tactic = new AbstractTactic("Test", 0.5, 0.5) {
            @Override public String getFormationString() { return "X"; }
            @Override public void validateForSquad(List<AbstractPlayer> squad) {}
        };

        team.setCurrentTactic(tactic);

        assertEquals(tactic, team.getCurrentTactic());
    }

    @Test
    void equalsReturnsTrueForSameObject() {
        AbstractTeam team = new DummyTeam("Team");

        assertEquals(team, team);
    }

    @Test
    void equalsReturnsFalseForDifferentTeams() {
        AbstractTeam t1 = new DummyTeam("Team");
        AbstractTeam t2 = new DummyTeam("Team");

        assertNotEquals(t1, t2);
    }

    @Test
    void toStringContainsTeamName() {
        AbstractTeam team = new DummyTeam("MyTeam");

        assertTrue(team.toString().contains("MyTeam"));
    }
}