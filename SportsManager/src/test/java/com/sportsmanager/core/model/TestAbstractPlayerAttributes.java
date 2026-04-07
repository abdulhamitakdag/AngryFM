package com.sportsmanager.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestAbstractPlayerAttributes extends BaseTest {

    static class DummyAttributes extends AbstractPlayerAttributes {

        private int overall = 50;
        private double lastBoost = 0.0;

        @Override
        public int computeOverallRating() {
            return overall;
        }

        @Override
        public void applyTrainingBoost(double intensity) {
            lastBoost = intensity;
            overall += intensity;
        }

        public double getLastBoost() {
            return lastBoost;
        }
    }

    @Test
    void getOverallRatingReturnsComputeOverallRatingValue() {
        DummyAttributes attributes = new DummyAttributes();

        assertEquals(50, attributes.getOverallRating());
    }

    @Test
    void getOverallRatingReflectsUpdatedValueAfterTraining() {
        DummyAttributes attributes = new DummyAttributes();

        attributes.applyTrainingBoost(10.0);

        assertEquals(60, attributes.getOverallRating());
    }

    @Test
    void limiterReturnsMinWhenValueBelowMin() {
        DummyAttributes attributes = new DummyAttributes();

        double result = attributes.limiter(-5.0, 0.0, 100.0);

        assertEquals(0.0, result);
    }

    @Test
    void limiterReturnsMaxWhenValueAboveMax() {
        DummyAttributes attributes = new DummyAttributes();

        double result = attributes.limiter(150.0, 0.0, 100.0);

        assertEquals(100.0, result);
    }

    @Test
    void limiterReturnsValueWhenWithinRange() {
        DummyAttributes attributes = new DummyAttributes();

        double result = attributes.limiter(75.0, 0.0, 100.0);

        assertEquals(75.0, result);
    }

    @Test
    void applyTrainingBoostStoresLastBoostValue() {
        DummyAttributes attributes = new DummyAttributes();

        attributes.applyTrainingBoost(5.5);

        assertEquals(5.5, attributes.getLastBoost());
    }

    @Test
    void computeOverallRatingReturnsUpdatedValueAfterMultipleBoosts() {
        DummyAttributes attributes = new DummyAttributes();

        attributes.applyTrainingBoost(5.0);
        attributes.applyTrainingBoost(5.0);

        assertEquals(60, attributes.getOverallRating());
    }
}