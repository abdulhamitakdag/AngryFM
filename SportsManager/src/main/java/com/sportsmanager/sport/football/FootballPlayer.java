package com.sportsmanager.sport.football;

import com.sportsmanager.core.model.AbstractPlayer;
import com.sportsmanager.core.model.Gender;

public class FootballPlayer extends AbstractPlayer {

    private final FootballPositions position;


    public FootballPlayer(String name, int age, Gender gender, int shirtNumber, FootballPositions position, FootballAttributes attributes) {
        super(name, age, gender, position.name(), shirtNumber, attributes);
        this.position = position;
    }

    public FootballPositions getFootballPosition() {
        return this.position;
    }

    @Override
    public String getPosition() {
        return this.position.name();
    }

    @Override
    public double getTrainingEffectiveness() {

        int currentAge = this.getAge();

        if (this.isInjured()) {
            return 0.0;
        }

        if (currentAge < 25 && currentAge > 15) {
            return 1.3;
        } else if (currentAge > 31) {
            return 0.7;
        }
        else {
            return 1;
        }
    }
}