package com.sportsmanager.core.model;

import com.sportsmanager.core.interfaces.ILeague;

import java.util.*;

public abstract class AbstractLeague implements ILeague {

    private final String name;
    private final List<AbstractTeam> teams;
    private final List<Fixture> fixtures;
    private final Map<UUID, TeamInLeagueTable> teamMap;
    private int currentWeek;

    public AbstractLeague(String name) {
        this.name = name;
        this.teams = new ArrayList<>();
        this.fixtures = new ArrayList<>();
        this.teamMap = new HashMap<>(); //LinkedHashMap gereksiz veri tutacağından HashMap e geçildi.
        this.currentWeek = 1;
    }
//her lig için belirlenecek kazanma ve beraberliğin getirdiği puan sayısı
    protected abstract int getWinPoints();
    protected abstract int getDrawPoints();

    // FootballMatch falan döndürecek buradan
    //Maç oluşturma methodu her maç için farklı olacağından abstract method tercih edildi
    protected abstract AbstractMatch createMatch(AbstractTeam home, AbstractTeam away);

    @Override
    public List<TeamInLeagueTable> getStandings() {
        List<TeamInLeagueTable> sorted = new ArrayList<>(teamMap.values());
        sorted.sort((a, b) -> {
            if (a.getPoints() != b.getPoints()) {
                return Integer.compare(b.getPoints(), a.getPoints());//puan üstünlüğü kontrolü
            }
            if (a.getGoalDifference() != b.getGoalDifference()) {//puanlar eşitse averaj üstünlüğü
                return Integer.compare(b.getGoalDifference(), a.getGoalDifference());
            }
            // eşitlik bozulmazsa alfabetik sıraya göre diz
            return a.getTeam().getName().compareTo(b.getTeam().getName());
        });
        return Collections.unmodifiableList(sorted); //en sonda çıkan sıralanmış tabloyu salt okunur yaparak döndürüyoruz.
    }

    @Override
    public void recordResult(MatchResult result) {
        if (result == null) {
            throw new IllegalArgumentException("Result cannot be null");
        }

        // sonucun takımlarına uyan ilk oynanmamış fixture'ı buluyoruz
        // eskiden ilk oynanmamışı alıyorduk ama yanlış takımla eşleşme olabiliyordu
        Fixture fixture = null;
        for (Fixture f : fixtures) {
            if (!f.isPlayed()
                    && f.getHomeTeam().equals(result.getHomeTeam())
                    && f.getAwayTeam().equals(result.getAwayTeam())) {
                fixture = f;
                break;
            }
        }
        if (fixture == null) {
            throw new IllegalStateException("No unplayed fixture found for matchup: " 
                + result.getHomeTeam().getName() + " vs " + result.getAwayTeam().getName());
        }

        fixture.setResult(result);

        TeamInLeagueTable homeEntry = teamMap.get(fixture.getHomeTeam().getId());
        TeamInLeagueTable awayEntry = teamMap.get(fixture.getAwayTeam().getId());

        if (homeEntry == null || awayEntry == null) {
            throw new IllegalStateException("Team not found in fixture");
        }
// iki takımın da verileri güncellenir
        homeEntry.addResult(result.isHomeWin(), result.isDraw(),
                result.getHomeScore(), result.getAwayScore(),
                getWinPoints(), getDrawPoints());

        awayEntry.addResult(result.isAwayWin(), result.isDraw(),
                result.getAwayScore(), result.getHomeScore(),
                getWinPoints(), getDrawPoints());

        // takımların kendi istatistiklerini de güncelliyoruz
        AbstractTeam home = fixture.getHomeTeam();
        AbstractTeam away = fixture.getAwayTeam();
        home.recordResult(result.isHomeWin(), result.isDraw(),
                result.getHomeScore(), result.getAwayScore());
        away.recordResult(result.isAwayWin(), result.isDraw(),
                result.getAwayScore(), result.getHomeScore());

    }
    @Override
    public void generateFixtures(List<AbstractTeam> teamList) {
        if (teamList == null || teamList.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 teams for fixtures");
        }
        if (teamList.size()%2!=0) {
            throw new IllegalArgumentException("Team numbers must be even in the league");
        }
        // sezon ortasında yanlışlıkla çağrılmasın diye kontrol ediyoruz
        if (!fixtures.isEmpty()) {
            throw new IllegalStateException("Fixture already have been created, first reset the season");
        }

        this.teams.clear();
        this.teams.addAll(teamList);
        this.fixtures.clear();
        this.teamMap.clear();
        this.currentWeek = 1;

        for (AbstractTeam team : teams) {
            teamMap.put(team.getId(), new TeamInLeagueTable(team));
        }

        int n = teams.size();
        List<AbstractTeam> roundTeams = new ArrayList<>(teams);
        if (n % 2 != 0) {
            roundTeams.add(null); // tek sayiysa bye
        }

        int totalTeams = roundTeams.size();
        int halfSize = totalTeams / 2;
        int totalRounds = totalTeams - 1;

        // 1.tur
        for (int round = 0; round < totalRounds; round++) {
            int week = round + 1;
            for (int match = 0; match < halfSize; match++) {
                AbstractTeam home = roundTeams.get(match);
                AbstractTeam away = roundTeams.get(totalTeams - 1 - match);

                if (home != null && away != null) {
                    fixtures.add(new Fixture(week, home, away));
                }
            }
            AbstractTeam last = roundTeams.remove(roundTeams.size() - 1);
            roundTeams.add(1, last);
        }

        // 2. tur  ev sahibi deplasmanla yer değiştirir
        int firstTourSize = fixtures.size();
        for (int i = 0; i < firstTourSize; i++) {
            Fixture f = fixtures.get(i);
            int secondWeek = ((i / Math.max(1, halfSize)) + 1) + totalRounds;
            fixtures.add(new Fixture(secondWeek, f.getAwayTeam(), f.getHomeTeam()));
        }
    }

    @Override
    public boolean isSeasonOver() {
        for (Fixture f : fixtures) {
            if (!f.isPlayed()) return false;
        }
        return !fixtures.isEmpty();
    }

    public void advanceWeek() {
        currentWeek++;
        for (AbstractTeam team : teams) {
            for (AbstractPlayer p : team.getInjuredPlayers()) {
                p.decrementInjury();
            }
        }
    }

    public AbstractTeam getChampion() {
        if (!isSeasonOver()) return null;
        List<TeamInLeagueTable> teamlist = getStandings();
        return teamlist.isEmpty() ? null : teamlist.get(0).getTeam();
    }

    public List<Fixture> getUnplayedFixtures() {
        List<Fixture> unplayed = new ArrayList<>();
        for (Fixture f : fixtures) {
            if (!f.isPlayed()) unplayed.add(f);
        }
        return Collections.unmodifiableList(unplayed);
    }

    // save/load restore için — injury decrement tetiklemez, haftayı doğrudan yükler
    public void setCurrentWeekDirect(int week) {
        this.currentWeek = week;
    }

    public String getName()              { return name; }
    public List<AbstractTeam> getTeams() { return Collections.unmodifiableList(teams); }
    public List<Fixture> getFixtures()   { return Collections.unmodifiableList(fixtures); }
    public int getCurrentWeek()          { return currentWeek; }
}
