package com.sportsmanager.core.model;

import com.sportsmanager.core.interfaces.ISport;
import com.sportsmanager.core.model.BaseTest;
import com.sportsmanager.factory.SportFactory;
import com.sportsmanager.sport.football.FootballSport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestSportFactory extends BaseTest {

    @Test
    void createSportReturnsFootballSportWhenFootballIsRequested() {
        ISport sport = SportFactory.createSport("football");
        assertInstanceOf(FootballSport.class, sport);
    }

    @Test
    void createSportThrowsExceptionForUnknownSport() {
        assertThrows(IllegalArgumentException.class, () -> SportFactory.createSport("basketball"));
    }

    @Test
    void createSportThrowsExceptionForNullSportId() {
        assertThrows(IllegalArgumentException.class, () -> SportFactory.createSport(null));
    }
}