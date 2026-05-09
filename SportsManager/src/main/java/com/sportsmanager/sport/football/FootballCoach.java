package com.sportsmanager.sport.football;

import com.sportsmanager.core.model.AbstractCoach;
import com.sportsmanager.core.model.AbstractPlayer;
import com.sportsmanager.core.model.AbstractTeam;
import com.sportsmanager.core.model.CoachSpecialty;
import com.sportsmanager.core.model.Gender;


public class FootballCoach extends AbstractCoach {
    public FootballCoach(String name, int age, Gender gender, CoachSpecialty specialty, int coachingLevel) {
        super(name, age, gender, specialty, coachingLevel);
    }

    @Override
    public double specialtyMultiplier() {
        return 1.0 + ((this.getCoachingLevel() - 1) * 0.125);
        // for level 1: 1
        // for level 2: 1.125
        // for level 3: 1.250
        // for level 4: 1.375
        // for level 5: 1.5
    }

    @Override
    public void conductTraining(AbstractTeam team, double intensity) {
        double effectiveIntensity = intensity * getTrainingEffectiveness() * specialtyMultiplier();
        double specBoost = intensity * 0.6;

        for (AbstractPlayer player : team.getAvailablePlayers()) {
            player.train(effectiveIntensity);

            if (!(player instanceof FootballPlayer)) continue;
            FootballPlayer fp = (FootballPlayer) player;
            if (!(fp.getAttributes() instanceof FootballAttributes)) continue;
            FootballAttributes attrs = (FootballAttributes) fp.getAttributes();
            FootballPositions pos = fp.getFootballPosition();

            switch (getSpecialty()) {
                case ATTACKING:
                    if (isAttacker(pos)) attrs.applyAttackingBoost(specBoost);
                    break;
                case DEFENDING:
                    if (isDefender(pos)) attrs.applyDefendingBoost(specBoost);
                    break;
                case FITNESS:
                    attrs.applyFitnessBoost(specBoost);
                    break;
                case GENERAL:
                default:
                    break;
            }
        }
    }

    private boolean isAttacker(FootballPositions pos) {
        return pos == FootballPositions.ST
            || pos == FootballPositions.LW
            || pos == FootballPositions.RW
            || pos == FootballPositions.CAM;
    }

    private boolean isDefender(FootballPositions pos) {
        return pos == FootballPositions.CB
            || pos == FootballPositions.LB
            || pos == FootballPositions.RB
            || pos == FootballPositions.CDM;
    }
}