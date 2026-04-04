package com.sportsmanager.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
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
}
