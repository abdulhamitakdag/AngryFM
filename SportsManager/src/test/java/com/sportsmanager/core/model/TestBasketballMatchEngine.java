package com.sportsmanager.core.model;

import com.sportsmanager.sport.basketball.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TestBasketballMatchEngine {

    private final BasketballMatchEngine engine = new BasketballMatchEngine();

    @Test
    void quarterScoreIsWithinReasonableRange() {
        BasketballTeam home = buildTeam("Home");
        BasketballTeam away = buildTeam("Away");
        for (int i = 0; i < 50; i++) {
            PeriodResult r = engine.simulatePeriod(home, away);
            assertTrue(r.getHomeScore() >= 5 && r.getHomeScore() <= 50, "Home score out of range: " + r.getHomeScore());
            assertTrue(r.getAwayScore() >= 5 && r.getAwayScore() <= 50, "Away score out of range: " + r.getAwayScore());
        }
    }

    @Test
    void emptySquadDoesNotCrash() {
        BasketballTeam home = new BasketballTeam("Empty1");
        BasketballTeam away = new BasketballTeam("Empty2");
        assertDoesNotThrow(() -> engine.simulatePeriod(home, away));
    }

    @Test
    void playerRatingReturns50ForNullAttributes() {
        BasketballPlayer p = new BasketballPlayer("Test", 25, Gender.MALE, 1, BasketballPositions.PG, null);
        double rating = engine.calculatePlayerRating(p);
        assertEquals(50.0, rating, 0.001);
    }

    @Test
    void determineInjuriesReturnsListNotNull() {
        BasketballTeam home = buildTeam("H");
        BasketballTeam away = buildTeam("A");
        List<Injury> injuries = engine.determineInjuries(home, away);
        assertNotNull(injuries);
    }

    private BasketballTeam buildTeam(String name) {
        BasketballTeam team = new BasketballTeam(name);
        BasketballPositions[] positions = BasketballPositions.values();
        for (int i = 0; i < 5; i++) {
            BasketballAttributes attrs = new BasketballAttributes(positions[i % positions.length], 70, 70, 70, 70, 70);
            team.addPlayer(new BasketballPlayer("P" + i, 25, Gender.MALE, i + 1, positions[i % positions.length], attrs));
        }
        return team;
    }
}