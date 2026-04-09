package com.sportsmanager.persistence;

import java.util.List;
import java.util.Map;

// oyun kaydının json'a dönüşecek hali
// jackson bunu okuyup yazıyor, o yüzden düz pojo olarak tuttuk
public class GameState {

    private int saveVersion = 1;
    private String sportType;
    private String leagueName;
    private int currentWeek;
    private String userTeamName;
    private List<TeamData> teams;
    private List<FixtureData> fixtures;

    public int getSaveVersion()
        { return saveVersion; }
    public void setSaveVersion(int saveVersion)
        { this.saveVersion = saveVersion; }

    public String getSportType()
        { return sportType; }
    public void setSportType(String sportType)
        { this.sportType = sportType; }

    public String getLeagueName()
        { return leagueName; }
    public void setLeagueName(String leagueName)
        { this.leagueName = leagueName; }

    public int getCurrentWeek()
        { return currentWeek; }
    public void setCurrentWeek(int currentWeek)
        { this.currentWeek = currentWeek; }

    public String getUserTeamName()
        { return userTeamName; }
    public void setUserTeamName(String userTeamName)
        { this.userTeamName = userTeamName; }

    public List<TeamData> getTeams()
        { return teams; }
    public void setTeams(List<TeamData> teams)
        { this.teams = teams; }

    public List<FixtureData> getFixtures()
        { return fixtures; }
    public void setFixtures(List<FixtureData> fixtures)
        { this.fixtures = fixtures; }

    // takım bilgisi
    public static class TeamData {
        private String name;
        private List<PlayerData> players;
        private List<CoachData> coaches;
        private TacticData tactic;
        // sezon istatistikleri
        private int wins;
        private int draws;
        private int losses;
        private int goalsScored;
        private int goalsConceded;

        public String getName()
            { return name; }
        public void setName(String name)
            { this.name = name; }

        public List<PlayerData> getPlayers()
            { return players; }
        public void setPlayers(List<PlayerData> players)
            { this.players = players; }

        public List<CoachData> getCoaches()
            { return coaches; }
        public void setCoaches(List<CoachData> coaches)
            { this.coaches = coaches; }

        public TacticData getTactic()
            { return tactic; }
        public void setTactic(TacticData tactic)
            { this.tactic = tactic; }

        public int getWins()
            { return wins; }
        public void setWins(int wins)
            { this.wins = wins; }

        public int getDraws()
            { return draws; }
        public void setDraws(int draws)
            { this.draws = draws; }

        public int getLosses()
            { return losses; }
        public void setLosses(int losses)
            { this.losses = losses; }

        public int getGoalsScored()
            { return goalsScored; }
        public void setGoalsScored(int goalsScored)
            { this.goalsScored = goalsScored; }

        public int getGoalsConceded()
            { return goalsConceded; }
        public void setGoalsConceded(int goalsConceded)
            { this.goalsConceded = goalsConceded; }
    }

    // oyuncu bilgisi - saha oyuncusu ve kaleci attributeları ayrı tutuluyor
    // kullanılmayan fieldlar null kalacak, mesela kalecide pace null olur
    public static class PlayerData {
        private String name;
        private int age;
        private String gender;
        private int shirtNumber;
        private String position;
        // saha oyuncusu
        private Double pace;
        private Double shooting;
        private Double passing;
        private Double defending;
        private Double physical;
        //kaleci
        private Double reflexes;
        private Double positioning;
        private Double diving;
        private Double handling;
        // sakatlık bilgisi
        private String injurySeverity;
        private int injuryGamesRemaining;

        public String getName()
            { return name; }
        public void setName(String name)
            { this.name = name; }

        public int getAge()
            { return age; }
        public void setAge(int age)
            { this.age = age; }

        public String getGender()
            { return gender; }
        public void setGender(String gender)
            { this.gender = gender; }

        public int getShirtNumber()
            { return shirtNumber; }
        public void setShirtNumber(int shirtNumber)
            { this.shirtNumber = shirtNumber; }

        public String getPosition()
            { return position; }
        public void setPosition(String position)
            { this.position = position; }

        public Double getPace()
            { return pace; }
        public void setPace(Double pace)
            { this.pace = pace; }

        public Double getShooting()
            { return shooting; }
        public void setShooting(Double shooting)
            { this.shooting = shooting; }

        public Double getPassing()
            { return passing; }
        public void setPassing(Double passing)
            { this.passing = passing; }

        public Double getDefending()
            { return defending; }
        public void setDefending(Double defending)
            { this.defending = defending; }

        public Double getPhysical()
            { return physical; }
        public void setPhysical(Double physical)
            { this.physical = physical; }

        public Double getReflexes()
            { return reflexes; }
        public void setReflexes(Double reflexes)
            { this.reflexes = reflexes; }

        public Double getPositioning()
            { return positioning; }
        public void setPositioning(Double positioning)
            { this.positioning = positioning; }

        public Double getDiving()
            { return diving; }
        public void setDiving(Double diving)
            { this.diving = diving; }

        public Double getHandling()
            { return handling; }
        public void setHandling(Double handling)
            { this.handling = handling; }

        public String getInjurySeverity()
            { return injurySeverity; }
        public void setInjurySeverity(String injurySeverity)
            { this.injurySeverity = injurySeverity; }

        public int getInjuryGamesRemaining()
            { return injuryGamesRemaining; }
        public void setInjuryGamesRemaining(int injuryGamesRemaining)
            { this.injuryGamesRemaining = injuryGamesRemaining; }
    }

    // koç bilgisi
    public static class CoachData {
        private String name;
        private int age;
        private String gender;
        private String specialty;
        private int coachingLevel;

        public String getName()
            { return name; }
        public void setName(String name)
            { this.name = name; }

        public int getAge()
            { return age; }
        public void setAge(int age)
            { this.age = age; }

        public String getGender()
            { return gender; }
        public void setGender(String gender)
            { this.gender = gender; }

        public String getSpecialty()
            { return specialty; }
        public void setSpecialty(String specialty)
            { this.specialty = specialty; }

        public int getCoachingLevel()
            { return coachingLevel; }
        public void setCoachingLevel(int coachingLevel)
            { this.coachingLevel = coachingLevel; }
    }

    // taktik bilgisi
    public static class TacticData {
        private String name;
        private double attackingWeight;
        private double pressureIntensity;
        private Map<String, Integer> requiredPositions;

        public String getName()
            { return name; }
        public void setName(String name)
            { this.name = name; }

        public double getAttackingWeight()
            { return attackingWeight; }
        public void setAttackingWeight(double attackingWeight)
            { this.attackingWeight = attackingWeight; }

        public double getPressureIntensity()
            { return pressureIntensity; }
        public void setPressureIntensity(double pressureIntensity)
            { this.pressureIntensity = pressureIntensity; }

        public Map<String, Integer> getRequiredPositions()
            { return requiredPositions; }
        public void setRequiredPositions(Map<String, Integer> requiredPositions)
            { this.requiredPositions = requiredPositions; }
    }

    // fikstür bilgisi - oynandıysa skoru da tutuyor
    public static class FixtureData {
        private int week;
        private String homeTeamName;
        private String awayTeamName;
        private boolean played;
        private int homeScore;
        private int awayScore;

        public int getWeek()
            { return week; }
        public void setWeek(int week)
            { this.week = week; }

        public String getHomeTeamName()
            { return homeTeamName; }
        public void setHomeTeamName(String homeTeamName)
            { this.homeTeamName = homeTeamName; }

        public String getAwayTeamName()
            { return awayTeamName; }
        public void setAwayTeamName(String awayTeamName)
            { this.awayTeamName = awayTeamName; }

        public boolean isPlayed()
            { return played; }
        public void setPlayed(boolean played)
            { this.played = played; }

        public int getHomeScore()
            { return homeScore; }
        public void setHomeScore(int homeScore)
            { this.homeScore = homeScore; }

        public int getAwayScore()
            { return awayScore; }
        public void setAwayScore(int awayScore)
            { this.awayScore = awayScore; }
    }
}
