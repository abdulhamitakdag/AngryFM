package com.sportsmanager.core.model;

import com.sportsmanager.sport.basketball.BasketballAttributes;
import com.sportsmanager.sport.basketball.BasketballPlayer;
import com.sportsmanager.sport.basketball.BasketballPositions;
import com.sportsmanager.sport.football.FootballAttributes;
import com.sportsmanager.sport.football.FootballPlayer;
import com.sportsmanager.sport.football.FootballPositions;
import com.sportsmanager.sport.football.FootballTeam;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestFootballTeam extends BaseTest{
    private FootballPlayer player(FootballPositions position, int shirt) {
        FootballAttributes attributes = position == FootballPositions.GK
                ? new FootballAttributes(position, 70, 70, 70, 70)
                : new FootballAttributes(position, 70, 70, 70, 70, 70);
        return new FootballPlayer("P" + shirt, 22, Gender.MALE, shirt, position, attributes);
    }

    private List<AbstractPlayer> validLineup() {
        return List.of(
                player(FootballPositions.GK, 1),
                player(FootballPositions.LB, 2), player(FootballPositions.CB, 3),
                player(FootballPositions.CB, 4), player(FootballPositions.RB, 5),
                player(FootballPositions.CM, 6), player(FootballPositions.CM, 7),
                player(FootballPositions.LW, 8), player(FootballPositions.RW, 9),
                player(FootballPositions.ST, 10), player(FootballPositions.ST, 11)
        );
    }

    @Test
    void maxSquadSizeIsTwentyThree() {
        FootballTeam team = new FootballTeam("Football Team");

        assertEquals(23, team.getMaxSquadSize());
    }

    @Test
    void validLineupDoesNotThrow() {
        FootballTeam team = new FootballTeam("Football Team");

        assertDoesNotThrow(() -> team.validateLineup(validLineup()));
    }

    @Test
    void lineupMustContainExactlyElevenPlayers() {
        FootballTeam team = new FootballTeam("Football Team");

        assertThrows(IllegalArgumentException.class, () -> team.validateLineup(null));
        assertThrows(IllegalArgumentException.class, () -> team.validateLineup(validLineup().subList(0, 10)));
    }

    @Test
    void lineupMustContainExactlyOneGoalkeeper() {
        FootballTeam team = new FootballTeam("Football Team");

        List<AbstractPlayer> noGoalkeeper = List.of(
                player(FootballPositions.LB, 2), player(FootballPositions.CB, 3),
                player(FootballPositions.CB, 4), player(FootballPositions.RB, 5),
                player(FootballPositions.CM, 6), player(FootballPositions.CM, 7),
                player(FootballPositions.LW, 8), player(FootballPositions.RW, 9),
                player(FootballPositions.ST, 10), player(FootballPositions.ST, 11),
                player(FootballPositions.CAM, 12));

        assertThrows(IllegalArgumentException.class, () -> team.validateLineup(noGoalkeeper));
    }

    @Test
    void injuredPlayerCannotBeInLineup() {
        FootballTeam team = new FootballTeam("Football Team");
        List<AbstractPlayer> lineup = validLineup();
        lineup.get(1).setInjury(new Injury(Injury.Severity.MINOR, 2));

        assertThrows(IllegalArgumentException.class, () -> team.validateLineup(lineup));
    }

    @Test
    void nonFootballPlayerCannotBeInLineup() {
        FootballTeam team = new FootballTeam("Football Team");
        BasketballPlayer basketballPlayer = new BasketballPlayer("Basket", 22, Gender.MALE, 99,
                BasketballPositions.PG, new BasketballAttributes(BasketballPositions.PG, 70, 70, 70, 70, 70));

        List<AbstractPlayer> lineup = List.of(
                player(FootballPositions.GK, 1), player(FootballPositions.LB, 2),
                player(FootballPositions.CB, 3), player(FootballPositions.CB, 4),
                player(FootballPositions.RB, 5), player(FootballPositions.CM, 6),
                player(FootballPositions.CM, 7), player(FootballPositions.LW, 8),
                player(FootballPositions.RW, 9), player(FootballPositions.ST, 10),
                basketballPlayer);

        assertThrows(IllegalArgumentException.class, () -> team.validateLineup(lineup));
    }
}
