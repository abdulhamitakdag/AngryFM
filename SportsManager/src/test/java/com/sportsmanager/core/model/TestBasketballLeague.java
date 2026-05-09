package com.sportsmanager.core.model;

import com.sportsmanager.sport.basketball.BasketballLeague;
import com.sportsmanager.sport.basketball.BasketballTeam;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestBasketballLeague extends BaseTest{

    @Test
    void constructorStoresLeagueName(){
        BasketballLeague league = new BasketballLeague("Basket League");
        assertEquals("Basket League", league.getName());
        assertEquals(1, league.getCurrentWeek());
    }

    @Test
    void generateFixturesCreatesHomeAndAwayFixturesForEvenTeams(){
        BasketballLeague league = new BasketballLeague("Basket League");
        List<AbstractTeam> teams = List.of(
                new BasketballTeam("A"), new BasketballTeam("B"),
                new BasketballTeam("C"), new BasketballTeam("D"));

        league.generateFixtures(teams);

        assertEquals(4, league.getTeams().size());
        assertEquals(12, league.getFixtures().size());
    }

    @Test
    void recordResultUsesBasketballPoints(){
        BasketballLeague league = new BasketballLeague("Basket League");
        BasketballTeam home = new BasketballTeam("Home");
        BasketballTeam away = new BasketballTeam("Away");
        league.generateFixtures(List.of(home, away));

        league.recordResult(new MatchResult(home, away, 90, 80));

        assertEquals(2, league.getStandings().get(0).getPoints());
    }

}