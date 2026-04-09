package com.sportsmanager.persistence;

import com.sportsmanager.core.model.*;
import com.sportsmanager.sport.football.*;
import com.sportsmanager.util.RandomGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestSaveManager {

    @TempDir
    Path tempDir;

    private FootballSaveManager saveManager;

    @BeforeEach
    void setUp() {
        saveManager = new FootballSaveManager(tempDir);
    }

    @Test
    void saveAndLoadPreservesLeagueName() throws IOException {
        FootballLeague league = createTestLeague();
        GameState state = saveManager.createState(league, "TestTakim1");
        saveManager.save(state, "test1");

        GameState loaded = saveManager.load("test1");
        assertEquals("Test Ligi", loaded.getLeagueName());
    }

    @Test
    void saveAndLoadPreservesUserTeamName() throws IOException {
        FootballLeague league = createTestLeague();
        GameState state = saveManager.createState(league, "TestTakim1");
        saveManager.save(state, "test1");

        GameState loaded = saveManager.load("test1");
        assertEquals("TestTakim1", loaded.getUserTeamName());
    }

    @Test
    void saveAndLoadPreservesTeamCount() throws IOException {
        FootballLeague league = createTestLeague();
        GameState state = saveManager.createState(league, "TestTakim1");
        saveManager.save(state, "test1");

        GameState loaded = saveManager.load("test1");
        assertEquals(4, loaded.getTeams().size());
    }

    @Test
    void saveAndLoadPreservesPlayerData() throws IOException {
        FootballLeague league = createTestLeague();
        GameState state = saveManager.createState(league, "TestTakim1");
        saveManager.save(state, "test1");

        // ilk takımın ilk oyuncusunu kontrol ediyoruz
        GameState loaded = saveManager.load("test1");
        GameState.PlayerData firstPlayer = loaded.getTeams().get(0).getPlayers().get(0);
        assertNotNull(firstPlayer.getName());
        assertTrue(firstPlayer.getAge() >= 16 && firstPlayer.getAge() <= 50);
        assertNotNull(firstPlayer.getPosition());
    }

    @Test
    void saveAndLoadPreservesFixtures() throws IOException {
        FootballLeague league = createTestLeague();
        GameState state = saveManager.createState(league, "TestTakim1");
        saveManager.save(state, "test1");

        GameState loaded = saveManager.load("test1");
        assertFalse(loaded.getFixtures().isEmpty());
    }

    @Test
    void restoreLeaguePreservesTeamCount() throws IOException {
        FootballLeague league = createTestLeague();
        GameState state = saveManager.createState(league, "TestTakim1");
        saveManager.save(state, "test1");

        GameState loaded = saveManager.load("test1");
        AbstractLeague restoredLeague = saveManager.restoreLeague(loaded);
        assertEquals(4, restoredLeague.getTeams().size());
    }

    @Test
    void restoreLeaguePreservesTeamNames() throws IOException {
        FootballLeague league = createTestLeague();
        List<String> originalNames = league.getTeams().stream()
                .map(AbstractTeam::getName).sorted().toList();

        GameState state = saveManager.createState(league, "TestTakim1");
        saveManager.save(state, "test1");

        // yükledikten sonra takım isimleri aynı kalmalı
        GameState loaded = saveManager.load("test1");
        AbstractLeague restoredLeague = saveManager.restoreLeague(loaded);
        List<String> restoredNames = restoredLeague.getTeams().stream()
                .map(AbstractTeam::getName).sorted().toList();

        assertEquals(originalNames, restoredNames);
    }

    @Test
    void restoreLeaguePreservesSquadSizes() throws IOException {
        FootballLeague league = createTestLeague();
        int originalSquadSize = league.getTeams().get(0).getCurrentSquadSize();

        GameState state = saveManager.createState(league, "TestTakim1");
        saveManager.save(state, "test1");

        GameState loaded = saveManager.load("test1");
        AbstractLeague restoredLeague = saveManager.restoreLeague(loaded);
        assertEquals(originalSquadSize, restoredLeague.getTeams().get(0).getCurrentSquadSize());
    }

    @Test
    void restoreLeaguePreservesPlayedResults() throws IOException {
        FootballLeague league = createTestLeague();

        // bi kaç maç oynatalım
        playNextMatch(league);
        playNextMatch(league);

        long playedCount = league.getFixtures().stream().filter(Fixture::isPlayed).count();

        GameState state = saveManager.createState(league, "TestTakim1");
        saveManager.save(state, "test1");

        // yükleyince oynanan maç sayısı aynı olmalı
        GameState loaded = saveManager.load("test1");
        AbstractLeague restoredLeague = saveManager.restoreLeague(loaded);

        long restoredPlayedCount = restoredLeague.getFixtures().stream().filter(Fixture::isPlayed).count();
        assertEquals(playedCount, restoredPlayedCount);
    }

    @Test
    void restoreLeaguePreservesStandings() throws IOException {
        FootballLeague league = createTestLeague();

        playNextMatch(league);
        playNextMatch(league);

        // toplam puanı karşılaştırıyoruz
        int originalTotalPoints = league.getStandings().stream()
                .mapToInt(TeamInLeagueTable::getPoints).sum();

        GameState state = saveManager.createState(league, "TestTakim1");
        saveManager.save(state, "test1");

        GameState loaded = saveManager.load("test1");
        AbstractLeague restoredLeague = saveManager.restoreLeague(loaded);

        int restoredTotalPoints = restoredLeague.getStandings().stream()
                .mapToInt(TeamInLeagueTable::getPoints).sum();
        assertEquals(originalTotalPoints, restoredTotalPoints);
    }

    @Test
    void listSavesReturnsEmptyWhenNoSaves() throws IOException {
        List<String> saves = saveManager.listSaves();
        assertTrue(saves.isEmpty());
    }

    @Test
    void listSavesReturnsSavedGames() throws IOException {
        FootballLeague league = createTestLeague();
        GameState state = saveManager.createState(league, "TestTakim1");

        saveManager.save(state, "save1");
        saveManager.save(state, "save2");

        List<String> saves = saveManager.listSaves();
        assertEquals(2, saves.size());
        assertTrue(saves.contains("save1"));
        assertTrue(saves.contains("save2"));
    }

    @Test
    void deleteRemovesSave() throws IOException {
        FootballLeague league = createTestLeague();
        GameState state = saveManager.createState(league, "TestTakim1");
        saveManager.save(state, "toDelete");

        assertTrue(saveManager.delete("toDelete"));
        assertFalse(saveManager.listSaves().contains("toDelete"));
    }

    @Test
    void loadNonExistentThrowsIOException() {
        // olmayan bi dosyayı yüklemeye çalışınca IOException fırlatmalı
        assertThrows(IOException.class, () -> saveManager.load("yok"));
    }

    @Test
    void saveVersionIsSet() throws IOException {
        FootballLeague league = createTestLeague();
        GameState state = saveManager.createState(league, "TestTakim1");
        saveManager.save(state, "ver");

        GameState loaded = saveManager.load("ver");
        assertEquals(1, loaded.getSaveVersion());
    }

    @Test
    void sportTypeIsFootball() throws IOException {
        FootballLeague league = createTestLeague();
        GameState state = saveManager.createState(league, "TestTakim1");
        assertEquals("football", state.getSportType());
    }

    @Test
    void pathTraversalInSaveNameThrows() {
        FootballLeague league = createTestLeague();
        GameState state = saveManager.createState(league, "TestTakim1");

        // .. içeren isim kabul edilmemeli
        assertThrows(IllegalArgumentException.class, () -> saveManager.save(state, "../hack"));
        assertThrows(IllegalArgumentException.class, () -> saveManager.save(state, "a/b"));
        assertThrows(IllegalArgumentException.class, () -> saveManager.save(state, "a\\b"));
        assertThrows(IllegalArgumentException.class, () -> saveManager.save(state, ""));
    }

    @Test
    void pathTraversalInLoadNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> saveManager.load("../hack"));
    }

    @Test
    void pathTraversalInDeleteNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> saveManager.delete("../hack"));
    }

    @Test
    void saveAndLoadPreservesSeasonStats() throws IOException {
        FootballLeague league = createTestLeague();

        // bi kaç maç oynatalım ki istatistikler oluşsun
        playNextMatch(league);
        playNextMatch(league);

        int originalWins = league.getTeams().get(0).getWins();
        int originalGoalsScored = league.getTeams().get(0).getGoalsScored();

        GameState state = saveManager.createState(league, "TestTakim1");
        saveManager.save(state, "statsTest");

        GameState loaded = saveManager.load("statsTest");
        AbstractLeague restored = saveManager.restoreLeague(loaded);

        assertEquals(originalWins, restored.getTeams().get(0).getWins());
        assertEquals(originalGoalsScored, restored.getTeams().get(0).getGoalsScored());
    }

    @Test
    void corruptJsonThrowsReadableError() throws IOException {
        // bozuk bi json dosyası oluşturuyoruz
        java.nio.file.Files.createDirectories(tempDir);
        java.nio.file.Files.writeString(tempDir.resolve("bozuk.json"), "{{{bozuk veri");

        IOException ex = assertThrows(IOException.class, () -> saveManager.load("bozuk"));
        assertTrue(ex.getMessage().contains("bozuk"));
    }

    // 4 takımlık test ligi oluşturuyor
    // isim çakışması olmasın diye takım isimlerini elle veriyoruz
    private FootballLeague createTestLeague() {
        String[] teamNames = {"TestTakim1", "TestTakim2", "TestTakim3", "TestTakim4"};
        List<AbstractTeam> teams = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            AbstractTeam team = RandomGenerator.generateTeam(RandomGenerator.DEFAULT_SPORT);
            // random isim yerine sabit isim veriyoruz
            AbstractTeam renamedTeam = new com.sportsmanager.sport.football.FootballTeam(teamNames[i]);
            for (AbstractPlayer p : team.getSquad()) {
                renamedTeam.addPlayer(p);
            }
            for (AbstractCoach c : team.getCoaches()) {
                renamedTeam.addCoach(c);
            }
            if (team.getCurrentTactic() != null) {
                renamedTeam.setCurrentTactic(team.getCurrentTactic());
            }
            teams.add(renamedTeam);
        }

        FootballLeague league = new FootballLeague("Test Ligi");
        league.generateFixtures(teams);
        return league;
    }

    // sıradaki maçı oynayıp sonucu lige kaydediyor
    private void playNextMatch(FootballLeague league) {
        List<Fixture> unplayed = league.getUnplayedFixtures();
        if (unplayed.isEmpty()) return;

        Fixture next = unplayed.get(0);
        FootballMatch match = new FootballMatch(next.getHomeTeam(), next.getAwayTeam());
        match.start();
        match.simulateCurrentPeriod();
        match.resumeAfterBreak();
        match.simulateCurrentPeriod();

        league.recordResult(match.getMatchResult());
    }
}
