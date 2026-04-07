package com.sportsmanager.core.model;

import com.sportsmanager.sport.football.FootballCoach;
import org.junit.jupiter.api.Test;

import static com.sportsmanager.util.RandomGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestFootballCoach extends BaseTest {

    @Test
    void specialtyMultiplierAttackingCoachWithStrikerIsPositive() {
        FootballCoach coach = generateMaleCoach();
        AbstractPlayer player = generateMalePlayer(DEFAULT_SPORT);

        double multiplier = coach.specialtyMultiplier(player);

        assertTrue(multiplier > 0.0);
    }

    @Test
    void specialtyMultiplierReturnsReasonableValue() {
        FootballCoach coach = generateMaleCoach();
        AbstractPlayer player = generateMalePlayer(DEFAULT_SPORT);

        double multiplier = coach.specialtyMultiplier(player);

        assertTrue(multiplier >= 1.0);
    }

    @Test
    void coachingLevelIsWithinValidRange() {
        FootballCoach coach = generateMaleCoach();
        assertWithinRange(coach.getCoachingLevel(), 1, 5, "coachingLevel");
    }
}