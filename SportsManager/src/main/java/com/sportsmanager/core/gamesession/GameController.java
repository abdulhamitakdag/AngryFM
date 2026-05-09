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
import java.util.Random;


public class GameController {

    private static GameController instance;

    private final ISport sport;
    private final Gender gender;
    private final AbstractLeague league;
    private final List<AbstractTeam> teams;
    private AbstractTeam userTeam;

    private String currentSaveName;

    public static final int MAX_TRAININGS_PER_WEEK = 1;
    private int trainingsThisWeek = 0;
    private int trainingsCountedForWeek = 0;

    private GameController(ISport sport, Gender gender,
                           AbstractLeague league, List<AbstractTeam> teams) {
        this.sport = sport;
        this.gender = gender;
        this.league = league;
        this.teams = teams;
    }



    public static GameController startNew(String sportId, Gender gender, int teamCount) {
        ISport sport = SportFactory.createSport(sportId);

        if (teamCount > RandomGenerator.getAvailableTeamNameCount()) {
            throw new IllegalArgumentException(
                "Requested team count (" + teamCount + ") exceeds available team name count ("
                + RandomGenerator.getAvailableTeamNameCount() + ").");
        }

        RandomGenerator.resetTeamNamePool();

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
        Gender gender = (state.getGender() != null)
                ? Gender.valueOf(state.getGender())
                : Gender.MALE;
        List<AbstractTeam> teams = new ArrayList<>(league.getTeams());
        instance = new GameController(sport, gender, league, teams);
        instance.currentSaveName = saveName;
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
        GameState state = saveManager.createState(league, userTeamName, gender);
        saveManager.save(state, saveName);
        this.currentSaveName = saveName;
    }

    public String getCurrentSaveName() {
        return currentSaveName;
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

    private FootballMatch ongoingUserMatch;

    public List<MatchResult> simulateOtherMatchesThisWeek() {
        List<MatchResult> results = new ArrayList<>();
        int week = league.getCurrentWeek();
        for (Fixture f : league.getFixtures()) {
            if (f.getWeek() == week && !f.isPlayed()) {
                if (userTeam != null && (f.getHomeTeam().equals(userTeam) || f.getAwayTeam().equals(userTeam))) {
                    continue;
                }
                MatchResult r = simulateFootballMatch(f.getHomeTeam(), f.getAwayTeam());
                league.recordResult(r);
                results.add(r);
            }
        }
        return results;
    }

    public PeriodResult startUserMatch() {
        Fixture f = getUserFixture();
        if (f == null) return null;
        ongoingUserMatch = new FootballMatch(f.getHomeTeam(), f.getAwayTeam());
        ongoingUserMatch.start();
        return ongoingUserMatch.simulateCurrentPeriod();
    }

    public MatchResult finishUserMatch() {
        if (ongoingUserMatch == null) return null;
        ongoingUserMatch.resumeAfterBreak();
        ongoingUserMatch.simulateCurrentPeriod();
        MatchResult r = ongoingUserMatch.getMatchResult();
        league.recordResult(r);
        ongoingUserMatch = null;
        return r;
    }

    public FootballMatch getOngoingUserMatch() {
        return ongoingUserMatch;
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

    // ------- Antrenman -------

    private void syncTrainingWeek() {
        int week = league.getCurrentWeek();
        if (trainingsCountedForWeek != week) {
            trainingsCountedForWeek = week;
            trainingsThisWeek = 0;
        }
    }

    public int getTrainingsLeft() {
        syncTrainingWeek();
        return MAX_TRAININGS_PER_WEEK - trainingsThisWeek;
    }

    public boolean canTrain() {
        return getTrainingsLeft() > 0 && userTeam != null;
    }

    public TrainingReport trainTeam(double intensity) {
        if (userTeam == null) {
            throw new IllegalStateException("No user team");
        }
        syncTrainingWeek();
        if (trainingsThisWeek >= MAX_TRAININGS_PER_WEEK) {
            throw new IllegalStateException("No training sessions left this week");
        }

        List<AbstractPlayer> healthyBefore = new ArrayList<>(userTeam.getAvailablePlayers());

        userTeam.runTrainingSession(intensity);
        trainingsThisWeek++;

        double injuryChance = intensity * intensity * 0.05;
        List<AbstractPlayer> newlyInjured = new ArrayList<>();
        for (AbstractPlayer p : healthyBefore) {
            if (rng.nextDouble() < injuryChance) {
                Injury.Severity sev = rollInjurySeverity();
                int games = rollInjuryGames(sev);
                p.setInjury(new Injury(sev, games));
                newlyInjured.add(p);
            }
        }
        return new TrainingReport(newlyInjured, injuryChance);
    }

    private final Random rng = new Random();

    private Injury.Severity rollInjurySeverity() {
        double r = rng.nextDouble();
        if (r < 0.60) return Injury.Severity.MINOR;
        if (r < 0.90) return Injury.Severity.MODERATE;
        return Injury.Severity.SERIOUS;
    }

    private int rollInjuryGames(Injury.Severity sev) {
        switch (sev) {
            case MINOR:    return 1 + rng.nextInt(2);
            case MODERATE: return 3 + rng.nextInt(3);
            case SERIOUS:  return 6 + rng.nextInt(5);
            default:       return 1;
        }
    }

    public static class TrainingReport {
        private final List<AbstractPlayer> newlyInjured;
        private final double injuryChancePerPlayer;

        public TrainingReport(List<AbstractPlayer> newlyInjured, double chance) {
            this.newlyInjured = newlyInjured;
            this.injuryChancePerPlayer = chance;
        }

        public List<AbstractPlayer> getNewlyInjured() { return newlyInjured; }
        public double getInjuryChancePerPlayer()      { return injuryChancePerPlayer; }
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
