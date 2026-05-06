package com.sportsmanager.core.gamesession;

import com.sportsmanager.core.interfaces.ISport;
import com.sportsmanager.core.model.*;
import com.sportsmanager.factory.SportFactory;
import com.sportsmanager.persistence.FootballSaveManager;
import com.sportsmanager.persistence.GameState;
import com.sportsmanager.sport.football.FootballMatch;
import com.sportsmanager.util.RandomGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// UI controller'larının tek erişim noktası — singleton
// startNew() veya loadGame() çağrılana kadar getInstance() null döner
public class GameController {

    private static GameController instance;

    private final ISport sport;
    private final Gender gender;
    private final AbstractLeague league;
    private final List<AbstractTeam> teams;
    private AbstractTeam userTeam;

    private GameController(ISport sport, Gender gender,
                           AbstractLeague league, List<AbstractTeam> teams) {
        this.sport = sport;
        this.gender = gender;
        this.league = league;
        this.teams = teams;
    }

    // ------- Fabrika / yükleme -------

    public static GameController startNew(String sportId, Gender gender, int teamCount) {
        ISport sport = SportFactory.createSport(sportId);
        List<AbstractTeam> teams = new ArrayList<>();
        for (int i = 0; i < teamCount; i++) {
            AbstractTeam t = RandomGenerator.generateTeam(sport, gender);
            t.setCurrentTactic(sport.createDefaultTactic());
            teams.add(t);
        }
        String leagueName = (gender == Gender.MALE)
                ? "AngryFM Men's Super League"
                : "AngryFM Women's Super League";
        AbstractLeague league = sport.createLeague(leagueName);
        league.generateFixtures(teams);
        instance = new GameController(sport, gender, league, teams);
        return instance;
    }

    // Kaydedilmiş oyunu yükler ve singleton'ı günceller
    public static GameController loadGame(String saveName) throws IOException {
        FootballSaveManager saveManager = new FootballSaveManager();
        GameState state = saveManager.load(saveName);
        AbstractLeague league = saveManager.restoreLeague(state);
        ISport sport = SportFactory.createSport(state.getSportType());
        Gender gender = Gender.MALE; // save'de gender saklanmıyor, ileride eklenmeli
        List<AbstractTeam> teams = new ArrayList<>(league.getTeams());
        instance = new GameController(sport, gender, league, teams);
        if (state.getUserTeamName() != null) {
            for (AbstractTeam t : teams) {
                if (t.getName().equals(state.getUserTeamName())) {
                    instance.userTeam = t;
                    break;
                }
            }
        }
        return instance;
    }

    // Mevcut oyunu diske yazar
    public void saveGame(String saveName) throws IOException {
        FootballSaveManager saveManager = new FootballSaveManager();
        String userTeamName = (userTeam != null) ? userTeam.getName() : null;
        GameState state = saveManager.createState(league, userTeamName);
        saveManager.save(state, saveName);
    }

    // ------- Singleton erişimi -------

    public static GameController getInstance() {
        return instance;
    }

    // ------- Haftalık döngü -------

    // Kullanıcının bu haftaki fixture'ını döner; yoksa null
    public Fixture getUserFixture() {
        if (userTeam == null) return null;
        int week = league.getCurrentWeek();
        for (Fixture f : league.getFixtures()) {
            if (f.getWeek() == week && !f.isPlayed()) {
                if (f.getHomeTeam().equals(userTeam) || f.getAwayTeam().equals(userTeam)) {
                    return f;
                }
            }
        }
        return null;
    }

    // Bu haftanın tüm maçlarını simüle eder, sonuçları kaydeder, haftayı ilerletir
    // TODO: spor agnostik hale getir (şimdilik sadece futbol)
    public List<MatchResult> simulateFullWeek() {
        List<MatchResult> results = new ArrayList<>();
        int week = league.getCurrentWeek();
        for (Fixture f : league.getFixtures()) {
            if (f.getWeek() == week && !f.isPlayed()) {
                MatchResult r = simulateFootballMatch(f.getHomeTeam(), f.getAwayTeam());
                league.recordResult(r);
                results.add(r);
            }
        }
        league.advanceWeek();
        return results;
    }

    private MatchResult simulateFootballMatch(AbstractTeam home, AbstractTeam away) {
        FootballMatch match = new FootballMatch(home, away);
        match.start();
        match.simulateCurrentPeriod();
        match.resumeAfterBreak();
        match.simulateCurrentPeriod();
        return match.getMatchResult();
    }

    // Haftayı ilerletir (sadece injury decrement + currentWeek++)
    public void advanceWeek() {
        league.advanceWeek();
    }

    // ------- Getter'lar -------

    public void setUserTeam(AbstractTeam team)   { this.userTeam = team; }
    public AbstractTeam getUserTeam()            { return userTeam; }
    public ISport getSport()                     { return sport; }
    public AbstractLeague getLeague()            { return league; }
    public List<AbstractTeam> getTeams()         { return teams; }
    public Gender getGender()                    { return gender; }
    public List<?> getStandings()                { return league.getStandings(); }
    public List<?> getFixtures()                 { return league.getFixtures(); }
}
