package com.sportsmanager.util;

import com.sportsmanager.core.interfaces.ISport;
import com.sportsmanager.core.model.AbstractPerson;
import com.sportsmanager.core.model.AbstractPlayer;
import com.sportsmanager.core.model.AbstractPlayerAttributes;
import com.sportsmanager.core.model.Gender;

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
        return firstMascName + "" + surname;
    }

    public static String generateRandomFullFemaleName(){
        String firstFemmeName= firstFemaleNames.get(random.nextInt(firstFemaleNames.size()));
        String surname= surnames.get(random.nextInt(surnames.size()));
        return firstFemmeName + "" + surname;
    }

    public static AbstractPlayer generateFemalePlayer(){
        String name=generateRandomFullFemaleName();
        int age= random.nextInt(36)+15;

        /*ID yok hiçbir parametrede
        String ID= sport.getSportId();*/
        //shirtNumber setter-getter lazım veya nasıl sınırları olacak belirlenmeli
        int shirtNumber=getShirtNumber;
        //position setter-getter lazım
        String position= getValidPositions();
        //getValidPositions değişebilir
        //attribute setter-getter lazım
        AbstractPlayerAttributes attributes= sports.generateRandomAttributes(position);
        //generateRandomAttributes değişebilir
        return new AbstractPlayer(name, age, Gender.FEMALE, position, shirtNumber, attributes) {
            @Override
            public double getTrainingEffectiveness() {
                return 0;
            }
        };
    }

    //String name, int age, Gender gender, String position, int shirtNumber, AbstractPlayerAttributes attributes
    public static AbstractPlayer generateMalePlayer(){
        String name=generateRandomFullMaleName();
        int age= random.nextInt(36)+15;

        /*ID yok hiçbir parametrede
        String ID= sport.getSportId();*/
        //shirtNumber setter-getter lazım veya nasıl sınırları olacak belirlenmeli
        int shirtNumber=getShirtNumber;
        //position setter-getter lazım
        String position= getValidPositions();
        //getValidPositions değişebilir
        //attribute setter-getter lazım
        AbstractPlayerAttributes attributes= sports.generateRandomAttributes(position);
        //generateRandomAttributes değişebilir
        return new AbstractPlayer(name, age, Gender.MALE, position, shirtNumber, attributes) {
            @Override
            public double getTrainingEffectiveness() {
                return 0;
            }
        };
    }

    public static AbstractPerson generateFemaleCoach(){
        String name= generateRandomFullFemaleName();
        int age = random.nextInt(36) + 15;
        return new AbstractPerson(name, age, Gender.FEMALE) {
            @Override
            public void train(double intensity) {

            }

            @Override
            public double getTrainingEffectiveness() {
                return 0;
            }
        };
    }

    public static AbstractPerson generateMaleCoach(){
    String name= generateRandomFullMaleName();
    int age = random.nextInt(36) + 15;
    return new AbstractPerson(name, age, Gender.MALE) {
        @Override
        public void train(double intensity) {

        }

        @Override
        public double getTrainingEffectiveness() {
            return 0;
        }
    };
    }
}