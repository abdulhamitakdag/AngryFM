package com.sportsmanager.util;

import com.sportsmanager.core.interfaces.ISport;
import com.sportsmanager.core.model.AbstractCoach;
import com.sportsmanager.core.model.AbstractPlayer;
import com.sportsmanager.core.model.AbstractTeam;
import com.sportsmanager.core.model.Gender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestRandomGenerator {

    @Test
    void testGenerateRandomTeamName() {
        String teamName = RandomGenerator.generateRandomTeamName();
        assertNotNull(teamName, "Team name should not be null");
        assertFalse(teamName.isEmpty(), "Team name should not be empty");
    }

    @Test
    void testGenerateRandomFullMaleName() {
        String name = RandomGenerator.generateRandomFullMaleName();
        assertNotNull(name, "Male name should not be null");
        assertTrue(name.contains(" "), "Male name should contain a space separating first name and surname");
    }

    @Test
    void testGenerateRandomFullFemaleName() {
        String name = RandomGenerator.generateRandomFullFemaleName();
        assertNotNull(name, "Female name should not be null");
        assertTrue(name.contains(" "), "Female name should contain a space separating first name and surname");
    }

    @Test
    void testGenerateMalePlayer() {
        ISport sport = RandomGenerator.DEFAULT_SPORT;
        AbstractPlayer player = RandomGenerator.generateMalePlayer(sport);
        
        assertNotNull(player, "Generated male player should not be null");
        assertEquals(Gender.MALE, player.getGender(), "Player gender should be MALE");
        assertTrue(player.getAge() >= 15 && player.getAge() <= 50, "Player age should be within typical range");
        assertNotNull(player.getName(), "Player name should not be null");
        assertNotNull(player.getPosition(), "Player position should not be null");
    }

    @Test
    void testGenerateFemalePlayer() {
        ISport sport = RandomGenerator.DEFAULT_SPORT;
        AbstractPlayer player = RandomGenerator.generateFemalePlayer(sport);
        
        assertNotNull(player, "Generated female player should not be null");
        assertEquals(Gender.FEMALE, player.getGender(), "Player gender should be FEMALE");
        assertTrue(player.getAge() >= 15 && player.getAge() <= 50, "Player age should be within typical range");
        assertNotNull(player.getName(), "Player name should not be null");
        assertNotNull(player.getPosition(), "Player position should not be null");
    }

    @Test
    void testGenerateMaleCoach() {
        AbstractCoach coach = RandomGenerator.generateMaleCoach();
        assertNotNull(coach, "Generated male coach should not be null");
        assertEquals(Gender.MALE, coach.getGender(), "Coach gender should be MALE");
    }

    @Test
    void testGenerateFemaleCoach() {
        AbstractCoach coach = RandomGenerator.generateFemaleCoach();
        assertNotNull(coach, "Generated female coach should not be null");
        assertEquals(Gender.FEMALE, coach.getGender(), "Coach gender should be FEMALE");
    }

    @Test
    void testGenerateTeam() {
        ISport sport = RandomGenerator.DEFAULT_SPORT;
        AbstractTeam team = RandomGenerator.generateTeam(sport);
        
        assertNotNull(team, "Generated team should not be null");
        assertNotNull(team.getName(), "Team name should not be null");
        assertEquals(sport.getRecommendedSquadSize(), team.getSquad().size(), "Team should have recommended squad size");
        assertEquals(sport.getRecommendedCoachCount(), team.getCoaches().size(), "Team should have recommended coach count");
    }
}
