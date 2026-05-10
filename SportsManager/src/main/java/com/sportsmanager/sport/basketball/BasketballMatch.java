package com.sportsmanager.sport.basketball;

import com.sportsmanager.core.model.*;

import java.util.List;

public class BasketballMatch extends AbstractMatch {

    private static final int REGULAR_QUARTERS = 4;
    private final BasketballMatchEngine engine;
    // true olduğunda getTotalPeriods() 5 döndürür → AbstractMatch OT quarter'ını oynar
    private boolean overtimePlayed = false;

    public BasketballMatch(AbstractTeam homeTeam, AbstractTeam awayTeam) {
        super(homeTeam, awayTeam);
        this.engine = new BasketballMatchEngine();
    }

    @Override
    public int getTotalPeriods() {
        return overtimePlayed ? REGULAR_QUARTERS + 1 : REGULAR_QUARTERS;
    }

    // basketbolda beraberlik olmaz; OT varsa ekstra quarter açılır
    @Override
    protected PeriodResult simulatePeriodInternal(AbstractTeam home, AbstractTeam away) {
        PeriodResult result = engine.simulatePeriod(home, away);

        // 4. quarter bitti mi? → beraberlik kontrolü
        if (getCurrentPeriod() == REGULAR_QUARTERS && !overtimePlayed) {
            int totalHome = result.getHomeScore();
            int totalAway = result.getAwayScore();
            for (PeriodResult pr : getPeriodResults()) {
                totalHome += pr.getHomeScore();
                totalAway += pr.getAwayScore();
            }
            if (totalHome == totalAway) {
                // getTotalPeriods() artık 5 döndürecek → AbstractMatch BETWEEN_PERIODS yapacak
                overtimePlayed = true;
            }
        }

        // OT quarter'ı da berabere biterse ev sahibi 1 ekstra puan alır (çok nadir durum)
        if (getCurrentPeriod() == REGULAR_QUARTERS + 1) {
            int homeOt = result.getHomeScore();
            int awayOt = result.getAwayScore();
            if (homeOt == awayOt) {
                return new PeriodResult(homeOt + 1, awayOt);
            }
        }

        return result;
    }

    @Override
    protected void applyInjuries(AbstractTeam home, AbstractTeam away) {
        engine.determineInjuries(home, away);
    }

    @Override
    protected void validateSubstitution(AbstractTeam team, AbstractPlayer playerOut, AbstractPlayer playerIn) {
        if (playerOut == null || playerIn == null) {
            throw new IllegalArgumentException("Players cannot be null");
        }
        List<AbstractPlayer> squad = team.getSquad();
        if (!squad.contains(playerOut)) {
            throw new IllegalStateException("Player not in squad: " + playerOut.getName());
        }
        if (!squad.contains(playerIn)) {
            throw new IllegalStateException("Substitute not in squad: " + playerIn.getName());
        }
        if (playerIn.isInjured()) {
            throw new IllegalStateException("Cannot sub in injured player: " + playerIn.getName());
        }
    }

    // basketbolda değişiklik sınırı yok
    @Override
    protected int getMaxSubstitutions() {
        return Integer.MAX_VALUE;
    }

    public boolean isOvertimePlayed() {
        return overtimePlayed;
    }

    public BasketballMatchEngine getEngine() {
        return engine;
    }
}
