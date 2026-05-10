package com.sportsmanager.sport.football;

import com.sportsmanager.core.model.AbstractPlayerAttributes;

public class FootballAttributes extends AbstractPlayerAttributes {

    private final FootballPositions position;

    //for gk's
    private double reflexes;
    private double positioning;
    private double diving;
    private double handling;

    //for on-field
    private double pace;
    private double shooting;
    private double passing;
    private double defending;
    private double physical;

    public FootballAttributes(FootballPositions position, double pace, double shooting, double passing, double defending, double physical) {
        if (position == FootballPositions.GK) {
            throw new IllegalArgumentException("On-field attributes can not be assigned to goalkeepers.");
        }
        this.position = position;
        this.pace = limiter(pace, 0, 100);
        this.shooting = limiter(shooting, 0, 100);
        this.passing = limiter(passing, 0, 100);
        this.defending = limiter(defending, 0, 100);
        this.physical = limiter(physical, 0, 100);
    }

    public FootballAttributes(FootballPositions position, double reflexes, double positioning, double diving, double handling) {
        if (position != FootballPositions.GK) {
            throw new IllegalArgumentException("Goalkeeper attributes can not be assigned to on-field players.");
        }
        this.position = position;
        this.reflexes = limiter(reflexes, 0, 100);
        this.positioning = limiter(positioning, 0, 100);
        this.diving = limiter(diving, 0, 100);
        this.handling = limiter(handling, 0, 100);
    }

    @Override
    public int computeOverallRating() {
        double rating = 0.0;
        int newRating;

        if (position == FootballPositions.GK) {
            rating = (reflexes * 0.20) + (positioning * 0.30) + (diving * 0.30) + (handling * 0.20);
        } else {
            switch (position) {
                case ST:
                    rating = (shooting * 0.40) + (pace * 0.30) + (physical * 0.20) + (passing * 0.10);
                    break;
                case CB:
                case LB:
                case RB:
                    rating = (defending * 0.40) + (physical * 0.30) + (pace * 0.18) + (passing * 0.12);
                    break;
                case CAM:
                    rating = (passing * 0.42) + (shooting * 0.33) + (pace * 0.20) + (physical * 0.05);
                    break;
                case LW:
                case RW:
                    rating = (pace * 0.40) + (shooting * 0.30) + (passing * 0.25) + (physical * 0.05);
                    break;
                default:
                    rating = (passing * 0.42) + (physical * 0.30) + (defending * 0.23) + (shooting * 0.05);
                    break;
            }
        }

        newRating = (int) (rating + 0.5);

        return (int) limiter(newRating, 0, 100);
    }

    public FootballPositions getPosition() {
        return position;
    }

    public double getReflexes() {
        return reflexes;
    }
    public double getPositioning() {
        return positioning;
    }
    public double getDiving() {
        return diving;
    }
    public double getHandling() {
        return handling;
    }

    public double getPace() {
        return pace;
    }
    public double getShooting() {
        return shooting;
    }
    public double getPassing() {
        return passing;
    }
    public double getDefending() {
        return defending;
    }
    public double getPhysical() {
        return physical;
    }

    @Override
    public void applyTrainingBoost(double intensity) {
        double boost = intensity * 0.5;

        if (position == FootballPositions.GK) {
            this.reflexes = limiter(this.reflexes + boost, 0, 100);
            this.positioning = limiter(this.positioning + boost, 0, 100);
            this.diving = limiter(this.diving + boost, 0, 100);
            this.handling = limiter(this.handling + boost, 0, 100);
        } else {
            this.pace = limiter(this.pace + boost, 0, 100);
            this.shooting = limiter(this.shooting + boost, 0, 100);
            this.passing = limiter(this.passing + boost, 0, 100);
            this.defending = limiter(this.defending + boost, 0, 100);
            this.physical = limiter(this.physical + boost, 0, 100);
        }
    }

    public void applyAttackingBoost(double intensity) {
        double boost = intensity * 0.5;
        if (position == FootballPositions.GK) return;
        this.shooting = limiter(this.shooting + boost, 0, 100);
        this.pace = limiter(this.pace + boost, 0, 100);
        this.passing = limiter(this.passing + boost, 0, 100);
    }

    public void applyDefendingBoost(double intensity) {
        double boost = intensity * 0.5;
        if (position == FootballPositions.GK) {
            this.reflexes = limiter(this.reflexes + boost, 0, 100);
            this.diving = limiter(this.diving + boost, 0, 100);
            this.handling = limiter(this.handling + boost, 0, 100);
            return;
        }
        this.defending = limiter(this.defending + boost, 0, 100);
        this.physical = limiter(this.physical + boost, 0, 100);
    }

    public void applyFitnessBoost(double intensity) {
        double boost = intensity * 0.5;
        if (position == FootballPositions.GK) {
            this.positioning = limiter(this.positioning + boost, 0, 100);
            return;
        }
        this.physical = limiter(this.physical + boost, 0, 100);
        this.pace = limiter(this.pace + boost, 0, 100);
    }

    @Override
    public void applySeasonProgression(int age, java.util.Random rng) {
        if (age <= 24) {
            double min = 1.0, max = 3.0;
            if (position == FootballPositions.GK) {
                this.reflexes    = limiter(this.reflexes    + min + rng.nextDouble() * (max - min), 0, 100);
                this.positioning = limiter(this.positioning + min + rng.nextDouble() * (max - min), 0, 100);
                this.diving      = limiter(this.diving      + min + rng.nextDouble() * (max - min), 0, 100);
                this.handling    = limiter(this.handling    + min + rng.nextDouble() * (max - min), 0, 100);
            } else {
                this.pace      = limiter(this.pace      + min + rng.nextDouble() * (max - min), 0, 100);
                this.shooting  = limiter(this.shooting  + min + rng.nextDouble() * (max - min), 0, 100);
                this.passing   = limiter(this.passing   + min + rng.nextDouble() * (max - min), 0, 100);
                this.defending = limiter(this.defending + min + rng.nextDouble() * (max - min), 0, 100);
                this.physical  = limiter(this.physical  + min + rng.nextDouble() * (max - min), 0, 100);
            }
        } else if (age <= 29) {
            double swing = 1.0;
            if (position == FootballPositions.GK) {
                this.reflexes    = limiter(this.reflexes    + (rng.nextDouble() * 2 - 1) * swing, 0, 100);
                this.positioning = limiter(this.positioning + (rng.nextDouble() * 2 - 1) * swing, 0, 100);
                this.diving      = limiter(this.diving      + (rng.nextDouble() * 2 - 1) * swing, 0, 100);
                this.handling    = limiter(this.handling    + (rng.nextDouble() * 2 - 1) * swing, 0, 100);
            } else {
                this.pace      = limiter(this.pace      + (rng.nextDouble() * 2 - 1) * swing, 0, 100);
                this.shooting  = limiter(this.shooting  + (rng.nextDouble() * 2 - 1) * swing, 0, 100);
                this.passing   = limiter(this.passing   + (rng.nextDouble() * 2 - 1) * swing, 0, 100);
                this.defending = limiter(this.defending + (rng.nextDouble() * 2 - 1) * swing, 0, 100);
                this.physical  = limiter(this.physical  + (rng.nextDouble() * 2 - 1) * swing, 0, 100);
            }
        } else if (age <= 34) {
            double min = -1.0, max = -3.0;
            if (position == FootballPositions.GK) {
                this.reflexes    = limiter(this.reflexes    + min + rng.nextDouble() * (max - min), 0, 100);
                this.positioning = limiter(this.positioning + min + rng.nextDouble() * (max - min), 0, 100);
                this.diving      = limiter(this.diving      + min + rng.nextDouble() * (max - min), 0, 100);
                this.handling    = limiter(this.handling    + min + rng.nextDouble() * (max - min), 0, 100);
            } else {
                this.pace      = limiter(this.pace      - (1.0 + rng.nextDouble() * 3.0), 0, 100);
                this.physical  = limiter(this.physical  - (1.0 + rng.nextDouble() * 3.0), 0, 100);
                this.shooting  = limiter(this.shooting  + min + rng.nextDouble() * (max - min), 0, 100);
                this.passing   = limiter(this.passing   + min + rng.nextDouble() * (max - min), 0, 100);
                this.defending = limiter(this.defending + min + rng.nextDouble() * (max - min), 0, 100);
            }
        } else {
            double min = -2.0, max = -5.0;
            if (position == FootballPositions.GK) {
                this.reflexes    = limiter(this.reflexes    + min + rng.nextDouble() * (max - min), 0, 100);
                this.positioning = limiter(this.positioning + min + rng.nextDouble() * (max - min), 0, 100);
                this.diving      = limiter(this.diving      + min + rng.nextDouble() * (max - min), 0, 100);
                this.handling    = limiter(this.handling    + min + rng.nextDouble() * (max - min), 0, 100);
            } else {
                this.pace      = limiter(this.pace      - (2.0 + rng.nextDouble() * 4.0), 0, 100);
                this.physical  = limiter(this.physical  - (2.0 + rng.nextDouble() * 4.0), 0, 100);
                this.shooting  = limiter(this.shooting  + min + rng.nextDouble() * (max - min), 0, 100);
                this.passing   = limiter(this.passing   + min + rng.nextDouble() * (max - min), 0, 100);
                this.defending = limiter(this.defending + min + rng.nextDouble() * (max - min), 0, 100);
            }
        }
    }

}
