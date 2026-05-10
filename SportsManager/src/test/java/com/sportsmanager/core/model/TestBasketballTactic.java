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

        assertWithinRange(tactic.getOffensiveModifier(), 0.8, 1.2, "offensive modifier");
        assertWithinRange(tactic.getDefensiveModifier(), 0.8, 1.2, "defensive modifier");
        assertWithinRange(tactic.getPressureModifier(), 0.9, 1.1, "pressure modifier");
    }

    @Test
    void validateForSquadCurrentlyAllowsAnySquad() {
        BasketballTactic tactic = BasketballTactic.createBalanced();

        assertDoesNotThrow(() -> tactic.validateForSquad(Collections.emptyList()));
    }
}
