package com.sportsmanager.core.model;

import com.sportsmanager.sport.football.FootballCoach;
import org.junit.jupiter.api.Test;

import static com.sportsmanager.util.RandomGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestFootballCoach extends BaseTest {

    @Test
    void specialtyMultiplierAttackingCoachWithStrikerIsPositive() {
        FootballCoach coach = (FootballCoach) generateMaleCoach();

        double multiplier = coach.specialtyMultiplier();

        assertTrue(multiplier > 0.0);
    }

    @Test
    void specialtyMultiplierReturnsReasonableValue() {
        FootballCoach coach = (FootballCoach) generateMaleCoach();

        double multiplier = coach.specialtyMultiplier();

        assertTrue(multiplier >= 1.0);
    }

    @Test
    void coachingLevelIsWithinValidRange() {
        FootballCoach coach = (FootballCoach) generateMaleCoach();
        assertWithinRange(coach.getCoachingLevel(), 1, 5, "coachingLevel");
    }
}