package com.sportsmanager.core.model;

import com.sportsmanager.core.interfaces.ILeague;

import java.util.*;

public abstract class AbstractLeague implements ILeague {

    private final String name;
    private final List<AbstractTeam> teams;
    private final List<Fixture> fixtures;
    private final Map<UUID, TeamInLeagueTable> standingsMap;
    private int currentWeek;

    public AbstractLeague(String name) {
        this.name = name;
        this.teams = new ArrayList<>();
        this.fixtures = new ArrayList<>();
        this.standingsMap = new LinkedHashMap<>();
        this.currentWeek = 1;
    }

    protected abstract int getWinPoints();
    protected abstract int getDrawPoints();

    // FootballMatch vs dondurecek
    protected abstract AbstractMatch createMatch(AbstractTeam home, AbstractTeam away);

    @Override
    public List<TeamInLeagueTable> getStandings() {
        List<TeamInLeagueTable> sorted = new ArrayList<>(standingsMap.values());
        sorted.sort((a, b) -> {
            if (a.getPoints() != b.getPoints()) {
                return Integer.compare(b.getPoints(), a.getPoints());
            }
            if (a.getGoalDifference() != b.getGoalDifference()) {
                return Integer.compare(b.getGoalDifference(), a.getGoalDifference());
            }
            // esitlik bozulamazsa kura cek
            return new Random().nextBoolean() ? 1 : -1;
        });
        return Collections.unmodifiableList(sorted);
    }

    @Override
    public void recordResult(MatchResult result) {
        if (result == null) {
            throw new IllegalArgumentException("Result cannot be null");
        }

        // ilk oynanmamis fixture'i bul
        Fixture fixture = null;
        for (Fixture f : fixtures) {
            if (!f.isPlayed()) {
                fixture = f;
                break;
            }
        }
        if (fixture == null) {
            throw new IllegalStateException("No unplayed fixtures remaining");
        }

        fixture.setResult(result);

        TeamInLeagueTable homeEntry = standingsMap.get(fixture.getHomeTeam().getId());
        TeamInLeagueTable awayEntry = standingsMap.get(fixture.getAwayTeam().getId());

        if (homeEntry == null || awayEntry == null) {
            throw new IllegalStateException("Team not found in standings");
        }

        homeEntry.addResult(result.isHomeWin(), result.isDraw(),
                result.getHomeScore(), result.getAwayScore(),
                getWinPoints(), getDrawPoints());

        awayEntry.addResult(result.isAwayWin(), result.isDraw(),
                result.getAwayScore(), result.getHomeScore(),
                getWinPoints(), getDrawPoints());
    }
    @Override
    public void generateFixtures(List<AbstractTeam> teamList) {
        if (teamList == null || teamList.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 teams for fixtures");
        }

        this.teams.clear();
        this.teams.addAll(teamList);
        this.fixtures.clear();
        this.standingsMap.clear();
        this.currentWeek = 1;

        for (AbstractTeam team : teams) {
            standingsMap.put(team.getId(), new TeamInLeagueTable(team));
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

        // 2.tur  ev sahibi karşya geçer
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
    }

    // sezon bitmeden null doner
    public AbstractTeam getChampion() {
        if (!isSeasonOver()) return null;
        List<TeamInLeagueTable> standings = getStandings();
        return standings.isEmpty() ? null : standings.get(0).getTeam();
    }

    public List<Fixture> getUnplayedFixtures() {
        List<Fixture> unplayed = new ArrayList<>();
        for (Fixture f : fixtures) {
            if (!f.isPlayed()) unplayed.add(f);
        }
        return Collections.unmodifiableList(unplayed);
    }

    public String getName()              { return name; }
    public List<AbstractTeam> getTeams() { return Collections.unmodifiableList(teams); }
    public List<Fixture> getFixtures()   { return Collections.unmodifiableList(fixtures); }
    public int getCurrentWeek()          { return currentWeek; }
}
