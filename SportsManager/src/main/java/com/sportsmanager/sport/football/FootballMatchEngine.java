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
        double homeLambda = calcLambda(home, away);
        double awayLambda = calcLambda(away, home);

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

    // λ = (avgOVR / 100) * offensiveMod * defPenalty * 1.5
    private double calcLambda(AbstractTeam team, AbstractTeam opponent) {
        if (team.getAvailablePlayers().isEmpty()) return 0.3; // kadro boşsa bile bişeyler olsun

        double avgOvr = team.getAvailableAvgOvr(11);

        // taktik atanmamışsa 1.0 kabul ediyoruz
        double offensiveMod = 1.0;
        double defPenalty = 1.0;
        if (team.getCurrentTactic() != null) {
            offensiveMod = team.getCurrentTactic().getOffensiveModifier();
        }
        if (opponent.getCurrentTactic() != null) {
            // rakip savunması güçlüyse (1.20) → defPenalty = 0.80 → daha az gol
            // rakip savunması zayıfsa (0.80) → defPenalty = 1.20 → daha çok gol
            defPenalty = 2.0 - opponent.getCurrentTactic().getDefensiveModifier();
        }

        offensiveMod *= (1.0 + coachOffensiveBonus(team.getActiveCoach()));
        defPenalty   /= (1.0 + coachDefensiveBonus(opponent.getActiveCoach()));

        return (avgOvr / 100.0) * offensiveMod * defPenalty * 1.5;
    }

    private double coachOffensiveBonus(AbstractCoach coach) {
        if (coach == null) return 0;
        if (coach.getSpecialty() == CoachSpecialty.ATTACKING) return coach.getCoachingLevel() * 0.02;
        if (coach.getSpecialty() == CoachSpecialty.GENERAL)   return coach.getCoachingLevel() * 0.01;
        return 0;
    }

    private double coachDefensiveBonus(AbstractCoach coach) {
        if (coach == null) return 0;
        if (coach.getSpecialty() == CoachSpecialty.DEFENDING) return coach.getCoachingLevel() * 0.02;
        if (coach.getSpecialty() == CoachSpecialty.GENERAL)   return coach.getCoachingLevel() * 0.01;
        return 0;
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
        double chance = 0.03;
        AbstractCoach active = team.getActiveCoach();
        if (active != null && active.getSpecialty() == CoachSpecialty.FITNESS) {
            chance *= Math.max(0.10, 1.0 - active.getCoachingLevel() * 0.10);
        }
        for (AbstractPlayer player : team.getAvailablePlayers()) {
            if (rng.nextDouble() < chance) {
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
