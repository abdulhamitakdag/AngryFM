package com.sportsmanager.core.model;

import com.sportsmanager.sport.football.FootballAttributes;
import com.sportsmanager.sport.football.FootballPlayer;
import com.sportsmanager.sport.football.FootballPositions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestFootballPlayer extends BaseTest {

    private FootballPlayer createPlayer(int age, FootballPositions position) {
        FootballAttributes attrs = (position == FootballPositions.GK)
                ? new FootballAttributes(position, 70, 70, 70, 70)
                : new FootballAttributes(position, 70, 70, 70, 70, 70);
        return new FootballPlayer("Player", age, Gender.MALE, 7, position, attrs);
    }

    @Test
    void positionIsStoredAsEnumAndString() {
        FootballPlayer player = createPlayer(22, FootballPositions.GK);

        assertEquals(FootballPositions.GK, player.getFootballPosition());
        assertEquals("GK", player.getPosition());
    }

    @Test
    void youngHealthyPlayerHasHigherTrainingEffectiveness() {
        FootballPlayer player = createPlayer(22, FootballPositions.ST);

        assertEquals(1.3, player.getTrainingEffectiveness(), 0.001);
    }

    @Test
    void olderHealthyPlayerHasLowerTrainingEffectiveness() {
        FootballPlayer player = createPlayer(34, FootballPositions.CB);

        assertEquals(0.7, player.getTrainingEffectiveness(), 0.001);
    }

    @Test
    void injuredPlayerHasZeroTrainingEffectiveness() {
        FootballPlayer player = createPlayer(22, FootballPositions.CM);
        player.setInjury(new Injury(Injury.Severity.MINOR, 2));

        assertEquals(0.0, player.getTrainingEffectiveness(), 0.001);
    }

    @Test
    void overallRatingIsWithinRange() {
        FootballPlayer player = createPlayer(22, FootballPositions.LW);

        int overall = player.getAttributes().computeOverallRating();

        assertWithinRange(overall, 0, 100, "overall");
    }
}