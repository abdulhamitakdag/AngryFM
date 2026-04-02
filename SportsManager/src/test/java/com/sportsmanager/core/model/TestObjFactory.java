package com.sportsmanager.core.model;
import java.util.Random;

import static com.sportsmanager.util.ResourceLoader.generateRandomFullFemmeName;
import static com.sportsmanager.util.ResourceLoader.generateRandomFullMascName;


public class TestObjFactory {

    /*arkadaşlar bu class testing yaparken kolaylık olsun diye çakırt diye obje yaratıyor çok kıyak */
    private static final Random random= new Random();
    //no positions and shirt numbers and stuff yet.
    public static TestPlayer.AbstractPlayerStub generateTestFemmePlayer(){
        String name=generateRandomFullFemmeName();
        int age = random.nextInt(36) + 15;
        return new TestPlayer.AbstractPlayerStub(name, age, Gender.FEMALE);
    }

    public static TestPlayer.AbstractPlayerStub generateTestMascPlayer(){
        String name=generateRandomFullMascName();
        int age = random.nextInt(36) + 15;
        return new TestPlayer.AbstractPlayerStub(name, age, Gender.MALE);

    }
}
