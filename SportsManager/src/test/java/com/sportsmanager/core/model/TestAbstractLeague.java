package com.sportsmanager.core.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.sportsmanager.util.RandomGenerator.DEFAULT_SPORT;
import static com.sportsmanager.util.RandomGenerator.generateTeam;
import static org.junit.jupiter.api.Assertions.*;

public class TestAbstractLeague extends BaseTest {

    static class DummyLeague extends AbstractLeague {

        public DummyLeague(String name) {
            super(name);
        }

        @Override
        protected int getWinPoints() {
            return 3;
        }

        @Override
        protected int getDrawPoints() {
            return 1;
        }

        @Override
        protected AbstractMatch createMatch(AbstractTeam home, AbstractTeam away) {
            return null; // not used in this class
        }
    }

    @Test
    void constructorStoresNameCorrectly() {
        AbstractLeague league = new DummyLeague("Test League");

        assertEquals("Test League", league.getName());
    }

    @Test
    void generateFixturesThrowsExceptionWhenLessThan2Teams() {
        AbstractLeague league = new DummyLeague("Test League");

        assertThrows(IllegalArgumentException.class,
                () -> league.generateFixtures(List.of(generateTeam())));
    }

    @Test
    void generateFixturesCreatesFixturesFor4Teams() {
        AbstractLeague league = new DummyLeague("Test League");

        List<AbstractTeam> teams = List.of(
                generateTeam(DEFAULT_SPORT),
                generateTeam(DEFAULT_SPORT),
                generateTeam(DEFAULT_SPORT),
                generateTeam(DEFAULT_SPORT));

        league.generateFixtures(teams);

        assertFalse(league.getFixtures().isEmpty());
    }

    @Test
    void generateFixturesCreatesCorrectNumberOfFixturesFor4Teams() {
        AbstractLeague league = new DummyLeague("Test League");

        List<AbstractTeam> teams = List.of(
                generateTeam(DEFAULT_SPORT),
                generateTeam(DEFAULT_SPORT),
                generateTeam(DEFAULT_SPORT),
                generateTeam(DEFAULT_SPORT));

        league.generateFixtures(teams);

        assertEquals(12, league.getFixtures().size());
    }

    @Test
    void teamMapAndStandingsContainAllTeams() {
        AbstractLeague league = new DummyLeague("Test League");

        List<AbstractTeam> teams = List.of(
                generateTeam(DEFAULT_SPORT),
                generateTeam(DEFAULT_SPORT),
                generateTeam(DEFAULT_SPORT),
                generateTeam(DEFAULT_SPORT));

        league.generateFixtures(teams);

        assertEquals(4, league.getStandings().size());
    }

    @Test
    void recordResultThrowsExceptionWhenResultIsNull() {
        AbstractLeague league = new DummyLeague("Test League");

        List<AbstractTeam> teams = List.of(
                generateTeam(DEFAULT_SPORT),
                generateTeam(DEFAULT_SPORT));

        league.generateFixtures(teams);

        assertThrows(IllegalArgumentException.class,
                () -> league.recordResult(null));
    }

    @Test
    void recordResultUpdatesStandings() {
        AbstractLeague league = new DummyLeague("Test League");

        AbstractTeam t1 = generateTeam(DEFAULT_SPORT);
        AbstractTeam t2 = generateTeam(DEFAULT_SPORT);

        league.generateFixtures(List.of(t1, t2));

        MatchResult result = new MatchResult(t1, t2, 2, 1);// ilki home score, ikinci away score

        league.recordResult(result);

        List<TeamInLeagueTable> standings = league.getStandings();

        assertEquals(2, standings.size());
        assertTrue(standings.get(0).getPoints() >= standings.get(1).getPoints());
    }

    @Test
    void recordResultThrowsExceptionWhenNoFixturesRemaining() {
        AbstractLeague league = new DummyLeague("Test League");

        AbstractTeam t1 = generateTeam(DEFAULT_SPORT);
        AbstractTeam t2 = generateTeam(DEFAULT_SPORT);

        league.generateFixtures(List.of(t1, t2));

        MatchResult result = new MatchResult(t1, t2, 1, 0);

        // play all matches
        while (!league.isSeasonOver()) {
            league.recordResult(result);
        }

        assertThrows(IllegalStateException.class,
                () -> league.recordResult(result));
    }

    @Test
    void getStandingsReturnsSortedList() {
        AbstractLeague league = new DummyLeague("Test League");

        AbstractTeam t1 = generateTeam(DEFAULT_SPORT);
        AbstractTeam t2 = generateTeam(DEFAULT_SPORT);

        league.generateFixtures(List.of(t1, t2));

        league.recordResult(new MatchResult(t1, t2, 3, 0));

        List<TeamInLeagueTable> standings = league.getStandings();

        assertTrue(standings.get(0).getPoints() >= standings.get(1).getPoints());
    }

    @Test
    void getStandingsReturnsUnmodifiableList() {
        AbstractLeague league = new DummyLeague("Test League");

        league.generateFixtures(List.of(
                generateTeam(DEFAULT_SPORT),
                generateTeam(DEFAULT_SPORT)));

        List<TeamInLeagueTable> standings = league.getStandings();

        assertThrows(UnsupportedOperationException.class,
                () -> standings.add(null));
    }

    @Test
    void isSeasonOverReturnsFalseAtStart() {
        AbstractLeague league = new DummyLeague("Test League");

        league.generateFixtures(List.of(
                generateTeam(DEFAULT_SPORT),
                generateTeam(DEFAULT_SPORT)));

        assertFalse(league.isSeasonOver());
    }

    @Test
    void isSeasonOverReturnsTrueWhenAllMatchesPlayed() {
        AbstractLeague league = new DummyLeague("Test League");

        AbstractTeam t1 = generateTeam(DEFAULT_SPORT);
        AbstractTeam t2 = generateTeam(DEFAULT_SPORT);

        league.generateFixtures(List.of(t1, t2));

        MatchResult result = new MatchResult(t1, t2, 1, 0);

        while (!league.isSeasonOver()) {
            league.recordResult(result);
        }

        assertTrue(league.isSeasonOver());
    }

    @Test
    void getChampionReturnsNullWhenSeasonNotFinished() {
        AbstractLeague league = new DummyLeague("Test League");

        league.generateFixtures(List.of(
                generateTeam(DEFAULT_SPORT),
                generateTeam(DEFAULT_SPORT)));

        assertNull(league.getChampion());
    }

    @Test
    void getChampionReturnsTeamWhenSeasonFinished() {
        AbstractLeague league = new DummyLeague("Test League");

        AbstractTeam t1 = generateTeam(DEFAULT_SPORT);
        AbstractTeam t2 = generateTeam(DEFAULT_SPORT);

        league.generateFixtures(List.of(t1, t2));

        MatchResult result = new MatchResult(t1, t2, 2, 0);

        while (!league.isSeasonOver()) {
            league.recordResult(result);
        }

        assertNotNull(league.getChampion());
    }

    @Test
    void getUnplayedFixturesReturnsOnlyUnplayedMatches() {
        AbstractLeague league = new DummyLeague("Test League");

        AbstractTeam t1 = generateTeam(DEFAULT_SPORT);
        AbstractTeam t2 = generateTeam(DEFAULT_SPORT);

        league.generateFixtures(List.of(t1, t2));

        int total = league.getFixtures().size();

        league.recordResult(new MatchResult(t1, t2, 1, 0));

        int remaining = league.getUnplayedFixtures().size();

        assertTrue(remaining < total);
    }

    @Test
    void advanceWeekIncreasesCurrentWeek() {
        AbstractLeague league = new DummyLeague("Test League");

        int before = league.getCurrentWeek();

        league.advanceWeek();

        int after = league.getCurrentWeek();

        assertEquals(before + 1, after);
    }
}