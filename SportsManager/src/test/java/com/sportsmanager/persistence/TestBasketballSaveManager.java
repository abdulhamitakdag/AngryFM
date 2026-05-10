package com.sportsmanager.persistence;

import com.sportsmanager.core.model.*;
import com.sportsmanager.sport.basketball.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class TestBasketballSaveManager {

    @TempDir
    Path tempDir;
    BasketballSaveManager saveManager;

    @BeforeEach
    void setUp() {
        saveManager = new BasketballSaveManager(tempDir);
    }

    @Test
    void sportTypeIsBasketball() {
        assertEquals("basketball", saveManager.getSportType());
    }

    @Test
    void saveAndLoadPreservesLeagueName() throws IOException {
        BasketballLeague league = createTestLeague();
        GameState state = saveManager.createState(league, "TeamA");
        saveManager.save(state, "bball_save");
        GameState loaded = saveManager.load("bball_save");
        assertEquals("Test Basketball League", loaded.getLeagueName());
    }

    @Test
    void saveAndLoadPreservesPlayerAttributes() throws IOException {
        BasketballLeague league = createTestLeague();
        GameState state = saveManager.createState(league, "TeamA");
        saveManager.save(state, "bball_attrs");
        GameState loaded = saveManager.load("bball_attrs");

        GameState.PlayerData pd = loaded.getTeams().get(0).getPlayers().get(0);
        assertTrue(pd.getShooting() > 0, "Shooting should be saved");
        assertTrue(pd.getPlaymaking() > 0, "Playmaking should be saved");
        assertTrue(pd.getRebounding() > 0, "Rebounding should be saved");
    }

    @Test
    void restoreLeagueCreatesBasketballTeams() throws IOException {
        BasketballLeague league = createTestLeague();
        GameState state = saveManager.createState(league, "TeamA");
        saveManager.save(state, "restore_test");
        GameState loaded = saveManager.load("restore_test");
        AbstractLeague restored = saveManager.restoreLeague(loaded);

        assertTrue(restored instanceof BasketballLeague);
        assertTrue(restored.getTeams().get(0) instanceof BasketballTeam);
    }

    @Test
    void restoreLeagueCreatesBasketballPlayers() throws IOException {
        BasketballLeague league = createTestLeague();
        GameState state = saveManager.createState(league, "TeamA");
        saveManager.save(state, "player_test");
        GameState loaded = saveManager.load("player_test");
        AbstractLeague restored = saveManager.restoreLeague(loaded);

        AbstractPlayer player = restored.getTeams().get(0).getSquad().get(0);
        assertTrue(player instanceof BasketballPlayer);
        assertTrue(player.getAttributes() instanceof BasketballAttributes);
    }

    private BasketballLeague createTestLeague() {
        BasketballLeague league = new BasketballLeague("Test Basketball League");

        BasketballTeam teamA = new BasketballTeam("TeamA");
        BasketballAttributes attrs = new BasketballAttributes(BasketballPositions.PG, 80, 85, 70, 60, 75);
        BasketballPlayer p1 = new BasketballPlayer("Shane", 35, Gender.MALE, 23, BasketballPositions.PG, attrs);
        teamA.addPlayer(p1);

        BasketballTeam teamB = new BasketballTeam("TeamB");

        java.util.List<AbstractTeam> teams = new java.util.ArrayList<>();
        teams.add(teamA);
        teams.add(teamB);

        league.generateFixtures(teams);

        return league;
    }
}