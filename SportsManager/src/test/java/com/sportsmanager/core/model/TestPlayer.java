package com.sportsmanager.core.model;

import com.sportsmanager.BaseTest;
import com.sportsmanager.TestingZone;
import com.sportsmanager.core.Injury;
import com.sportsmanager.core.model.Injury;
import org.junit.jupiter.api.Test;

public class TestPlayer extends com.sportsmanager.core.model.BaseTest {
    @Test
    void testInjuredFemmePlayerNoTrain(){
        AbstractPlayer player= TestObjFactory.generateTestFemmePlayer();
        com.sportsmanager.core.model.Injury injury=
                new com.sportsmanager.core.model.Injury(3,3);

    }

    @Test
    void testInjuredMascPlayerNoTrain(){
        AbstractPlayer player= TestObjFactory.generateTestMascPlayer();
    }



}