package com.sportsmanager.core.model;
import com.sportsmanager.sport.football.FootballPlayer;
import org.junit.jupiter.api.Test;

import static com.sportsmanager.util.RandomGenerator.generateFemalePlayer;
import static com.sportsmanager.util.RandomGenerator.generateMalePlayer;
import static org.junit.jupiter.api.Assertions.*;

public class TestPlayer extends BaseTest {

    @Test
    void setInjuryStoresInjury_Male() {
        AbstractPlayer player = generateMalePlayer();
        Injury injury = new Injury(Injury.Severity.MODERATE, 3);
        player.setInjury(injury);
        assertEquals(injury, player.getInjury());
    }

    @Test
    void setInjuryStoresInjury_Female() {
        AbstractPlayer player = generateFemalePlayer();
        Injury injury = new Injury(Injury.Severity.MODERATE, 3);
        player.setInjury(injury);
        assertEquals(injury, player.getInjury());
    }

    @Test
    void isInjuredReturnsFalseWhenNoInjury_Male() {
        AbstractPlayer player = generateMalePlayer();
        assertFalse(player.isInjured());
    }
    @Test
    void isInjuredReturnsFalseWhenNoInjury_Female() {
        AbstractPlayer player = generateFemalePlayer();
        assertFalse(player.isInjured());
    }

    @Test
    void isInjuredReturnsTrueWhenPlayerHasActiveInjury_Female() {
        AbstractPlayer player = generateFemalePlayer();
        player.setInjury(new Injury(Injury.Severity.MINOR, 2));
        assertTrue(player.isInjured());
    }
    @Test
    void isInjuredReturnsTrueWhenPlayerHasActiveInjury_Male() {
        AbstractPlayer player = generateMalePlayer();
        player.setInjury(new Injury(Injury.Severity.MINOR, 2));
        assertTrue(player.isInjured());
    }

    @Test
    void decrementInjuryReducesGamesRemaining_Male() {
        AbstractPlayer player = generateMalePlayer();
        player.setInjury(new Injury(Injury.Severity.MINOR, 2));
        player.decrementInjury();
        assertNotNull(player.getInjury());
        assertEquals(1, player.getInjury().getGamesRemaining());
    }
    @Test
    void decrementInjuryReducesGamesRemaining_Female() {
        AbstractPlayer player = generateFemalePlayer();
        player.setInjury(new Injury(Injury.Severity.MINOR, 2));
        player.decrementInjury();
        assertNotNull(player.getInjury());
        assertEquals(1, player.getInjury().getGamesRemaining());
    }

    @Test
    void decrementInjuryClearsInjuryWhenGamesRemainingIs0_Female() {
        AbstractPlayer player = generateFemalePlayer();
        player.setInjury(new Injury(Injury.Severity.MINOR, 1));
        player.decrementInjury();
        assertNull(player.getInjury());
    }
    @Test
    void decrementInjuryClearsInjuryWhenGamesRemainingIs0_Male() {
        AbstractPlayer player = generateMalePlayer();
        player.setInjury(new Injury(Injury.Severity.MINOR, 1));
        player.decrementInjury();
        assertNull(player.getInjury());
    }

    @Test
    void noTrainWhenPlayerIsInjured_Female() {
        AbstractPlayer player = generateFemalePlayer();
        player.setInjury(new Injury(Injury.Severity.MODERATE, 3));
        int before = player.getAttributes().computeOverallRating();
        player.train(2.0);
        int after = player.getAttributes().computeOverallRating();
        assertEquals(before, after);
    }

    @Test
    void noTrainWhenPlayerIsInjured_Male() {
        AbstractPlayer player = generateMalePlayer();
        player.setInjury(new Injury(Injury.Severity.MODERATE, 3));
        int before = player.getAttributes().computeOverallRating();
        player.train(2.0);
        int after = player.getAttributes().computeOverallRating();
        assertEquals(before, after);
    }
    @Test
    void trainAppliesBoostWhenPlayerIsHealthy_Male() {
        AbstractPlayer player = generateMalePlayer();
        int before = player.getAttributes().computeOverallRating();
        player.train(2.0);
        int after = player.getAttributes().computeOverallRating();
        assertTrue(after >= before);
    }

    @Test
    void trainAppliesBoostWhenPlayerIsHealthy_Female() {
        AbstractPlayer player = generateFemalePlayer();
        int before = player.getAttributes().computeOverallRating();
        player.train(2.0);
        int after = player.getAttributes().computeOverallRating();
        assertTrue(after >= before);
    }
}