package com.sportsmanager.sport.basketball;

import com.sportsmanager.core.model.AbstractPlayer;
import com.sportsmanager.core.model.AbstractTactic;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class BasketballTactic extends AbstractTactic {

    private final Map<BasketballPositions, Integer> requiredPositions;

    public BasketballTactic(String name, double attackingWeight, double pressureIntensity, Map<BasketballPositions, Integer> requiredPositions) {
        super(name, attackingWeight, pressureIntensity);
        this.requiredPositions = requiredPositions;
    }

    public BasketballTactic(String name, double attackingWeight, double pressureIntensity) {
        this(name, attackingWeight, pressureIntensity, getStandardLineup());
    }

    public Map<BasketballPositions, Integer> getRequiredPositions() {
        return requiredPositions;
    }

    @Override
    public Map<String, Integer> getRequiredPositionNames() {
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<BasketballPositions, Integer> e : requiredPositions.entrySet()) {
            result.put(e.getKey().name(), e.getValue());
        }
        return result;
    }

    private static Map<BasketballPositions, Integer> getStandardLineup() {
        Map<BasketballPositions, Integer> pos = new HashMap<>();
        pos.put(BasketballPositions.PG, 1);
        pos.put(BasketballPositions.SG, 1);
        pos.put(BasketballPositions.SF, 1);
        pos.put(BasketballPositions.PF, 1);
        pos.put(BasketballPositions.C, 1);
        return pos;
    }

    public static BasketballTactic createOffensive() {
        return new BasketballTactic("Offensive", 0.70, 0.60, getStandardLineup());
    }

    public static BasketballTactic createDefensive() {
        return new BasketballTactic("Defensive", 0.30, 0.40, getStandardLineup());
    }

    public static BasketballTactic createBalanced() {
        return new BasketballTactic("Balanced", 0.50, 0.50, getStandardLineup());
    }

    @Override
    public String getFormationString() {
        return getName();
    }

    @Override
    public void validateForSquad(List<AbstractPlayer> squad) {
        Map<BasketballPositions, Integer> availablePositions = new HashMap<>();

        for (AbstractPlayer p : squad) {
            if (p instanceof BasketballPlayer bp && !bp.isInjured()) {
                BasketballPositions pos = bp.getBasketballPosition();
                availablePositions.put(pos, availablePositions.getOrDefault(pos, 0) + 1);
            }
        }

        for (Map.Entry<BasketballPositions, Integer> entry : requiredPositions.entrySet()) {
            BasketballPositions requiredPos = entry.getKey();
            int requiredCount = entry.getValue();
            int availableCount = availablePositions.getOrDefault(requiredPos, 0);

            if (availableCount < requiredCount) {
                throw new IllegalArgumentException("Tactic error! " + this.getName() + " Insufficient " + requiredPos + " Required " + requiredCount + " Current " + availableCount);
            }
        }
    }
}