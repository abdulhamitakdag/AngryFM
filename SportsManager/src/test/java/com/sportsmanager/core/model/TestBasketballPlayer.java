package com.sportsmanager.core.model;

import com.sportsmanager.sport.basketball.BasketballAttributes;
import com.sportsmanager.sport.basketball.BasketballPlayer;
import com.sportsmanager.sport.basketball.BasketballPositions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestBasketballPlayer extends BaseTest{
    private BasketballPlayer createPlayer(int age, BasketballPositions position) {
        return new BasketballPlayer("Player", age, Gender.MALE, 7, position,
                new BasketballAttributes(position, 70, 70, 70, 70, 70));
    }

    @Test
    void positionIsStoredAsEnumAndString() {
        BasketballPlayer player = createPlayer(22, BasketballPositions.PG);

        assertEquals(BasketballPositions.PG, player.getBasketballPosition());
        assertEquals("PG", player.getPosition());
    }

    @Test
    void youngHealthyPlayerHasHigherTrainingEffectiveness() {
        BasketballPlayer player = createPlayer(22, BasketballPositions.SG);

        assertEquals(1.3, player.getTrainingEffectiveness(), 0.001);
    }

    @Test
    void olderHealthyPlayerHasLowerTrainingEffectiveness() {
        BasketballPlayer player = createPlayer(34, BasketballPositions.C);

        assertEquals(0.7, player.getTrainingEffectiveness(), 0.001);
    }

    @Test
    void injuredPlayerHasZeroTrainingEffectiveness() {
        BasketballPlayer player = createPlayer(22, BasketballPositions.PF);
        player.setInjury(new Injury(Injury.Severity.MINOR, 2));

        assertEquals(0.0, player.getTrainingEffectiveness(), 0.001);
    }
}