package com.sportsmanager.core.model;

import java.util.*;

public class PeriodResult {

    private final UUID id;
    private final int homeScore;
    private final int awayScore;
    private final List<String> events;

    public PeriodResult(int homeScore, int awayScore) {
        this(homeScore, awayScore, new ArrayList<>());
    }

    public PeriodResult(int homeScore, int awayScore, List<String> events) {
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
        this.events = events != null ? Collections.unmodifiableList(new ArrayList<>(events)) : Collections.emptyList();
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public List<String> getEvents() {
        return events;
    }
}
