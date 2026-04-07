package com.sportsmanager.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestAbstractPlayer extends BaseTest {

    static class DummyAttributes extends AbstractPlayerAttributes {
        private double totalBoost = 0.0;

        @Override
        public int computeOverallRating() {
            return (int) totalBoost;
        }

        @Override
        public void applyTrainingBoost(double intensity) {
            totalBoost += intensity;
        }

        public double getTotalBoost() {
            return totalBoost;
        }
    }

    static class DummyPlayer extends AbstractPlayer {

        public DummyPlayer(String name, int age, Gender gender, int shirtNumber, AbstractPlayerAttributes attributes) {
            super(name, age, gender, shirtNumber, attributes);
        }

        @Override
        public String getPosition() {
            return "TEST";
        }

        @Override
        public double getTrainingEffectiveness() {
            return 1.0;
        }
    }

    @Test
    void constructorStoresShirtNumberCorrectly_Male() {
        AbstractPlayer player = new DummyPlayer("Ahmet", 20, Gender.MALE, 10, new DummyAttributes());
        assertEquals(10, player.getShirtNumber());
    }

    @Test
    void constructorStoresShirtNumberCorrectly_Female() {
        AbstractPlayer player = new DummyPlayer("Ayşe", 20, Gender.FEMALE, 11, new DummyAttributes());
        assertEquals(11, player.getShirtNumber());
    }

    @Test
    void constructorStoresAttributesCorrectly_Male() {
        AbstractPlayerAttributes attributes = new DummyAttributes();
        AbstractPlayer player = new DummyPlayer("Ahmet", 20, Gender.MALE, 10, attributes);
        assertEquals(attributes, player.getAttributes());
    }

    @Test
    void constructorStoresAttributesCorrectly_Female() {
        AbstractPlayerAttributes attributes = new DummyAttributes();
        AbstractPlayer player = new DummyPlayer("Ayşe", 20, Gender.FEMALE, 11, attributes);
        assertEquals(attributes, player.getAttributes());
    }

    @Test
    void getPositionReturnsTest_Male() {
        AbstractPlayer player = new DummyPlayer("Ahmet", 20, Gender.MALE, 10, new DummyAttributes());
        assertEquals("TEST", player.getPosition());
    }

    @Test
    void getPositionReturnsTest_Female() {
        AbstractPlayer player = new DummyPlayer("Ayşe", 20, Gender.FEMALE, 11, new DummyAttributes());
        assertEquals("TEST", player.getPosition());
    }

    @Test
    void setInjuryStoresInjury_Male() {
        AbstractPlayer player = new DummyPlayer("Ahmet", 20, Gender.MALE, 10, new DummyAttributes());
        Injury injury = new Injury(Injury.Severity.MODERATE, 3);

        player.setInjury(injury);

        assertEquals(injury, player.getInjury());
    }

    @Test
    void setInjuryStoresInjury_Female() {
        AbstractPlayer player = new DummyPlayer("Ayşe", 20, Gender.FEMALE, 11, new DummyAttributes());
        Injury injury = new Injury(Injury.Severity.MODERATE, 3);

        player.setInjury(injury);

        assertEquals(injury, player.getInjury());
    }

    @Test
    void isInjuredReturnsFalseWhenNoInjury_Male() {
        AbstractPlayer player = new DummyPlayer("Ahmet", 20, Gender.MALE, 10, new DummyAttributes());
        assertFalse(player.isInjured());
    }

    @Test
    void isInjuredReturnsFalseWhenNoInjury_Female() {
        AbstractPlayer player = new DummyPlayer("Ayşe", 20, Gender.FEMALE, 11, new DummyAttributes());
        assertFalse(player.isInjured());
    }

    @Test
    void isInjuredReturnsTrueWhenPlayerHasActiveInjury_Male() {
        AbstractPlayer player = new DummyPlayer("Ahmet", 20, Gender.MALE, 10, new DummyAttributes());
        player.setInjury(new Injury(Injury.Severity.MINOR, 2));

        assertTrue(player.isInjured());
    }

    @Test
    void isInjuredReturnsTrueWhenPlayerHasActiveInjury_Female() {
        AbstractPlayer player = new DummyPlayer("Ayşe", 20, Gender.FEMALE, 11, new DummyAttributes());
        player.setInjury(new Injury(Injury.Severity.MINOR, 2));

        assertTrue(player.isInjured());
    }

    @Test
    void decrementInjuryReducesGamesRemaining_Male() {
        AbstractPlayer player = new DummyPlayer("Ahmet", 20, Gender.MALE, 10, new DummyAttributes());
        player.setInjury(new Injury(Injury.Severity.MINOR, 2));

        player.decrementInjury();

        assertNotNull(player.getInjury());
        assertEquals(1, player.getInjury().getGamesRemaining());
    }

    @Test
    void decrementInjuryReducesGamesRemaining_Female() {
        AbstractPlayer player = new DummyPlayer("Ayşe", 20, Gender.FEMALE, 11, new DummyAttributes());
        player.setInjury(new Injury(Injury.Severity.MINOR, 2));

        player.decrementInjury();

        assertNotNull(player.getInjury());
        assertEquals(1, player.getInjury().getGamesRemaining());
    }

    @Test
    void decrementInjuryClearsInjuryWhenGamesRemainingIs0_Male() {
        AbstractPlayer player = new DummyPlayer("Ahmet", 20, Gender.MALE, 10, new DummyAttributes());
        player.setInjury(new Injury(Injury.Severity.MINOR, 1));

        player.decrementInjury();

        assertNull(player.getInjury());
    }

    @Test
    void decrementInjuryClearsInjuryWhenGamesRemainingIs0_Female() {
        AbstractPlayer player = new DummyPlayer("Ayşe", 20, Gender.FEMALE, 11, new DummyAttributes());
        player.setInjury(new Injury(Injury.Severity.MINOR, 1));

        player.decrementInjury();

        assertNull(player.getInjury());
    }

    @Test
    void decrementInjuryDoesNothingWhenPlayerHasNoInjury_Male() {
        AbstractPlayer player = new DummyPlayer("Ahmet", 20, Gender.MALE, 10, new DummyAttributes());

        player.decrementInjury();

        assertNull(player.getInjury());
    }

    @Test
    void decrementInjuryDoesNothingWhenPlayerHasNoInjury_Female() {
        AbstractPlayer player = new DummyPlayer("Ayşe", 20, Gender.FEMALE, 11, new DummyAttributes());

        player.decrementInjury();

        assertNull(player.getInjury());
    }

    @Test
    void noTrainWhenPlayerIsInjured_Male() {
        DummyAttributes attributes = new DummyAttributes();
        AbstractPlayer player = new DummyPlayer("Ahmet", 20, Gender.MALE, 10, attributes);
        player.setInjury(new Injury(Injury.Severity.MODERATE, 3));

        player.train(2.0);

        assertEquals(0.0, attributes.getTotalBoost());
    }

    @Test
    void noTrainWhenPlayerIsInjured_Female() {
        DummyAttributes attributes = new DummyAttributes();
        AbstractPlayer player = new DummyPlayer("Ayşe", 20, Gender.FEMALE, 11, attributes);
        player.setInjury(new Injury(Injury.Severity.MODERATE, 3));

        player.train(2.0);

        assertEquals(0.0, attributes.getTotalBoost());
    }

    @Test
    void trainAppliesBoostWhenPlayerIsHealthy_Male() {
        DummyAttributes attributes = new DummyAttributes();
        AbstractPlayer player = new DummyPlayer("Ahmet", 20, Gender.MALE, 10, attributes);

        player.train(2.0);

        assertEquals(2.0, attributes.getTotalBoost());
    }

    @Test
    void trainAppliesBoostWhenPlayerIsHealthy_Female() {
        DummyAttributes attributes = new DummyAttributes();
        AbstractPlayer player = new DummyPlayer("Ayşe", 20, Gender.FEMALE, 11, attributes);

        player.train(2.0);

        assertEquals(2.0, attributes.getTotalBoost());
    }

    @Test
    void trainDoesNothingWhenAttributesAreNull_Male() {
        AbstractPlayer player = new DummyPlayer("Ahmet", 20, Gender.MALE, 10, null);

        assertDoesNotThrow(() -> player.train(2.0));
    }

    @Test
    void trainDoesNothingWhenAttributesAreNull_Female() {
        AbstractPlayer player = new DummyPlayer("Ayşe", 20, Gender.FEMALE, 11, null);

        assertDoesNotThrow(() -> player.train(2.0));
    }
}