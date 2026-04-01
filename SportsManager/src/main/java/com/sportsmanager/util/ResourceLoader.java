package com.sportsmanager.util;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
public class ResourceLoader {
    /*bu class bizim .txt resourcelarımızı okuyup java objesi yapacak*/
    /*aka.: resource loadlayacak, hence the name brolar*/
    /*gotta make the methods static m8, file loadlamakla uğraşmayak*/



    public static List<String> loadLinesFromTxt(String filename){
        InputStream is = ResourceLoader.class.getClassLoader().getResourceAsStream(filename);
        if (is==null){
            throw new RuntimeException("Error: resource file named" + filename+ " not found.");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))){
            return reader.lines().filter(line-> !line.trim().isEmpty()).collect(Collectors.toList());
        } catch (Exception e){throw new RuntimeException("Error: failed to read resource file named" + filename);}
    }

    private static final List<String> teamNames= loadLinesFromTxt("teamnames.txt");
    private static final Random random= new Random();

    public static String generateRandomTeamName(){
        return teamNames.get(random.nextInt(teamNames.size()));
    }

    private static final List<String> firstMascNames= loadLinesFromTxt("mascnames.txt");
    private static final List<String> firstFemmeNames= loadLinesFromTxt("femmenames.txt");
    private static final List<String> surnames= loadLinesFromTxt("surnames.txt");

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
}
