package com.sportsmanager.sport.football;

import com.sportsmanager.core.interfaces.IMatchEngine;
import com.sportsmanager.core.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FootballMatchEngine implements IMatchEngine {

    private final Random rng = new Random();

    @Override
    public PeriodResult simulatePeriod(AbstractTeam home, AbstractTeam away) {
        // takımın ovr ortalamasını alıp taktik çarpanıyla poisson'a sokuyoruz
        double homeLambda = calcLambda(home);
        double awayLambda = calcLambda(away);

        int homeGoals = poisson(homeLambda);
        int awayGoals = poisson(awayLambda);

        return new PeriodResult(homeGoals, awayGoals);
    }

    //her oyuncu için %3 sakatlık şansı var
      //%60 hafif, %30 orta, %10 ağır
    @Override
    public List<Injury> determineInjuries(AbstractTeam home, AbstractTeam away) {
        List<Injury> injuries = new ArrayList<>();

        checkTeamInjuries(home, injuries);
        checkTeamInjuries(away, injuries);

        return injuries;
    }

    @Override
    public double calculatePlayerRating(AbstractPlayer player) {
        if (player.getAttributes() == null) return 50.0;
        // ovr üstüne biraz şans faktörü ekliyoruz
        double base = player.getAttributes().getOverallRating();
        double variation = (rng.nextDouble() - 0.5) * 10; // +- 5
        return Math.max(0, Math.min(100, base + variation));
    }

    // λ = (avgOVR / 100) * tacticMod * 1.5
    private double calcLambda(AbstractTeam team) {
        List<AbstractPlayer> available = team.getAvailablePlayers();
        if (available.isEmpty()) return 0.3; // kadro boşsa bile bişeyler olsun

        double totalOvr = 0;
        for (AbstractPlayer p : available) {
            if (p.getAttributes() != null) {
                totalOvr += p.getAttributes().getOverallRating();
            }
        }
        double avgOvr = totalOvr / available.size();

        // taktik atanmamışsa 1.0 kabul ediyoruz
        double tacticMod = 1.0;
        if (team.getCurrentTactic() != null) {
            tacticMod = team.getCurrentTactic().getOffensiveModifier();
        }

        return (avgOvr / 100.0) * tacticMod * 1.5;
    }

    private int poisson(double lambda) {
        double l = Math.exp(-lambda);
        int k = 0;
        double p = 1.0;
        do {
            k++;
            p *= rng.nextDouble();
        } while (p > l);
        return k - 1;
    }

    private void checkTeamInjuries(AbstractTeam team, List<Injury> injuries) {
        for (AbstractPlayer player : team.getAvailablePlayers()) {
            if (rng.nextDouble() < 0.03) {
                Injury.Severity sev = rollSeverity();
                int games = gamesForSeverity(sev);
                Injury inj = new Injury(sev, games);
                player.setInjury(inj);
                injuries.add(inj);
            }
        }
    }

    private Injury.Severity rollSeverity() {
        double roll = rng.nextDouble();
        if (roll < 0.60) return Injury.Severity.MINOR;
        if (roll < 0.90) return Injury.Severity.MODERATE;
        return Injury.Severity.SERIOUS;
    }

    // sakatlığa göre kaç maç kaçıracak
    private int gamesForSeverity(Injury.Severity sev) {
        switch (sev) {
            case MINOR: return 1 + rng.nextInt(2);     // 1-2 maç
            case MODERATE: return 3 + rng.nextInt(3);   // 3-5 maç
            case SERIOUS: return 6 + rng.nextInt(5);    // 6-10 maç, acı
            default: return 1;
        }
    }
}
