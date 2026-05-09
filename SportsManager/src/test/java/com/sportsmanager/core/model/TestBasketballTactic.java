package com.sportsmanager.core.model;

import com.sportsmanager.sport.basketball.BasketballTactic;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class TestBasketballTactic extends BaseTest {

    @Test
    void factoryMethodsReturnExpectedFormationNames() {
        assertEquals("1-2-2", BasketballTactic.createStandard().getFormationString());
        assertEquals("2-1-2", BasketballTactic.createZone().getFormationString());
        assertEquals("1-3-1", BasketballTactic.createTrap().getFormationString());
        assertEquals("2-3", BasketballTactic.createBig().getFormationString());
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
        BasketballTactic tactic = BasketballTactic.createStandard();

        assertDoesNotThrow(() -> tactic.validateForSquad(Collections.emptyList()));
    }
}
