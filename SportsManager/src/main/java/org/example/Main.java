package org.example;

import com.sportsmanager.core.interfaces.ISport;
import com.sportsmanager.core.model.*;
import com.sportsmanager.factory.SportFactory;
import com.sportsmanager.sport.football.*;
import com.sportsmanager.util.RandomGenerator;

import java.util.ArrayList;
import java.util.List;

import static com.sportsmanager.util.RandomGenerator.DEFAULT_GENDER;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== AngryFM - Football Manager Simulation ===\n");
        ISport sport = SportFactory.createSport("football");
        /* frontend'e taşınırken bu kısımda user inputla hangi cinsiyetin ligini
        oynamak istediğini soracağız ve userın inputladığı cinsiyeti
        generateTeam(sport, gender) parametresine alıp ona göre yaptırtacağız:
        */

        /*
        Gender gender;
        */

        // 4 takim olustur
        int teamCount = 4;
        List<AbstractTeam> teams = new ArrayList<>();
        for (int i = 0; i < teamCount; i++) {
            //frontende taşınınca buradaki DEFAULT_GENDER'ın yerini, değerini user inputtan alan gender variable'ı alacak
            AbstractTeam team = RandomGenerator.generateTeam(sport, DEFAULT_GENDER);
            team.setCurrentTactic(sport.createDefaultTactic());
            teams.add(team);
        }

        System.out.println("Teams:");
        for (AbstractTeam team : teams) {
            int avgOvr = 0;
            List<AbstractPlayer> squad = team.getSquad();
            for (AbstractPlayer p : squad) {
                if (p.getAttributes() != null) {
                    avgOvr += p.getAttributes().getOverallRating();
                }
            }
            avgOvr = squad.isEmpty() ? 0 : avgOvr / squad.size();
            System.out.printf("  %-25s | Squad: %d | Avg OVR: %d | Tactic: %s%n",
                    team.getName(), team.getCurrentSquadSize(), avgOvr,
                    team.getCurrentTactic() != null ? team.getCurrentTactic().getName() : "None");
        }

        // Lig olustur ve fiksturu generate et
        FootballLeague league = new FootballLeague("AngryFM Super League");

        /*frontende taşınınca üsttekini silip bunlar aktive edilmeli, lig isimleri cinsiyete göre ayrılır
        if(gender==Gender.MALE){FootballLeague league= new FootballLeague("AngryFM Men's Super League");}
        * if(gender==Gender.FEMALE){FootballLeague league= new FootballLeague("AngryFM Women's Super League");}
        * */
        league.generateFixtures(teams);

        System.out.printf("%nLeague: %s%n", league.getName());
        System.out.printf("Total fixtures: %d matches%n%n", league.getFixtures().size());

        // Tum maclari hafta hafta oyna
        int matchesPlayed = 0;
        while (!league.isSeasonOver()) {
            Fixture next = league.getUnplayedFixtures().get(0);

            FootballMatch match = new FootballMatch(next.getHomeTeam(), next.getAwayTeam());
            match.start();
            match.simulateCurrentPeriod();    // 1. devre
            match.resumeAfterBreak();
            match.simulateCurrentPeriod();    // 2. devre

            MatchResult result = match.getMatchResult();
            league.recordResult(result);
            matchesPlayed++;

            System.out.printf("  Week %d: %-20s %d - %d %-20s%n",
                    next.getWeek(),
                    result.getHomeTeam().getName(), result.getHomeScore(),
                    result.getAwayScore(), result.getAwayTeam().getName());
        }

        // Puan tablosu
        System.out.println("\n=== LEAGUE TABLE ===");
        System.out.printf("%-4s %-25s %4s %4s %4s %4s %6s %6s %6s%n",
                "#", "Team", "W", "D", "L", "Pts", "GF", "GA", "GD");
        System.out.println("-".repeat(85));

        List<TeamInLeagueTable> standings = league.getStandings();
        for (int i = 0; i < standings.size(); i++) {
            TeamInLeagueTable entry = standings.get(i);
            System.out.printf("%-4d %-25s %4d %4d %4d %4d %6d %6d %6d%n",
                    i + 1,
                    entry.getTeam().getName(),
                    entry.getWins(), entry.getDraws(), entry.getLosses(),
                    entry.getPoints(),
                    entry.getGoalsScored(), entry.getGoalsConceded(),
                    entry.getGoalDifference());
        }

        // Sampiyon
        AbstractTeam champion = league.getChampion();
        System.out.printf("%n=== CHAMPION: %s ===%n", champion != null ? champion.getName() : "Undetermined");

        // Sakatlik raporu
        System.out.println("\nInjury Report:");
        boolean anyInjury = false;
        for (AbstractTeam team : teams) {
            List<AbstractPlayer> injured = team.getInjuredPlayers();
            if (!injured.isEmpty()) {
                anyInjury = true;
                for (AbstractPlayer p : injured) {
                    System.out.printf("  %-25s | %-20s | %s | %d games remaining%n",
                            team.getName(), p.getName(),
                            p.getInjury().getSeverity(),
                            p.getInjury().getGamesRemaining());
                }
            }
        }
        if (!anyInjury) {
            System.out.println("  No injured players.");
        }

        System.out.printf("%nSimulation complete. Total %d matches played.%n", matchesPlayed);
    }
}
