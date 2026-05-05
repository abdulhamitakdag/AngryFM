package com.sportsmanager.sport.basketball;

import com.sportsmanager.core.model.AbstractPlayer;
import com.sportsmanager.core.model.AbstractPlayerAttributes;
import com.sportsmanager.core.model.Gender;

public class BasketballPlayer extends AbstractPlayer {

    private final BasketballPositions position;

    public BasketballPlayer(String name, int age, Gender gender, int shirtNumber, BasketballPositions position, AbstractPlayerAttributes attributes) {
        super(name, age, gender, shirtNumber, attributes);
        this.position = position;
    }

    public BasketballPositions getBasketballPosition() {
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
        } else if (currentAge > 33) {
            return 0.7;
        }
        else {
            return 1;
        }
    }
}