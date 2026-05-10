package com.sportsmanager.core.model;

import com.sportsmanager.sport.football.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestFootballMatch extends BaseTest {
    private FootballPlayer player(FootballPositions position, int shirt) {
        FootballAttributes attributes = position == FootballPositions.GK
                ? new FootballAttributes(position, 70, 70, 70, 70)
                : new FootballAttributes(position, 70, 70, 70, 70, 70);
        return new FootballPlayer("Porçöz" + shirt, 22, Gender.MALE, shirt, position, attributes);
    }

    private FootballTeam team(String name) {
        FootballTeam team = new FootballTeam(name);
        for (int i = 1; i <= 12; i++) {
            team.addPlayer(player(i == 1 ? FootballPositions.GK : FootballPositions.ST, i));
        }
        return team;
    }

    @Test
    void constructorCreatesTwoPeriodMatchWithEngine() {
        FootballMatch match = new FootballMatch(team("Home"), team("Away"));

        assertEquals(2, match.getTotalPeriods());
        assertNotNull(match.getEngine());
        assertEquals(AbstractMatch.MatchState.NOT_STARTED, match.getState());
    }

    @Test
    void matchCanProgressUntilFinished() {
        FootballMatch match = new FootballMatch(team("Home"), team("Away"));

        match.start();
        assertEquals(AbstractMatch.MatchState.IN_PROGRESS, match.getState());

        match.simulateCurrentPeriod();
        assertEquals(AbstractMatch.MatchState.BETWEEN_PERIODS, match.getState());

        match.resumeAfterBreak();
        match.simulateCurrentPeriod();

        assertEquals(AbstractMatch.MatchState.FINISHED, match.getState());
        assertNotNull(match.getMatchResult());
    }

    @Test
    void substitutionRequiresStartedMatchAndValidPlayers() {
        FootballTeam home = team("Home");
        FootballMatch match = new FootballMatch(home, team("Away"));

        assertThrows(IllegalStateException.class, () -> match.substituteHome(home.getSquad().get(0), home.getSquad().get(11)));

        match.start();

        assertDoesNotThrow(() -> match.substituteHome(home.getSquad().get(0), home.getSquad().get(11)));
        assertEquals(1, match.getHomeSubCount());
    }

    @Test
    void moreThanFiveSubstitutionsThrowException() {
        FootballTeam home = team("Home");
        FootballMatch match = new FootballMatch(home, team("Away"));
        match.start();

        for (int i = 0; i < 5; i++) {
            match.substituteHome(home.getSquad().get(0), home.getSquad().get(11));
        }

        assertThrows(IllegalStateException.class, () -> match.substituteHome(home.getSquad().get(0), home.getSquad().get(11)));
    }

    @Test
    void injuredPlayerCannotBeSubstitutedIn() {
        FootballTeam home = team("Home");
        home.getSquad().get(11).setInjury(new Injury(Injury.Severity.MINOR, 2));
        FootballMatch match = new FootballMatch(home, team("Away"));
        match.start();

        assertThrows(IllegalStateException.class, () -> match.substituteHome(home.getSquad().get(0), home.getSquad().get(11)));
    }
}
