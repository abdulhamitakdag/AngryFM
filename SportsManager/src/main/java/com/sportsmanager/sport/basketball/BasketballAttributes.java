package com.sportsmanager.sport.basketball;

import com.sportsmanager.core.model.AbstractPlayerAttributes;

public class BasketballAttributes extends AbstractPlayerAttributes {

    private final BasketballPositions position;

    private double shooting;
    private double playmaking;
    private double defending;
    private double rebounding;
    private double physical;

    public BasketballAttributes(BasketballPositions position, double shooting, double playmaking, double defending, double rebounding, double physical) {
        this.position = position;
        this.shooting = limiter(shooting, 0, 100);
        this.playmaking = limiter(playmaking, 0, 100);
        this.defending = limiter(defending, 0, 100);
        this.rebounding = limiter(rebounding, 0, 100);
        this.physical = limiter(physical, 0, 100);
    }

    public BasketballPositions getPosition() { return position; }
    public double getShooting() { return shooting; }
    public double getPlaymaking() { return playmaking; }
    public double getDefending() { return defending; }
    public double getRebounding() { return rebounding; }
    public double getPhysical() { return physical; }

    @Override
    public int computeOverallRating() {
        double rating = 0.0;

        switch (position) {
            case PG:
                rating = (shooting * 0.30) + (playmaking * 0.40) + (defending * 0.10) + (rebounding * 0.05) + (physical * 0.15);
                break;
            case SG:
                rating = (shooting * 0.35) + (playmaking * 0.25) + (defending * 0.15) + (rebounding * 0.05) + (physical * 0.20);
                break;
            case SF:
                rating = (shooting * 0.25) + (playmaking * 0.15) + (defending * 0.20) + (rebounding * 0.15) + (physical * 0.25);
                break;
            case PF:
                rating = (shooting * 0.15) + (playmaking * 0.05) + (defending * 0.25) + (rebounding * 0.30) + (physical * 0.25);
                break;
            case C:
                rating = (shooting * 0.10) + (playmaking * 0.00) + (defending * 0.30) + (rebounding * 0.35) + (physical * 0.25);
                break;
        }

        int newRating = (int) (rating + 0.5);

        return (int) limiter(newRating, 0, 100);
    }

    @Override
    public void applyTrainingBoost(double intensity) {
        double boost = intensity * 0.4;

        this.shooting = limiter(this.shooting + boost, 0, 100);
        this.playmaking = limiter(this.playmaking + boost, 0, 100);
        this.defending = limiter(this.defending + boost, 0, 100);
        this.rebounding = limiter(this.rebounding + boost, 0, 100);
        this.physical = limiter(this.physical + boost, 0, 100);
    }

    @Override
    public void applySeasonProgression(int age, java.util.Random rng) {
        if (age <= 24) {
            double min = 1.0, max = 3.0;
            this.shooting   = limiter(this.shooting   + min + rng.nextDouble() * (max - min), 0, 100);
            this.playmaking = limiter(this.playmaking + min + rng.nextDouble() * (max - min), 0, 100);
            this.defending  = limiter(this.defending  + min + rng.nextDouble() * (max - min), 0, 100);
            this.rebounding = limiter(this.rebounding + min + rng.nextDouble() * (max - min), 0, 100);
            this.physical   = limiter(this.physical   + min + rng.nextDouble() * (max - min), 0, 100);
        } else if (age <= 29) {
            double swing = 1.0;
            this.shooting   = limiter(this.shooting   + (rng.nextDouble() * 2 - 1) * swing, 0, 100);
            this.playmaking = limiter(this.playmaking + (rng.nextDouble() * 2 - 1) * swing, 0, 100);
            this.defending  = limiter(this.defending  + (rng.nextDouble() * 2 - 1) * swing, 0, 100);
            this.rebounding = limiter(this.rebounding + (rng.nextDouble() * 2 - 1) * swing, 0, 100);
            this.physical   = limiter(this.physical   + (rng.nextDouble() * 2 - 1) * swing, 0, 100);
        } else if (age <= 34) {
            this.physical   = limiter(this.physical   - (1.0 + rng.nextDouble() * 3.0), 0, 100);
            this.rebounding = limiter(this.rebounding - (1.0 + rng.nextDouble() * 2.0), 0, 100);
            this.shooting   = limiter(this.shooting   - (1.0 + rng.nextDouble() * 2.0), 0, 100);
            this.playmaking = limiter(this.playmaking - (0.5 + rng.nextDouble() * 2.0), 0, 100);
            this.defending  = limiter(this.defending  - (0.5 + rng.nextDouble() * 2.0), 0, 100);
        } else {
            this.physical   = limiter(this.physical   - (2.0 + rng.nextDouble() * 4.0), 0, 100);
            this.rebounding = limiter(this.rebounding - (2.0 + rng.nextDouble() * 3.0), 0, 100);
            this.shooting   = limiter(this.shooting   - (2.0 + rng.nextDouble() * 3.0), 0, 100);
            this.playmaking = limiter(this.playmaking - (1.0 + rng.nextDouble() * 3.0), 0, 100);
            this.defending  = limiter(this.defending  - (1.0 + rng.nextDouble() * 3.0), 0, 100);
        }
    }
}