package com.sportsmanager.sport.basketball;

import com.sportsmanager.core.interfaces.IMatchEngine;
import com.sportsmanager.core.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BasketballMatchEngine implements IMatchEngine {

    private final Random rng = new Random();

    // her quarter 20-30 puan civarı, OVR ve taktik çarpanları ile ayarlanır
    @Override
    public PeriodResult simulatePeriod(AbstractTeam home, AbstractTeam away) {
        double homeLambda = calcLambda(home, away);
        double awayLambda = calcLambda(away, home);

        int homeScore = quarterScore(homeLambda);
        int awayScore = quarterScore(awayLambda);

        return new PeriodResult(homeScore, awayScore);
    }

    // her oyuncu için %2 sakatlık şansı
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
        double base = player.getAttributes().getOverallRating();
        double variation = (rng.nextDouble() - 0.5) * 10;
        return Math.max(0, Math.min(100, base + variation));
    }

    // λ = (avgOVR/100) * offensiveMod * defPenalty * 25  →  quarter ortalaması 20-30 puan
    private double calcLambda(AbstractTeam team, AbstractTeam opponent) {
        if (team.getAvailablePlayers().isEmpty()) return 15.0;

        double avgOvr = team.getStartingLineup().isEmpty()
                ? team.getAvailableAvgOvr(5)
                : team.getStartingAvgOvr();

        double offensiveMod = 1.0;
        double defPenalty   = 1.0;
        if (team.getCurrentTactic() != null) {
            offensiveMod = team.getCurrentTactic().getOffensiveModifier();
        }
        if (opponent.getCurrentTactic() != null) {
            defPenalty = 2.0 - opponent.getCurrentTactic().getDefensiveModifier();
        }

        offensiveMod *= (1.0 + coachOffensiveBonus(team.getActiveCoach()));
        defPenalty   /= (1.0 + coachDefensiveBonus(opponent.getActiveCoach()));

        return (avgOvr / 100.0) * offensiveMod * defPenalty * 25.0;
    }

    // 2'li ve 3'lü atışları karıştırarak quarter skoru üret
    private int quarterScore(double lambda) {
        int score = 0;
        // hedef puan sayısını Gaussian ile belirle (min 10, max 40)
        int targetPoints = (int) Math.round(lambda + (rng.nextGaussian() * 4));
        targetPoints = Math.max(10, Math.min(40, targetPoints));

        while (score < targetPoints) {
            int remaining = targetPoints - score;
            if (remaining <= 0) break;
            // %40 üçlük, %60 ikili
            int shot = (rng.nextDouble() < 0.40) ? 3 : 2;
            score += shot;
        }
        return score;
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

    private void checkTeamInjuries(AbstractTeam team, List<Injury> injuries) {
        double chance = 0.02;
        AbstractCoach active = team.getActiveCoach();
        if (active != null && active.getSpecialty() == CoachSpecialty.FITNESS) {
            chance *= Math.max(0.10, 1.0 - active.getCoachingLevel() * 0.10);
        }
        List<AbstractPlayer> onCourt = team.getStartingLineup().isEmpty()
                ? team.getAvailablePlayers() : team.getStartingLineup();
        for (AbstractPlayer player : onCourt) {
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

    private int gamesForSeverity(Injury.Severity sev) {
        switch (sev) {
            case MINOR:    return 1 + rng.nextInt(2);
            case MODERATE: return 3 + rng.nextInt(3);
            case SERIOUS:  return 6 + rng.nextInt(5);
            default:       return 1;
        }
    }
}
