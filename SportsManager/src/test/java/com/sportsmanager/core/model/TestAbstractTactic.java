package com.sportsmanager.core.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestAbstractTactic extends BaseTest {

    static class DummyTactic extends AbstractTactic {

        public DummyTactic(String name, double attackingWeight, double pressureIntensity) {
            super(name, attackingWeight, pressureIntensity);
        }

        @Override
        public String getFormationString() {
            return "4-4-2";
        }

        @Override
        public void validateForSquad(List<AbstractPlayer> squad) {
            if (squad == null) {
                throw new IllegalArgumentException("Squad cannot be null");
            }
        }
    }

    @Test
    void constructorStoresNameCorrectly() {
        AbstractTactic tactic = new DummyTactic("Test", 0.5, 0.5);

        assertEquals("Test", tactic.getName());
    }

    @Test
    void attackingWeightIsClampedWhenBelow0() {
        AbstractTactic tactic = new DummyTactic("Test", -1.0, 0.5);

        assertEquals(0.0, tactic.getAttackingWeight());
    }

    @Test
    void attackingWeightIsClampedWhenAbove1() {
        AbstractTactic tactic = new DummyTactic("Test", 2.0, 0.5);

        assertEquals(1.0, tactic.getAttackingWeight());
    }

    @Test
    void defensiveWeightIsCalculatedAsOneMinusAttackingWeight() {
        AbstractTactic tactic = new DummyTactic("Test", 0.7, 0.5);

        assertEquals(0.3, tactic.getDefensiveWeight());
    }

    @Test
    void pressureIntensityIsClampedWhenBelow0() {
        AbstractTactic tactic = new DummyTactic("Test", 0.5, -2.0);

        assertEquals(0.0, tactic.getPressureIntensity());
    }

    @Test
    void pressureIntensityIsClampedWhenAbove1() {
        AbstractTactic tactic = new DummyTactic("Test", 0.5, 2.0);

        assertEquals(1.0, tactic.getPressureIntensity());
    }

    @Test
    void offensiveModifierIsCalculatedCorrectly() {
        AbstractTactic tactic = new DummyTactic("Test", 0.5, 0.5);

        // 0.80 + (0.5 * 0.40) = 1.0
        assertEquals(1.0, tactic.getOffensiveModifier());
    }

    @Test
    void defensiveModifierIsCalculatedCorrectly() {
        AbstractTactic tactic = new DummyTactic("Test", 0.5, 0.5);

        // defensiveWeight = 0.5 → 0.80 + (0.5 * 0.40) = 1.0
        assertEquals(1.0, tactic.getDefensiveModifier());
    }

    @Test
    void pressureModifierIsCalculatedCorrectly() {
        AbstractTactic tactic = new DummyTactic("Test", 0.5, 0.5);

        // 0.90 + (0.5 * 0.20) = 1.0
        assertEquals(1.0, tactic.getPressureModifier());
    }

    @Test
    void setAttackingWeightUpdatesBothAttackAndDefense() {
        AbstractTactic tactic = new DummyTactic("Test", 0.5, 0.5);

        tactic.setAttackingWeight(0.8);

        assertEquals(0.8, tactic.getAttackingWeight());
        assertEquals(0.2, tactic.getDefensiveWeight());
    }

    @Test
    void setAttackingWeightClampsValue() {
        AbstractTactic tactic = new DummyTactic("Test", 0.5, 0.5);

        tactic.setAttackingWeight(2.0);

        assertEquals(1.0, tactic.getAttackingWeight());
        assertEquals(0.0, tactic.getDefensiveWeight());
    }

    @Test
    void setPressureIntensityUpdatesValue() {
        AbstractTactic tactic = new DummyTactic("Test", 0.5, 0.5);

        tactic.setPressureIntensity(0.8);

        assertEquals(0.8, tactic.getPressureIntensity());
    }

    @Test
    void setPressureIntensityClampsValue() {
        AbstractTactic tactic = new DummyTactic("Test", 0.5, 0.5);

        tactic.setPressureIntensity(-5.0);

        assertEquals(0.0, tactic.getPressureIntensity());
    }

    @Test
    void setNameUpdatesName() {
        AbstractTactic tactic = new DummyTactic("Old", 0.5, 0.5);

        tactic.setName("New");

        assertEquals("New", tactic.getName());
    }

    @Test
    void clampReturnsMinWhenBelow0() {
        double result = AbstractTactic.clamp(-5.0);

        assertEquals(0.0, result);
    }

    @Test
    void clampReturnsMaxWhenAbove1() {
        double result = AbstractTactic.clamp(5.0);

        assertEquals(1.0, result);
    }

    @Test
    void clampReturnsValueWhenWithinRange() {
        double result = AbstractTactic.clamp(0.6);

        assertEquals(0.6, result);
    }

    @Test
    void getFormationStringReturnsDummyValue() {
        AbstractTactic tactic = new DummyTactic("Test", 0.5, 0.5);

        assertEquals("4-4-2", tactic.getFormationString());
    }

    @Test
    void validateForSquadThrowsExceptionWhenNull() {
        AbstractTactic tactic = new DummyTactic("Test", 0.5, 0.5);

        assertThrows(IllegalArgumentException.class, () ->
                tactic.validateForSquad(null));
    }

    @Test
    void validateForSquadDoesNotThrowWhenValid() {
        AbstractTactic tactic = new DummyTactic("Test", 0.5, 0.5);

        assertDoesNotThrow(() ->
                tactic.validateForSquad(List.of()));
    }

    @Test
    void toStringContainsNameAndFormation() {
        AbstractTactic tactic = new DummyTactic("Test", 0.5, 0.5);

        String text = tactic.toString();

        assertTrue(text.contains("Test"));
        assertTrue(text.contains("4-4-2"));
    }
}