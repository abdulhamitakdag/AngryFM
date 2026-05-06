package com.sportsmanager.sport.basketball;

import com.sportsmanager.core.model.AbstractPlayer;
import com.sportsmanager.core.model.AbstractTactic;

import java.util.List;

// TODO (Person B): formasyon logic'ini ve validateForSquad'ı tamamla
public class BasketballTactic extends AbstractTactic {

    public BasketballTactic(String name, double attackingWeight, double pressureIntensity) {
        super(name, attackingWeight, pressureIntensity);
    }

    // Static factory metodları — Person B gerekirse ekleyebilir
    public static BasketballTactic createStandard() {
        return new BasketballTactic("1-2-2", 0.5, 0.5);
    }

    public static BasketballTactic createZone() {
        return new BasketballTactic("2-1-2", 0.4, 0.6);
    }

    public static BasketballTactic createTrap() {
        return new BasketballTactic("1-3-1", 0.45, 0.75);
    }

    public static BasketballTactic createBig() {
        return new BasketballTactic("2-3", 0.35, 0.5);
    }

    @Override
    public String getFormationString() {
        return getName();
    }

    @Override
    public void validateForSquad(List<AbstractPlayer> squad) {
        // TODO (Person B): pozisyon gereksinimleri kontrolü
    }
}
