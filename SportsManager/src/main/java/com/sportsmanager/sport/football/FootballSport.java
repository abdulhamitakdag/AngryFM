package com.sportsmanager.sport.football;

import com.sportsmanager.core.interfaces.IMatchEngine;
import com.sportsmanager.core.interfaces.ISport;
import com.sportsmanager.core.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FootballSport implements ISport {

    private final Random rng = new Random();

    @Override
    public String getSportId() {
        return "football";
    }

    @Override
    public int getPlayersOnField() {
        return 11;
    }

    @Override
    public int getWinPoints() {
        return 3;
    }

    @Override
    public boolean allowsDraw() {
        return true;
    }

    @Override
    public IMatchEngine createMatchEngine() {
        return new FootballMatchEngine();
    }

    @Override
    public List<String> getValidPositions() {
        List<String> positions = new ArrayList<>();
        for (FootballPositions pos : FootballPositions.values()) {
            positions.add(pos.name());
        }
        return positions;
    }


    //(Ben Selin) ISport'a eklediğim metodu implementledim
    @Override
    public AbstractPlayer createPlayer(String name, int age, Gender gender, int shirtNumber, String position, AbstractPlayerAttributes attributes) {
        FootballPositions pos = FootballPositions.valueOf(position.toUpperCase());
        return new FootballPlayer(name, age, gender, shirtNumber, pos, attributes);
    }

    @Override
    public AbstractTeam createTeam(String name) {
        return null;
    }

    @Override
    public AbstractCoach createCoach(String name, int age, Gender gender, CoachSpecialty specialty, int coachingLevel) {
        return null;
    }

    @Override
    public AbstractTactic createDefaultTactic() {
        return null;
    }

    @Override
    public int getRecommendedSquadSize() {
        return 0;
    }

    @Override
    public int getRecommendedCoachCount() {
        return 0;
    }


    @Override
    public AbstractPlayerAttributes generateRandomAttributes(String position) {
        FootballPositions pos;
        try {
            pos = FootballPositions.valueOf(position.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown position: " + position);
        }

        if (pos == FootballPositions.GK) {
            return new FootballAttributes(pos,
                    randStat(60, 95),   // reflexes
                    randStat(55, 90),   // positioning
                    randStat(60, 95),   // diving
                    randStat(55, 85));  // handling
        }

        // saha oyuncuları
        double pace, shooting, passing, defending, physical;

        switch (pos) {
            case ST:
                pace = randStat(55, 95);
                shooting = randStat(65, 95);
                passing = randStat(40, 75);
                defending = randStat(20, 45);
                physical = randStat(50, 85);
                break;
            case CAM:
            case CM:
                pace = randStat(45, 80);
                shooting = randStat(50, 80);
                passing = randStat(65, 95);
                defending = randStat(35, 65);
                physical = randStat(45, 75);
                break;
            case LW:
            case RW:
                pace = randStat(70, 97);
                shooting = randStat(55, 85);
                passing = randStat(55, 85);
                defending = randStat(20, 45);
                physical = randStat(40, 70);
                break;
            case CB:
                pace = randStat(35, 70);
                shooting = randStat(20, 45);
                passing = randStat(35, 65);
                defending = randStat(65, 95);
                physical = randStat(60, 90);
                break;
            case LB:
            case RB:
                pace = randStat(60, 90);
                shooting = randStat(25, 55);
                passing = randStat(50, 80);
                defending = randStat(55, 85);
                physical = randStat(55, 80);
                break;
            case CDM:
                pace = randStat(40, 70);
                shooting = randStat(30, 60);
                passing = randStat(55, 85);
                defending = randStat(60, 90);
                physical = randStat(60, 90);
                break;
            default:

                pace = randStat(45, 80);
                shooting = randStat(45, 80);
                passing = randStat(45, 80);
                defending = randStat(45, 80);
                physical = randStat(45, 80);
                break;
        }

        return new FootballAttributes(pos, pace, shooting, passing, defending, physical);
    }

    // min ile max arasında rastgele sayı
    private double randStat(int min, int max) {
        return min + rng.nextInt(max - min + 1);
    }
}
