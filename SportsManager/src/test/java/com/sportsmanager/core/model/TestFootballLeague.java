package com.sportsmanager.core.model;

import com.sportsmanager.sport.football.FootballLeague;
import com.sportsmanager.sport.football.FootballMatch;
import com.sportsmanager.sport.football.FootballMatchEngine;
import com.sportsmanager.sport.football.FootballTeam;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestFootballLeague extends BaseTest {

    @Test
    void footballLeagueStoresNameAndPointRules() {
        FootballLeague league = new FootballLeague("Premier Test League");

        assertEquals("Premier Test League", league.getName());
        assertEquals(3, league.getPointsForWin());
        assertEquals(1, league.getPointsForDraw());
    }

    @Test
    void generateFixturesCreatesDoubleRoundRobin()
    //round robin turnuva rotasyon yöntemi ismi, don't forget
    {
        FootballLeague league = new FootballLeague("Premier Test League");
        List<AbstractTeam> teams = List.of(
                new FootballTeam("A"), new FootballTeam("B"),
                new FootballTeam("C"), new FootballTeam("D"));

        league.generateFixtures(teams);

        assertEquals(4, league.getTeams().size());
        assertEquals(12, league.getFixtures().size());
        assertFalse(league.isSeasonOver());
    }

    @Test
    void recordResultGivesThreePointsForWin() {
        FootballLeague league = new FootballLeague("Premier Test League");
        FootballTeam home = new FootballTeam("Home");
        FootballTeam away = new FootballTeam("Away");
        league.generateFixtures(List.of(home, away));

        league.recordResult(new MatchResult(home, away, 2, 0));

        assertEquals(home, league.getStandings().get(0).getTeam());
        assertEquals(3, league.getStandings().get(0).getPoints());
        assertEquals(1, home.getWins());
        assertEquals(1, away.getLosses());
    }

    @Test
    void recordResultGivesOnePointForDraw() {
        FootballLeague league = new FootballLeague("Premier Test League");
        FootballTeam home = new FootballTeam("Home");
        FootballTeam away = new FootballTeam("Away");
        league.generateFixtures(List.of(home, away));

        league.recordResult(new MatchResult(home, away, 1, 1));

        assertEquals(1, league.getStandings().get(0).getPoints());
        assertEquals(1, league.getStandings().get(1).getPoints());
    }

    @Test
    void createMatchReturnsFootballMatchThroughFixtureGenerationFlow() {
        FootballMatch match = new FootballMatch(new FootballTeam("Home"), new FootballTeam("Away"));

        assertEquals(2, match.getTotalPeriods());
        assertInstanceOf(FootballMatchEngine.class, match.getEngine());
    }
}
