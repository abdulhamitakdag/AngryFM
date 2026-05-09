package com.sportsmanager.core.model;

import com.sportsmanager.core.model.BaseTest;
import com.sportsmanager.core.model.CoachSpecialty;
import com.sportsmanager.core.model.Gender;
import com.sportsmanager.sport.basketball.BasketballCoach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestBasketballCoach extends BaseTest {
    @Test
    void specialtyMultiplierUsesCoachLevel(){
        BasketballCoach Level1=new BasketballCoach("Hüsamettin", 40, Gender.MALE, CoachSpecialty.GENERAL, 1);
        BasketballCoach Level5=new BasketballCoach("Necmettin", 40, Gender.MALE, CoachSpecialty.GENERAL, 5);
        assertEquals(1.0, Level1.specialtyMultiplier(), 0.001);
        assertEquals(1.5, Level5.specialtyMultiplier(), 0.001);
    }

    @Test
    void gettersMatchConstructorOutput(){
        BasketballCoach coach = new BasketballCoach("Rüya", 35, Gender.FEMALE, CoachSpecialty.DEFENDING, 3);
        assertEquals("Rüya", coach.getName());
        assertEquals(35, coach.getAge());
        assertEquals(Gender.FEMALE, coach.getGender());
        assertEquals(CoachSpecialty.DEFENDING, coach.getSpecialty());
        assertEquals(3, coach.getCoachingLevel());
    }

}
