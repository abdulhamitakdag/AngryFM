package com.sportsmanager.sport.football;

import com.sportsmanager.core.model.CoachSpecialty;
import com.sportsmanager.core.model.AbstractCoach;
import com.sportsmanager.core.model.Gender;


public class FootballCoach extends AbstractCoach {
    public FootballCoach(String name, int age, Gender gender, CoachSpecialty specialty, int coachingLevel) {
        super(name, age, gender, specialty, coachingLevel);
    }

    @Override
    public double specialtyMultiplier() {
        return 1.0 + ((this.getCoachingLevel() - 1) * 0.125);
        // for level 1: 1
        // for level 2: 1.125
        // for level 3: 1.250
        // for level 4: 1.375
        // for level 5: 1.5
    }
}