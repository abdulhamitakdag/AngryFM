package com.sportsmanager.core.model;

import com.sportsmanager.core.interfaces.IMatchEngine;
import com.sportsmanager.sport.football.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestFootballSport extends BaseTest{
    @Test
    void basicSportRulesAreReturnedCorrectly() {
        FootballSport sport = new FootballSport();

        assertEquals("football", sport.getSportId());
        assertEquals(11, sport.getPlayersOnField());
        assertEquals(3, sport.getWinPoints());
        assertTrue(sport.allowsDraw());
        assertEquals(23, sport.getRecommendedSquadSize());
        assertEquals(3, sport.getRecommendedCoachCount());
    }

    @Test
    void factoryMethodsCreateFootballObjects() {
        FootballSport sport = new FootballSport();
        AbstractPlayerAttributes attributes = sport.generateRandomAttributes("ST");

        assertInstanceOf(FootballLeague.class, sport.createLeague("League"));
        assertInstanceOf(FootballTeam.class, sport.createTeam("Team"));
        assertInstanceOf(FootballCoach.class, sport.createCoach("Coach", 40, Gender.MALE, CoachSpecialty.GENERAL, 3));
        assertInstanceOf(FootballTactic.class, sport.createDefaultTactic());
        assertInstanceOf(FootballPlayer.class, sport.createPlayer("Don Kişot", 22, Gender.MALE, 9, "ST", attributes));
    }

    @Test
    void createMatchEngineReturnsFootballMatchEngine() {
        FootballSport sport = new FootballSport();

        IMatchEngine engine = sport.createMatchEngine();

        assertInstanceOf(FootballMatchEngine.class, engine);
    }

    @Test
    void validPositionsContainExpectedFootballPositions() {
        FootballSport sport = new FootballSport();

        assertTrue(sport.getValidPositions().contains("GK"));
        assertTrue(sport.getValidPositions().contains("ST"));
        assertEquals(FootballPositions.values().length, sport.getValidPositions().size());
    }

    @Test
    void generateRandomAttributesRejectsUnknownPosition() {
        FootballSport sport = new FootballSport();

        assertThrows(IllegalArgumentException.class, () -> sport.generateRandomAttributes("NOT_A_POSITION"));
    }
}
