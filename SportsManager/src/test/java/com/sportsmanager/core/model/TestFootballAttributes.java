package com.sportsmanager.core.model;

import com.sportsmanager.core.model.BaseTest;
import com.sportsmanager.sport.football.FootballAttributes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestFootballAttributes extends BaseTest {

    @Test
    void computeOverallRatingReturnsValueWithinRange() {
        FootballAttributes attributes = new FootballAttributes(70, 75, 80, 65, 78);
        int overall = attributes.computeOverallRating();
        assertWithinRange(overall, 0, 100, "overall");
    }

    @Test
    void applyTrainingBoostDoesNotPushAttributesAbove100() {
        FootballAttributes attributes = new FootballAttributes(99, 99, 99, 99, 99);

        attributes.applyTrainingBoost(10.0);

        assertTrue(attributes.getPace() <= 100);
        assertTrue(attributes.getShooting() <= 100);
        assertTrue(attributes.getPassing() <= 100);
        assertTrue(attributes.getDefending() <= 100);
        assertTrue(attributes.getPhysical() <= 100);
    }

    @Test
    void applyTrainingBoostImprovesOrKeepsOverallRating() {
        FootballAttributes attributes = new FootballAttributes(60, 60, 60, 60, 60);
        int before = attributes.computeOverallRating();

        attributes.applyTrainingBoost(2.0);

        int after = attributes.computeOverallRating();
        assertTrue(after >= before);
    }

    @Test
    void constructorClampsValuesBelow0To0() {
        FootballAttributes attributes = new FootballAttributes(-10, -5, 20, 30, 40);

        assertTrue(attributes.getPace() >= 0);
        assertTrue(attributes.getShooting() >= 0);
    }

    @Test
    void constructorClampsValuesAbove100To100() {
        FootballAttributes attributes = new FootballAttributes(120, 140, 100, 150, 200);

        assertTrue(attributes.getPace() <= 100);
        assertTrue(attributes.getShooting() <= 100);
        assertTrue(attributes.getDefending() <= 100);
        assertTrue(attributes.getPhysical() <= 100);
    }
}