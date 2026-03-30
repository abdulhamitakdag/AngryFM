package com.sportsmanager.core.model;

import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTest {
    /*arkadaşlar bu class testing yaparken Maven'ın okuduğu kısım*/
    /*test metodlarını buradan okuyup otomatik çalıştırıyor */
    /*diğer bütün test classları bu classı extendlicek sooo bu class abstract olacak*/

    @BeforeEach void init(){}
    protected void assertWithinRange(double value, double min, double max, String name) {
        if (value < min || value > max) {
            throw new AssertionError(name + " was " + value + ", but expected between " + min + " and " + max);
        }
    }
}
