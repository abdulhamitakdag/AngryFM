package com.sportsmanager.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractMatch {

    public enum MatchState {
        NOT_STARTED, IN_PROGRESS, BETWEEN_PERIODS, FINISHED
    }

    private final AbstractTeam homeTeam;
    private final AbstractTeam awayTeam;
    private MatchState state;
    private int currentPeriod;
    private final List<PeriodResult> periodResults;

    private int homeSubCount;
    private int awaySubCount;

    public AbstractMatch(AbstractTeam homeTeam, AbstractTeam awayTeam) {
        if (homeTeam == null || awayTeam == null) {
            throw new IllegalArgumentException("Teams cannot be null");
        }
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.state = MatchState.NOT_STARTED;
        this.currentPeriod = 0;
        this.periodResults = new ArrayList<>();
        this.homeSubCount = 0;
        this.awaySubCount = 0;
    }

    public abstract int getTotalPeriods();

    protected abstract void validateSubstitution(AbstractTeam team, AbstractPlayer out, AbstractPlayer in);

    // alt sınıf kendi engine'ini kullanarak simüle edecek
    protected abstract PeriodResult simulatePeriodInternal(AbstractTeam home, AbstractTeam away);

    // maç sonu sakatlık hesabı
    protected abstract void applyInjuries(AbstractTeam home, AbstractTeam away);

    protected abstract int getMaxSubstitutions();

    /*maçı başlat*/
    public void start() {
        if (state != MatchState.NOT_STARTED) {
            throw new IllegalStateException("Match already started or finished");
        }
        currentPeriod = 1;
        state = MatchState.IN_PROGRESS;
    }

    public PeriodResult simulateCurrentPeriod() {
        if (state != MatchState.IN_PROGRESS) {
            throw new IllegalStateException("Match is not IN_PROGRESS");
        }

        PeriodResult result = simulatePeriodInternal(homeTeam, awayTeam);
        periodResults.add(result);

        if (currentPeriod >= getTotalPeriods()) {
            state = MatchState.FINISHED;
            finalizeMatch();
        } else {
            state = MatchState.BETWEEN_PERIODS;
        }
        return result;
    }

    public void resumeAfterBreak() {
        if (state != MatchState.BETWEEN_PERIODS) {
            throw new IllegalStateException("Not in BETWEEN_PERIODS state");
        }
        currentPeriod++;
        state = MatchState.IN_PROGRESS;
    }
    
    public void substituteHome(AbstractPlayer playerOut, AbstractPlayer playerIn) {
        checkSubState();
        if (homeSubCount >= getMaxSubstitutions()) {
            throw new IllegalStateException("Home team has no substitutions left");
        }
        validateSubstitution(homeTeam, playerOut, playerIn);
        homeSubCount++;
    }

    // deplasman değişikliği
    public void substituteAway(AbstractPlayer playerOut, AbstractPlayer playerIn) {
        checkSubState();
        if (awaySubCount >= getMaxSubstitutions()) {
            throw new IllegalStateException("Away team has no substitutions left");
        }
        validateSubstitution(awayTeam, playerOut, playerIn);
        awaySubCount++;
    }

    private void checkSubState() {
        if (state == MatchState.NOT_STARTED || state == MatchState.FINISHED) {
            throw new IllegalStateException("Cannot substitute before match starts or after it ends");
        }
    }

    // maç bitti, sakatlık falan hesaplansın
    protected void finalizeMatch() {
        applyInjuries(homeTeam, awayTeam);
    }

    // tüm devrelerin skorunu toplayıp MatchResult döndürüyoruz
    public MatchResult getMatchResult() {
        if (state != MatchState.FINISHED) {
            throw new IllegalStateException("Match is not finished yet");
        }
        int totalHome = 0;
        int totalAway = 0;
        for (PeriodResult pr : periodResults) {
            totalHome += pr.getHomeScore();
            totalAway += pr.getAwayScore();
        }
        return new MatchResult(totalHome, totalAway);
    }

    public AbstractTeam getHomeTeam()            { return homeTeam; }
    public AbstractTeam getAwayTeam()            { return awayTeam; }
    public MatchState getState()                 { return state; }
    public int getCurrentPeriod()                { return currentPeriod; }
    public List<PeriodResult> getPeriodResults() { return Collections.unmodifiableList(periodResults); }
    public int getHomeSubCount()                 { return homeSubCount; }
    public int getAwaySubCount()                 { return awaySubCount; }
}
