package com.sportsmanager.core.gamesession;

import com.sportsmanager.core.interfaces.ISport;
import com.sportsmanager.core.model.*;
import com.sportsmanager.factory.SportFactory;
import com.sportsmanager.persistence.AbstractSaveManager;
import com.sportsmanager.persistence.BasketballSaveManager;
import com.sportsmanager.persistence.FootballSaveManager;
import com.sportsmanager.persistence.GameState;
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
    private int seasonNumber = 1;

    public static final int MAX_TRAININGS_PER_WEEK = 1;
    private int trainingsThisWeek = 0;
    private int trainingsCountedForWeek = 0;

    private AbstractMatch ongoingUserMatch;
    private List<PeriodResult> lastUserMatchPeriods = new ArrayList<>();
    private List<AbstractPlayer> lastUserMatchInjuries = new ArrayList<>();

    private final Random rng = new Random();

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

    public static GameController loadGame(String saveName) throws IOException {
        // Herhangi bir save manager ile JSON'ı oku (format her sporda aynı)
        FootballSaveManager probe = new FootballSaveManager();
        GameState state = probe.load(saveName);

        // Sport tipine göre doğru save manager ile ligi geri oluştur
        AbstractSaveManager saveManager = createSaveManagerForSport(state.getSportType());
        AbstractLeague league = saveManager.restoreLeague(state);

        ISport sport = SportFactory.createSport(state.getSportType());
        Gender gender = (state.getGender() != null)
                ? Gender.valueOf(state.getGender())
                : Gender.MALE;
        List<AbstractTeam> teams = new ArrayList<>(league.getTeams());
        instance = new GameController(sport, gender, league, teams);
        instance.currentSaveName = saveName;
        instance.seasonNumber = state.getSeasonNumber() > 0 ? state.getSeasonNumber() : 1;
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

    public void saveGame(String saveName) throws IOException {
        AbstractSaveManager saveManager = createSaveManagerForSport(sport.getSportId());
        String userTeamName = (userTeam != null) ? userTeam.getName() : null;
        GameState state = saveManager.createState(league, userTeamName, gender, seasonNumber);
        saveManager.save(state, saveName);
        this.currentSaveName = saveName;
    }

    // Sport ID'ye göre doğru save manager'ı döndürür
    private static AbstractSaveManager createSaveManagerForSport(String sportId) {
        if ("basketball".equals(sportId)) return new BasketballSaveManager();
        return new FootballSaveManager();
    }

    public String getCurrentSaveName() {
        return currentSaveName;
    }

    // ------- Singleton erişimi -------

    public static GameController getInstance() {
        return instance;
    }

    // ------- Haftalık döngü -------

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

    // Diğer maçları simüle eder — hangi spor olursa olsun AbstractMatch kullanır
    public List<MatchResult> simulateOtherMatchesThisWeek() {
        List<MatchResult> results = new ArrayList<>();
        int week = league.getCurrentWeek();
        for (Fixture f : league.getFixtures()) {
            if (f.getWeek() == week && !f.isPlayed()) {
                if (userTeam != null && (f.getHomeTeam().equals(userTeam) || f.getAwayTeam().equals(userTeam))) {
                    continue;
                }
                MatchResult r = simulateAnyMatch(f.getHomeTeam(), f.getAwayTeam());
                league.recordResult(r);
                results.add(r);
            }
        }
        return results;
    }

    // Kullanıcı maçını başlatır, ilk period'u simüle edip döner (halftime dialog için)
    public PeriodResult startUserMatch() {
        Fixture f = getUserFixture();
        if (f == null) return null;
        ongoingUserMatch = league.createMatchForTeams(f.getHomeTeam(), f.getAwayTeam());
        ongoingUserMatch.start();
        return ongoingUserMatch.simulateCurrentPeriod();
    }

    // Bir sonraki period'u simüle eder (basketbol Q1 sonrası Q2 için kullanılır)
    public PeriodResult simulateNextPeriodOfUserMatch() {
        if (ongoingUserMatch == null) return null;
        if (ongoingUserMatch.getState() == AbstractMatch.MatchState.BETWEEN_PERIODS) {
            ongoingUserMatch.resumeAfterBreak();
        }
        if (ongoingUserMatch.getState() == AbstractMatch.MatchState.IN_PROGRESS) {
            return ongoingUserMatch.simulateCurrentPeriod();
        }
        return null;
    }

    // Kalan tüm period'ları oynar ve sonucu döner
    // Football: sadece 2. yarı | Basketball: Q2, Q3, Q4 (+ OT varsa)
    public MatchResult finishUserMatch() {
        if (ongoingUserMatch == null) return null;
        while (ongoingUserMatch.getState() != AbstractMatch.MatchState.FINISHED) {
            if (ongoingUserMatch.getState() == AbstractMatch.MatchState.BETWEEN_PERIODS) {
                ongoingUserMatch.resumeAfterBreak();
            }
            if (ongoingUserMatch.getState() == AbstractMatch.MatchState.IN_PROGRESS) {
                ongoingUserMatch.simulateCurrentPeriod();
            }
        }
        MatchResult r = ongoingUserMatch.getMatchResult();
        lastUserMatchPeriods = new ArrayList<>(ongoingUserMatch.getPeriodResults());
        lastUserMatchInjuries = new ArrayList<>(ongoingUserMatch.getMatchInjuries());
        league.recordResult(r);
        ongoingUserMatch = null;
        return r;
    }

    public AbstractMatch getOngoingUserMatch() {
        return ongoingUserMatch;
    }

    // Herhangi bir maçı tüm period'larıyla simüle eder
    private MatchResult simulateAnyMatch(AbstractTeam home, AbstractTeam away) {
        AbstractMatch match = league.createMatchForTeams(home, away);
        match.start();
        while (match.getState() != AbstractMatch.MatchState.FINISHED) {
            if (match.getState() == AbstractMatch.MatchState.BETWEEN_PERIODS) {
                autoSubstituteInjured(home);
                autoSubstituteInjured(away);
                match.resumeAfterBreak();
            }
            if (match.getState() == AbstractMatch.MatchState.IN_PROGRESS) {
                match.simulateCurrentPeriod();
            }
        }
        return match.getMatchResult();
    }

    private void autoSubstituteInjured(AbstractTeam team) {
        int playersOnField = sport.getPlayersOnField();
        List<AbstractPlayer> currentLineup = team.getStartingLineup();
        if (currentLineup.size() >= playersOnField) return;

        List<AbstractPlayer> bench = team.getBench();
        for (AbstractPlayer sub : bench) {
            if (currentLineup.size() >= playersOnField) break;
            team.swapStartingWithBench(null, sub);
            currentLineup = team.getStartingLineup();
        }
    }

    public void advanceWeek() {
        league.advanceWeek();
    }

    // ------- Sezon sonu -------

    public SeasonTransitionResult startNewSeason() {
        AbstractTeam champion = league.getChampion();

        List<AbstractPlayer> retiredPlayers = new ArrayList<>();
        List<AbstractPlayer> regenPlayers = new ArrayList<>();
        List<AbstractPlayer> userRetired = new ArrayList<>();
        List<AbstractPlayer> userRegen = new ArrayList<>();

        for (AbstractTeam team : teams) {
            // 1) yaşlandır ve attribute progression uygula
            for (AbstractPlayer p : team.getSquad()) {
                p.incrementAge();
                if (p.getAttributes() != null) {
                    p.getAttributes().applySeasonProgression(p.getAge(), rng);
                }
            }

            // 2) emeklilik: 40+ kesin, 35-39 artan şansla
            List<AbstractPlayer> toRetire = new ArrayList<>();
            for (AbstractPlayer p : team.getSquad()) {
                if (p.getAge() >= 40) {
                    toRetire.add(p);
                } else if (p.getAge() >= 35) {
                    double retireChance = (p.getAge() - 34) * 0.15;
                    if (rng.nextDouble() < retireChance) {
                        toRetire.add(p);
                    }
                }
            }

            retiredPlayers.addAll(toRetire);

            // 3) emekli oyuncuları çıkar ve regen ile doldur
            java.util.Set<Integer> usedNumbers = new java.util.HashSet<>();
            for (AbstractPlayer p : team.getSquad()) {
                usedNumbers.add(p.getShirtNumber());
            }

            boolean isUserTeam = team.equals(userTeam);
            for (AbstractPlayer retired : toRetire) {
                String position = retired.getPosition();
                usedNumbers.remove(retired.getShirtNumber());
                team.removePlayer(retired);

                AbstractPlayer regen = RandomGenerator.generateRegenPlayer(
                        sport, gender, position, usedNumbers);
                team.addPlayer(regen);
                regenPlayers.add(regen);

                if (isUserTeam) {
                    userRetired.add(retired);
                    userRegen.add(regen);
                }
            }
        }

        seasonNumber++;
        league.resetForNewSeason();

        for (AbstractTeam team : teams) {
            team.autoSetLineup(sport.getPlayersOnField());
        }

        trainingsThisWeek = 0;
        trainingsCountedForWeek = 0;
        ongoingUserMatch = null;

        return new SeasonTransitionResult(champion, retiredPlayers, regenPlayers,
                userRetired, userRegen, seasonNumber);
    }

    public static class SeasonTransitionResult {
        private final AbstractTeam champion;
        private final List<AbstractPlayer> retiredPlayers;
        private final List<AbstractPlayer> regenPlayers;
        private final List<AbstractPlayer> userRetiredPlayers;
        private final List<AbstractPlayer> userRegenPlayers;
        private final int newSeasonNumber;

        public SeasonTransitionResult(AbstractTeam champion, List<AbstractPlayer> retired,
                                      List<AbstractPlayer> regen, List<AbstractPlayer> userRetired,
                                      List<AbstractPlayer> userRegen, int newSeasonNumber) {
            this.champion = champion;
            this.retiredPlayers = retired;
            this.regenPlayers = regen;
            this.userRetiredPlayers = userRetired;
            this.userRegenPlayers = userRegen;
            this.newSeasonNumber = newSeasonNumber;
        }

        public AbstractTeam getChampion()                    { return champion; }
        public List<AbstractPlayer> getRetiredPlayers()      { return retiredPlayers; }
        public List<AbstractPlayer> getRegenPlayers()        { return regenPlayers; }
        public List<AbstractPlayer> getUserRetiredPlayers()  { return userRetiredPlayers; }
        public List<AbstractPlayer> getUserRegenPlayers()    { return userRegenPlayers; }
        public int getNewSeasonNumber()                      { return newSeasonNumber; }
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

    public void makeSubstitution(AbstractPlayer playerOut, AbstractPlayer playerIn) {
        if (ongoingUserMatch == null || userTeam == null) return;
        boolean isHome = ongoingUserMatch.getHomeTeam().equals(userTeam);
        if (isHome) ongoingUserMatch.substituteHome(playerOut, playerIn);
        else        ongoingUserMatch.substituteAway(playerOut, playerIn);
        userTeam.swapStartingWithBench(playerOut, playerIn);
    }

    public List<PeriodResult> getLastUserMatchPeriods()    { return lastUserMatchPeriods; }
    public List<AbstractPlayer> getLastUserMatchInjuries() { return lastUserMatchInjuries; }

    public void setUserTeam(AbstractTeam team)   { this.userTeam = team; }
    public AbstractTeam getUserTeam()            { return userTeam; }
    public ISport getSport()                     { return sport; }
    public AbstractLeague getLeague()            { return league; }
    public List<AbstractTeam> getTeams()         { return teams; }
    public Gender getGender()                    { return gender; }
    public int getSeasonNumber()                 { return seasonNumber; }
    public List<?> getStandings()                { return league.getStandings(); }
    public List<?> getFixtures()                 { return league.getFixtures(); }
}
