package com.sportsmanager.core.model;

import com.sportsmanager.sport.football.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestFootballMatchEngine extends BaseTest{

    private FootballPlayer player(FootballPositions position, int shirt) {
        FootballAttributes attributes = position == FootballPositions.GK
                ? new FootballAttributes(position, 80, 80, 80, 80)
                : new FootballAttributes(position, 80, 80, 80, 80, 80);
        return new FootballPlayer("Pilavşör" + shirt, 22, Gender.MALE, shirt, position, attributes);
    }

    private FootballTeam team(String name) {
        FootballTeam team = new FootballTeam(name);
        for (int i = 1; i <= 11; i++) {
            team.addPlayer(player(i == 1 ? FootballPositions.GK : FootballPositions.ST, i));
        }
        team.setCurrentTactic(FootballTactic.create442());
        return team;
    }

    @Test
    void simulatePeriodReturnsNonNegativeScores() {
        FootballMatchEngine engine = new FootballMatchEngine();

        PeriodResult result = engine.simulatePeriod(team("Home"), team("Away"));

        assertTrue(result.getHomeScore() >= 0);
        assertTrue(result.getAwayScore() >= 0);
    }

    @Test
    void calculatePlayerRatingIsWithinRange() {
        FootballMatchEngine engine = new FootballMatchEngine();
        AbstractPlayer player = player(FootballPositions.ST, 9);

        double rating = engine.calculatePlayerRating(player);

        assertWithinRange(rating, 0, 100, "player rating");
    }

    @Test
    void calculatePlayerRatingUsesDefaultForNullAttributes() {
        FootballMatchEngine engine = new FootballMatchEngine();
        AbstractPlayer player = new FootballPlayer("No Attr", 22, Gender.MALE, 9, FootballPositions.ST, null);

        double rating = engine.calculatePlayerRating(player);

        assertEquals(50.0, rating, 0.001);
    }

    @Test
    void determineInjuriesReturnsListAndDoesNotThrow() {
        FootballMatchEngine engine = new FootballMatchEngine();

        List<Injury> injuries = engine.determineInjuries(team("Home"), team("Away"));

        assertNotNull(injuries);
    }


}
