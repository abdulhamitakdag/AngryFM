package com.sportsmanager.core.model;

import com.sportsmanager.sport.basketball.BasketballTactic;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class TestBasketballTactic extends BaseTest {

    @Test
    void factoryMethodsReturnExpectedFormationNames() {
        assertEquals("Balanced", BasketballTactic.createBalanced().getFormationString());
        assertEquals("Offensive", BasketballTactic.createOffensive().getFormationString());
        assertEquals("Defensive", BasketballTactic.createDefensive().getFormationString());
    }

    @Test
    void inheritedModifiersStayWithinExpectedRanges() {
        BasketballTactic tactic = new BasketballTactic("Custom", 1.5, -1.0);

        double epsilon = 0.0001;
        assertWithinRange(tactic.getOffensiveModifier(), 0.8 - epsilon, 1.2 + epsilon, "offensive modifier");
        assertWithinRange(tactic.getDefensiveModifier(), 0.8 - epsilon, 1.2 + epsilon, "defensive modifier");
        assertWithinRange(tactic.getPressureModifier(), 0.9 - epsilon, 1.1 + epsilon, "pressure modifier");
    }

    @Test
    void validateForSquadThrowsOnEmptySquad() {
        BasketballTactic tactic = BasketballTactic.createBalanced();

        assertThrows(IllegalArgumentException.class, () -> tactic.validateForSquad(Collections.emptyList()));
    }
}
