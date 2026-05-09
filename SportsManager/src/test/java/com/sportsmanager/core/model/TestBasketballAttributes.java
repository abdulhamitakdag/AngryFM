package com.sportsmanager.core.model;

import com.sportsmanager.sport.basketball.BasketballAttributes;
import com.sportsmanager.sport.basketball.BasketballPositions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestBasketballAttributes extends BaseTest {

    @Test
    void constructorStoresPositionAndLimitsStats() {
        BasketballAttributes attributes = new BasketballAttributes(BasketballPositions.PG, 120, -10, 50, 70, 80);

        assertEquals(BasketballPositions.PG, attributes.getPosition());
        assertEquals(100, attributes.getShooting());
        assertEquals(0, attributes.getPlaymaking());
        assertEquals(50, attributes.getDefending());
        assertEquals(70, attributes.getRebounding());
        assertEquals(80, attributes.getPhysical());
    }

    @Test
    void overallRatingIsWithinRangeForEveryPosition() {
        for (BasketballPositions position : BasketballPositions.values()) {
            BasketballAttributes attributes = new BasketballAttributes(position, 75, 75, 75, 75, 75);

            int rating = attributes.computeOverallRating();

            assertWithinRange(rating, 0, 100, position.name() + " rating");
        }
    }

    @Test
    void trainingBoostIncreasesStatsWithoutPassingHundred() {
        BasketballAttributes attributes = new BasketballAttributes(BasketballPositions.C, 99, 99, 99, 99, 99);

        attributes.applyTrainingBoost(10.0);

        assertEquals(100, attributes.getShooting());
        assertEquals(100, attributes.getPlaymaking());
        assertEquals(100, attributes.getDefending());
        assertEquals(100, attributes.getRebounding());
        assertEquals(100, attributes.getPhysical());
    }
}
