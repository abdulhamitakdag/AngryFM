package com.sportsmanager.sport.football;

import com.sportsmanager.core.model.AbstractPlayer;
import com.sportsmanager.core.model.AbstractTactic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FootballTactic extends AbstractTactic {

    private final Map<FootballPositions, Integer> requiredPositions;

    public FootballTactic(String name, double attackingWeight, double pressureIntensity, Map<FootballPositions, Integer> requiredPositions) {
        super(name, attackingWeight, pressureIntensity);
        this.requiredPositions = requiredPositions;
    }

    @Override
    public String getFormationString() {
        return this.getName();
    }

    @Override
    public void validateForSquad(List<AbstractPlayer> squad) {
        Map<FootballPositions, Integer> availablePositions = new HashMap<>();

        for (AbstractPlayer p : squad) {
            if (p instanceof FootballPlayer) {
                FootballPlayer fp = (FootballPlayer) p;
                if (!fp.isInjured()) {
                    FootballPositions pos = fp.getFootballPosition();
                    availablePositions.put(pos, availablePositions.getOrDefault(pos, 0) + 1);
                }
            }
        }

        for (Map.Entry<FootballPositions, Integer> entry : requiredPositions.entrySet()) {
            FootballPositions requiredPos = entry.getKey();
            int requiredCount = entry.getValue();
            int availableCount = availablePositions.getOrDefault(requiredPos, 0);

            if (availableCount < requiredCount) {
                throw new IllegalArgumentException("Tactic error! " + this.getName() + " Insufficient " + requiredPos + " Required " + requiredCount + " Current " + availableCount);
            }
        }
    }


    public static FootballTactic create442() {
        Map<FootballPositions, Integer> pos = new HashMap<>();
        pos.put(FootballPositions.GK, 1);
        pos.put(FootballPositions.LB, 1); pos.put(FootballPositions.CB, 2); pos.put(FootballPositions.RB, 1);
        pos.put(FootballPositions.CM, 2); pos.put(FootballPositions.LW, 1); pos.put(FootballPositions.RW, 1);
        pos.put(FootballPositions.ST, 2);
        return new FootballTactic("4-4-2", 0.50, 0.50, pos);
    }

    public static FootballTactic create4231() {
        Map<FootballPositions, Integer> pos = new HashMap<>();
        pos.put(FootballPositions.GK, 1);
        pos.put(FootballPositions.LB, 1); pos.put(FootballPositions.CB, 2); pos.put(FootballPositions.RB, 1);
        pos.put(FootballPositions.CDM, 2);
        pos.put(FootballPositions.CAM, 1); pos.put(FootballPositions.LW, 1); pos.put(FootballPositions.RW, 1);
        pos.put(FootballPositions.ST, 1);
        return new FootballTactic("4-2-3-1", 0.55, 0.60, pos);
    }

    public static FootballTactic create433() {
        Map<FootballPositions, Integer> pos = new HashMap<>();
        pos.put(FootballPositions.GK, 1);
        pos.put(FootballPositions.LB, 1); pos.put(FootballPositions.CB, 2); pos.put(FootballPositions.RB, 1);
        pos.put(FootballPositions.CDM, 1); pos.put(FootballPositions.CM, 2);
        pos.put(FootballPositions.LW, 1); pos.put(FootballPositions.RW, 1); pos.put(FootballPositions.ST, 1);
        return new FootballTactic("4-3-3", 0.65, 0.75, pos);
    }

    public static FootballTactic create424() {
        Map<FootballPositions, Integer> pos = new HashMap<>();
        pos.put(FootballPositions.GK, 1);
        pos.put(FootballPositions.LB, 1); pos.put(FootballPositions.CB, 2); pos.put(FootballPositions.RB, 1);
        pos.put(FootballPositions.CM, 2);
        pos.put(FootballPositions.LW, 1); pos.put(FootballPositions.RW, 1); pos.put(FootballPositions.ST, 2);
        return new FootballTactic("4-2-4", 0.75, 0.80, pos);
    }

    public static FootballTactic create352() {
        Map<FootballPositions, Integer> pos = new HashMap<>();
        pos.put(FootballPositions.GK, 1);
        pos.put(FootballPositions.CB, 3);
        pos.put(FootballPositions.CDM, 2); pos.put(FootballPositions.CAM, 1);
        pos.put(FootballPositions.LW, 1); pos.put(FootballPositions.RW, 1);
        pos.put(FootballPositions.ST, 2);
        return new FootballTactic("3-5-2", 0.60, 0.50, pos);
    }

    public static FootballTactic create343() {
        Map<FootballPositions, Integer> pos = new HashMap<>();
        pos.put(FootballPositions.GK, 1);
        pos.put(FootballPositions.CB, 3);
        pos.put(FootballPositions.CDM, 2); pos.put(FootballPositions.CM, 2);
        pos.put(FootballPositions.LW, 1); pos.put(FootballPositions.RW, 1); pos.put(FootballPositions.ST, 1);
        return new FootballTactic("3-4-3", 0.45, 0.65, pos);
    }

    public static FootballTactic create532() {
        Map<FootballPositions, Integer> pos = new HashMap<>();
        pos.put(FootballPositions.GK, 1);
        pos.put(FootballPositions.LB, 1); pos.put(FootballPositions.CB, 3); pos.put(FootballPositions.RB, 1);
        pos.put(FootballPositions.CDM, 1); pos.put(FootballPositions.CM, 2);
        pos.put(FootballPositions.ST, 2);
        return new FootballTactic("5-3-2", 0.35, 0.30, pos);
    }

    public static FootballTactic create541() {
        Map<FootballPositions, Integer> pos = new HashMap<>();
        pos.put(FootballPositions.GK, 1);
        pos.put(FootballPositions.LB, 1); pos.put(FootballPositions.CB, 3); pos.put(FootballPositions.RB, 1);
        pos.put(FootballPositions.CM, 2); pos.put(FootballPositions.LW, 1); pos.put(FootballPositions.RW, 1);
        pos.put(FootballPositions.ST, 1);
        return new FootballTactic("5-4-1", 0.20, 0.20, pos);
    }
}