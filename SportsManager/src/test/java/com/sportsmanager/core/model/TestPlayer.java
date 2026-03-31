package com.sportsmanager.core.model;

import  com.sportsmanager.core.model.BaseTest;
import com.sportsmanager.core.model.TestObjFactory;
import com.sportsmanager.core.model.Injury;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestPlayer extends com.sportsmanager.core.model.BaseTest {
    @Test
    void testInjuredFemmePlayerNoTrain(){
        AbstractPlayer player= TestObjFactory.generateTestFemmePlayer();
        com.sportsmanager.core.model.Injury injury=
                new com.sportsmanager.core.model.Injury(3,3);
        player.setInjury(injury);
        /*there is no method of setting injury in the AbstractPlayer yet.*/
        player.train(1.0);
        assertTrue(player.getInjury() !=null, "Player is injured!");
        assertFalse(player.getInjury().isRecovered, "Player is not recovered from her injury yet!");
    //getter ve setterlar Injury için yazılmamış, yazılınca burası çalışmalı
        //veya nasıl bir getter/setter adı verildiyse ona göre değiştirilmeli
    }

    @Test
    void testInjuredMascPlayerNoTrain(){
        AbstractPlayer player= TestObjFactory.generateTestMascPlayer();
        com.sportsmanager.core.model.Injury injury=
                new com.sportsmanager.core.model.Injury(3,3);
        player.setInjury(injury);
        /*there is no method of setting injury in the AbstractPlayer yet.*/
        player.train(1.0);
        assertTrue(player.getInjury() !=null, "Player is injured!");
        assertFalse(player.getInjury().isRecovered, "Player is not recovered from his injury yet!");
        //getter ve setterlar Injury için yazılmamış, yazılınca burası çalışmalı
        //veya nasıl bir getter/setter adı verildiyse ona göre değiştirilmeli
    }
}