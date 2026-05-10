package com.sportsmanager.sport.basketball;

import com.sportsmanager.core.interfaces.ILeague;
import com.sportsmanager.core.interfaces.IMatchEngine;
import com.sportsmanager.core.interfaces.ISport;
import com.sportsmanager.core.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BasketballSport implements ISport {

    private final Random rng = new Random();

    @Override
    public String getSportId() {
        return "basketball";
    }

    @Override
    public int getPlayersOnField() {
        return 5;
    }

    @Override
    public int getWinPoints() {
        return 2;
    }

    @Override
    public boolean allowsDraw() {
        return false;
    }

    @Override
    public IMatchEngine createMatchEngine() {
        return new BasketballMatchEngine();
    }

    @Override
    public AbstractLeague createLeague(String leaguename) {
        return new BasketballLeague(leaguename);
    }

    @Override
    public List<String> getValidPositions() {
        List<String> positions = new ArrayList<>();
        for (BasketballPositions pos : BasketballPositions.values()) {
            positions.add(pos.name());
        }
        return positions;
    }

    @Override
    public AbstractPlayer createPlayer(String name, int age, Gender gender,
                                       int shirtNumber, String position,
                                       AbstractPlayerAttributes attributes) {
        BasketballPositions pos = BasketballPositions.valueOf(position.toUpperCase());
        return new BasketballPlayer(name, age, gender, shirtNumber, pos, attributes);
    }

    @Override
    public AbstractTeam createTeam(String name) {
        return new BasketballTeam(name);
    }

    @Override
    public AbstractCoach createCoach(String name, int age, Gender gender,
                                     CoachSpecialty specialty, int coachingLevel) {
        return new BasketballCoach(name, age, gender, specialty, coachingLevel);
    }

    @Override
    public AbstractTactic createDefaultTactic() {
        return BasketballTactic.createBalanced();
    }

    @Override
    public int getRecommendedSquadSize() {
        return 12;
    }

    @Override
    public int getRecommendedCoachCount() {
        return 2;
    }

    @Override
    public AbstractPlayerAttributes generateRandomAttributes(String position) {
        BasketballPositions pos;
        try {
            pos = BasketballPositions.valueOf(position.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown basketball position: " + position);
        }

        double shooting, playmaking, defending, rebounding, physical;

        switch (pos) {
            case PG:
                shooting   = rand(50, 85);
                playmaking = rand(65, 95);
                defending  = rand(40, 70);
                rebounding = rand(25, 50);
                physical   = rand(50, 80);
                break;
            case SG:
                shooting   = rand(65, 95);
                playmaking = rand(45, 75);
                defending  = rand(40, 70);
                rebounding = rand(25, 50);
                physical   = rand(50, 80);
                break;
            case SF:
                shooting   = rand(50, 80);
                playmaking = rand(40, 70);
                defending  = rand(45, 75);
                rebounding = rand(40, 70);
                physical   = rand(55, 85);
                break;
            case PF:
                shooting   = rand(35, 65);
                playmaking = rand(25, 55);
                defending  = rand(55, 85);
                rebounding = rand(60, 90);
                physical   = rand(60, 90);
                break;
            case C:
                shooting   = rand(25, 55);
                playmaking = rand(20, 45);
                defending  = rand(60, 90);
                rebounding = rand(65, 95);
                physical   = rand(65, 90);
                break;
            default:
                shooting   = rand(40, 70);
                playmaking = rand(40, 70);
                defending  = rand(40, 70);
                rebounding = rand(40, 70);
                physical   = rand(40, 70);
                break;
        }

        return new BasketballAttributes(pos, shooting, playmaking, defending, rebounding, physical);
    }

    private double rand(int min, int max) {
        return min + rng.nextInt(max - min + 1);
    }
}
