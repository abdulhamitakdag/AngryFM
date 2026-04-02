package com.sportsmanager.core.model;

import org.junit.jupiter.api.Test;

import java.util.Random;
import static com.sportsmanager.core.model.TestObjFactory.generateTestFemmePlayer;
import static com.sportsmanager.core.model.TestObjFactory.generateTestMascPlayer;
import static org.junit.jupiter.api.Assertions.*;

public class TestPlayer extends com.sportsmanager.core.model.BaseTest {
    static class AbstractPlayerStub extends AbstractPlayer {
        public AbstractPlayerStub(String name, int age, Gender gender) {
            super(name, age, gender, "N/A", 0, null);
        }

        @Override
        public double getTrainingEffectiveness() {
            return 0;
        }

        @Test
        void testInjuredFemmePlayerNoTrain() {
            AbstractPlayer player = generateTestFemmePlayer();
            com.sportsmanager.core.model.Injury injury = new com.sportsmanager.core.model.Injury(
                    com.sportsmanager.core.model.Injury.Severity.MODERATE, 3);
            player.setInjury(injury);
            /* there is no method of setting injury in the AbstractPlayer yet. */
            player.train(1.0);
            assertTrue(player.getInjury() != null, "Player is injured!");
            assertTrue(player.getInjury().getGamesRemaining() > 0, "Player is not recovered from her injury yet!");
            // getter ve setterlar Injury için yazılmamış, yazılınca burası çalışmalı
            // player recovery sistemini oturtamadım
            // veya nasıl bir getter/setter adı verildiyse ona göre değiştirilmeli
        }

        @Test
        void testInjuredMascPlayerNoTrain() {
            AbstractPlayer player = generateTestMascPlayer();
            com.sportsmanager.core.model.Injury injury = new com.sportsmanager.core.model.Injury(
                    com.sportsmanager.core.model.Injury.Severity.MODERATE, 3);
            player.setInjury(injury);
            /* there is no method of setting injury in the AbstractPlayer yet. */
            player.train(1.0);
            assertTrue(player.getInjury() != null, "Player is injured!");
            assertTrue(player.getInjury().getGamesRemaining() > 0, "Player is not recovered from his injury yet!");
            // getter ve setterlar Injury için yazılmamış, yazılınca burası çalışmalı
            // veya nasıl bir getter/setter adı verildiyse ona göre değiştirilmeli
        }
    }
}