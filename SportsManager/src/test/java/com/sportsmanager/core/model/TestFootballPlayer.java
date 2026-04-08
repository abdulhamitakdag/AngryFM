package com.sportsmanager.core.model;


import com.sportsmanager.sport.football.FootballAttributes;
import org.junit.jupiter.api.Test;

import static com.sportsmanager.util.RandomGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestFootballPlayer extends BaseTest {

    @Test
    void validPositionIsStoredCorrectly_Male() {
        AbstractPlayer player = generateMalePlayer(DEFAULT_SPORT);
        assertNotNull(player.getPosition());
    }

    @Test
    void validPositionIsStoredCorrectly_Female() {
        AbstractPlayer player = generateFemalePlayer(DEFAULT_SPORT);
        assertNotNull(player.getPosition());
    }

    @Test
    void invalidPositionThrowsException_Male() {
        assertThrows(IllegalArgumentException.class, () ->
                new com.sportsmanager.sport.football.FootballPlayer("John", 22, com.sportsmanager.core.model.Gender.MALE,
                        10, com.sportsmanager.sport.football.FootballPositions.GK, new FootballAttributes(com.sportsmanager.sport.football.FootballPositions.GK, 70, 70, 70, 70, 70)));
    }

    @Test
    void invalidPositionThrowsException_Female() {
        assertThrows(IllegalArgumentException.class, () ->
                new com.sportsmanager.sport.football.FootballPlayer("Jane", 22, com.sportsmanager.core.model.Gender.FEMALE,
                        10, com.sportsmanager.sport.football.FootballPositions.GK, new FootballAttributes(com.sportsmanager.sport.football.FootballPositions.GK, 70, 70, 70, 70, 70)));
    }

    @Test
    void overallRatingIsWithinRange_Male() {
        AbstractPlayer player = generateMalePlayer(DEFAULT_SPORT);
        int overall = player.getAttributes().computeOverallRating();
        assertWithinRange(overall, 0, 100, "overall");
    }

    @Test
    void overallRatingIsWithinRange_Female() {
        AbstractPlayer player = generateFemalePlayer(DEFAULT_SPORT);
        int overall = player.getAttributes().computeOverallRating();
        assertWithinRange(overall, 0, 100, "overall");
    }
}