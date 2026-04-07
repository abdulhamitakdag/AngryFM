package com.sportsmanager.util;

import com.sportsmanager.core.interfaces.ISport;
import com.sportsmanager.core.model.*;
import com.sportsmanager.factory.SportFactory;
import com.sportsmanager.sport.football.FootballCoach;

import java.util.List;
import java.util.Random;

import static com.sportsmanager.util.ResourceLoader.*;

public class RandomGenerator{
    private static final Random random= new Random();
    public static final ISport DEFAULT_SPORT = SportFactory.createSport("football");

    private static final List<String> firstMaleNames = loadLinesFromTxt("malenames.txt");
    private static final List<String> firstFemaleNames = loadLinesFromTxt("femalenames.txt");
    private static final List<String> surnames= loadLinesFromTxt("surnames.txt");

    private static final List<String> teamNames= loadLinesFromTxt("teamnames.txt");
    public static String generateRandomTeamName(){
        return teamNames.get(random.nextInt(teamNames.size()));
    }

    public static String generateRandomFullMaleName(){
        String firstMascName= firstMaleNames.get(random.nextInt(firstMaleNames.size()));
        String surname= surnames.get(random.nextInt(surnames.size()));
        return firstMascName + " " + surname;
    }

    public static String generateRandomFullFemaleName(){
        String firstFemmeName= firstFemaleNames.get(random.nextInt(firstFemaleNames.size()));
        String surname= surnames.get(random.nextInt(surnames.size()));
        return firstFemmeName + " " + surname;
    }

    public static AbstractPlayer generateFemalePlayer(ISport sport){
        String name = generateRandomFullFemaleName();
        int age = random.nextInt(36) + 15;
        List<String> positions = sport.getValidPositions();
        String position = positions.get(random.nextInt(positions.size()));
        int shirtNumber = random.nextInt(99) + 1;
        AbstractPlayerAttributes attributes = sport.generateRandomAttributes(position);
        return sport.createPlayer(name, age, Gender.FEMALE, shirtNumber, position, attributes);
    }

    public static AbstractPlayer generateMalePlayer(ISport sport){
        String name = generateRandomFullMaleName();
        int age = random.nextInt(36) + 15;
        List<String> positions = sport.getValidPositions();
        String position = positions.get(random.nextInt(positions.size()));
        int shirtNumber = random.nextInt(99) + 1;
        AbstractPlayerAttributes attributes = sport.generateRandomAttributes(position);
        return sport.createPlayer(name, age, Gender.MALE, shirtNumber, position, attributes);
    }

    public static AbstractCoach generateFemaleCoach(){
        String name = generateRandomFullFemaleName();
        int age = random.nextInt(36) + 15;
        CoachSpecialty[] specialties = CoachSpecialty.values();
        CoachSpecialty specialty = specialties[random.nextInt(specialties.length)];
        int level = random.nextInt(5) + 1;
        return new FootballCoach(name, age, Gender.FEMALE, specialty, level);
    }

    public static FootballCoach generateMaleCoach(){
        String name = generateRandomFullMaleName();
        int age = random.nextInt(36) + 15;
        CoachSpecialty[] specialties = CoachSpecialty.values();
        CoachSpecialty specialty = specialties[random.nextInt(specialties.length)];
        int level = random.nextInt(5) + 1;
        return new FootballCoach(name, age, Gender.MALE, specialty, level);
    }

    public static AbstractTeam generateTeam(ISport sport) {
        String teamName = generateRandomTeamName();
        AbstractTeam team = sport.createTeam(teamName);

        int squadSize = sport.getRecommendedSquadSize();
        int coachCount = sport.getRecommendedCoachCount();

        for (int i = 0; i < squadSize; i++) {
            AbstractPlayer player;
            if (random.nextBoolean()) {
                player = generateMalePlayer(sport);
            } else {
                player = generateFemalePlayer(sport);
            }
            team.addPlayer(player);
        }

        for (int i = 0; i < coachCount; i++) {
            AbstractCoach coach;
            if (random.nextBoolean()) {
                coach = generateMaleCoach();
            } else {
                coach = generateFemaleCoach();
            }
            team.addCoach(coach);
        }

        return team;
    }
}
