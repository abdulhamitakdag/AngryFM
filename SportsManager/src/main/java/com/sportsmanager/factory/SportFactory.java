package com.sportsmanager.factory;

import com.sportsmanager.core.interfaces.ISport;
import com.sportsmanager.sport.football.FootballSport;

public class SportFactory {

    // şimdilik sadece futbol var, yeni spor eklenince buraya case eklenir
    public static ISport createSport(String sportId) {
        if (sportId == null) {
            throw new IllegalArgumentException("sportId cannot be null");
        }

        switch (sportId.toLowerCase()) {
            case "football":
                return new FootballSport();
            default:
                throw new IllegalArgumentException("Unknown sport: " + sportId);
        }
    }
}
