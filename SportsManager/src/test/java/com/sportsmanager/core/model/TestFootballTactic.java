package com.sportsmanager.core.model;

import com.sportsmanager.sport.football.FootballAttributes;
import com.sportsmanager.sport.football.FootballPlayer;
import com.sportsmanager.sport.football.FootballPositions;
import com.sportsmanager.sport.football.FootballTactic;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestFootballTactic extends BaseTest{
    private FootballPlayer player(FootballPositions position, int shirt) {
        FootballAttributes attributes = position == FootballPositions.GK
                ? new FootballAttributes(position, 70, 70, 70, 70)
                : new FootballAttributes(position, 70, 70, 70, 70, 70);
        return new FootballPlayer("P" + shirt, 22, Gender.MALE, shirt, position, attributes);
    }

    private List<AbstractPlayer> lineup442() {
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
    void factoryMethodsReturnFormationNames() {
        assertEquals("4-4-2", FootballTactic.create442().getFormationString());
        assertEquals("4-2-3-1", FootballTactic.create4231().getFormationString());
        assertEquals("4-3-3", FootballTactic.create433().getFormationString());
        assertEquals("4-2-4", FootballTactic.create424().getFormationString());
        assertEquals("3-5-2", FootballTactic.create352().getFormationString());
        assertEquals("3-4-3", FootballTactic.create343().getFormationString());
        assertEquals("5-3-2", FootballTactic.create532().getFormationString());
        assertEquals("5-4-1", FootballTactic.create541().getFormationString());
    }

    @Test
    void validateForSquadAcceptsMatchingSquad() {
        FootballTactic tactic = FootballTactic.create442();

        assertDoesNotThrow(() -> tactic.validateForSquad(lineup442()));
        assertEquals(0, tactic.countMissingPositions(lineup442()));
    }

    @Test
    void validateForSquadRejectsMissingRequiredPosition() {
        FootballTactic tactic = FootballTactic.create442();
        List<AbstractPlayer> noLeftBack = List.of(
                player(FootballPositions.GK, 1),
                player(FootballPositions.CB, 2), player(FootballPositions.CB, 3),
                player(FootballPositions.CB, 4), player(FootballPositions.RB, 5),
                player(FootballPositions.CM, 6), player(FootballPositions.CM, 7),
                player(FootballPositions.LW, 8), player(FootballPositions.RW, 9),
                player(FootballPositions.ST, 10), player(FootballPositions.ST, 11)
        );

        assertTrue(tactic.countMissingPositions(noLeftBack) > 0);
        assertThrows(IllegalArgumentException.class, () -> tactic.validateForSquad(noLeftBack));
    }

    @Test
    void injuredPlayersDoNotCountForTacticRequirements() {
        FootballTactic tactic = FootballTactic.create442();
        List<AbstractPlayer> lineup = lineup442();
        lineup.get(1).setInjury(new Injury(Injury.Severity.MINOR, 2));

        assertEquals(1, tactic.countMissingPositions(lineup));
        assertThrows(IllegalArgumentException.class, () -> tactic.validateForSquad(lineup));
    }
}
