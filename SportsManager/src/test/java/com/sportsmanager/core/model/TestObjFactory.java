package com.sportsmanager.core.model;
import com.sportsmanager.util.ResourceLoader;
import java.util.List;
import java.util.Random;


public class TestObjFactory {

    /*arkadaşlar bu class testing yaparken kolaylık olsun diye çakırt diye obje yaratıyor çok kıyak */

    private static final List<String> teamNames= ResourceLoader.loadLinesFromTxt("teamnames.txt");
    private static final Random random= new Random();

    public static String generateRandomTeamName(){
        return teamNames.get(random.nextInt(teamNames.size()));
    }

    private static final List<String> firstMascNames= ResourceLoader.loadLinesFromTxt("mascnames.txt");
    private static final List<String> firstFemmeNames= ResourceLoader.loadLinesFromTxt("femmenames.txt");
    private static final List<String> surnames= ResourceLoader.loadLinesFromTxt("surnames.txt");
    private static final Random random= new Random();

    public static String generateRandomFullMascName(){
        String firstMascName= firstMascNames.get(random.nextInt(firstMascNames.size()));
        String surname= surnames.get(random.nextInt(surnames.size()));
        return firstMascName + "" + surname;
    }

    public static String generateRandomFullFemmeName(){
        String firstFemmeName= firstFemmeNames.get(random.nextInt(firstFemmeNames.size()));
        String surname= surnames.get(random.nextInt(surnames.size()));
        return firstFemmeName + "" + surname;
    }

    public static AbstractPlayer generateTestFemmePlayer(){
        String a=generateRandomFullFemmeName();
        return new TestPlayer(a);
    }

    public static AbstractPlayer generateTestMascPlayer(){
        String a=generateRandomFullMascName();
        return new TestPlayer(a);
    }

    /*here, i had to separate teams by gender, we can change that later on.*/
    /*similarly, the Player class is a placeholder.*/
    /*attributes should be pulled from Player class for stats*/


    /*
    public static Player generateTestFemmePlayer() {
        String name = generateRandomFullFemmeName();
        String team = generateRandomTeamName();
        int age = 15 + random.nextInt(35);
        return new Player(name, age, team, stats);
    }
    public static Team generateTestFemmeTeam(int playerCount) {
        String teamName = generateRandomTeamName();
        Team team = new Team(teamName);

        for (int i = 0; i < playerCount; i++) {
            team.addPlayer(generateTestFemmePlayer()); // Add the dummy players you just made
        }

        return team;
    }
    public static Player generateTestMascPlayer() {
        String name = generateRandomFullMascName();
        String team = generateRandomTeamName();
        int age = 15 + random.nextInt(35);
        return new Player(name, age, team, stats);
    }
    public static Team generateTestMascTeam(int playerCount) {
        String teamName = generateRandomTeamName();
        Team team = new Team(teamName);

        for (int i = 0; i < playerCount; i++) {
            team.addPlayer(generateTestMascPlayer()); // Add the dummy players you just made
        }

        return team; */
}
