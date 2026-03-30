package com.sportsmanager.core.model;

import java.util.*;

public class Fixture {

    private final UUID id;
    private final int week;
    private final AbstractTeam homeTeam;
    private final AbstractTeam awayTeam;
    private MatchResult result;


    public Fixture(int week, AbstractTeam homeTeam, AbstractTeam awayTeam) {
        this.id = UUID.randomUUID();
        this.week = week;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    public void setResult(MatchResult newResult) {
        this.result = newResult;
    }

    public boolean isPlayed() {
        return this.result != null;
    }

    public AbstractTeam getHomeTeam() {
        return homeTeam;
    }
    public AbstractTeam getAwayTeam() {
        return awayTeam;
    }
    public MatchResult getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fixture fixture = (Fixture) o;
        return Objects.equals(id, fixture.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    //AbstractTeam abstract class ı oluşturulmadığı için hata veriyor şimdilik.
    // Class eklendiğinde otomatik fixlenecek
}
