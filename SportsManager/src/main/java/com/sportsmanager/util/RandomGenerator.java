package com.sportsmanager.util;

import com.sportsmanager.core.interfaces.ISport;
import com.sportsmanager.core.model.*;
import com.sportsmanager.sport.football.FootballCoach;

import java.util.List;
import java.util.Random;

import static com.sportsmanager.util.ResourceLoader.*;

public class RandomGenerator{
    private static final Random random= new Random();

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

    public static AbstractCoach generateMaleCoach(){
        String name = generateRandomFullMaleName();
        int age = random.nextInt(36) + 15;
        CoachSpecialty[] specialties = CoachSpecialty.values();
        CoachSpecialty specialty = specialties[random.nextInt(specialties.length)];
        int level = random.nextInt(5) + 1;
        return new FootballCoach(name, age, Gender.MALE, specialty, level);
    }
}
