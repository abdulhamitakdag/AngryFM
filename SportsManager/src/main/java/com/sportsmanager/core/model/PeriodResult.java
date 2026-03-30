package com.sportsmanager.core.model;

import java.util.*;

public class PeriodResult {

    private final UUID id;
    private final int homeScore;
    private final int awayScore;

    public PeriodResult(int homeScore, int awayScore) {
        this.id = UUID.randomUUID();
        if (homeScore < 0) {
            this.homeScore = 0;
        } else {
            this.homeScore = homeScore;
        }
        if (awayScore < 0) {
            this.awayScore = 0;
        } else {
            this.awayScore = awayScore;
        }
    }

}
