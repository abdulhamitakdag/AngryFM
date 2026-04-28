package com.sportsmanager.sport.football;

import com.sportsmanager.core.model.*;

import java.util.List;

public class FootballMatch extends AbstractMatch {
    
    private static final int MAX_SUBS = 5;
    private final FootballMatchEngine engine;

    public FootballMatch(AbstractTeam homeTeam, AbstractTeam awayTeam) {
        super(homeTeam, awayTeam);
        this.engine = new FootballMatchEngine();
    }

    @Override
    public int getTotalPeriods() {
        return 2; // klasik 2 devre
    }

    @Override
    protected int getMaxSubstitutions() {
        return MAX_SUBS;
    }

    @Override
    protected PeriodResult simulatePeriodInternal(AbstractTeam home, AbstractTeam away) {
        return engine.simulatePeriod(home, away);
    }

    // engine sakatlıkları direkt oyunculara atıyor, biz sadece çağırıyoruz. listeyi kullanmıyoruz
    @Override
    protected void applyInjuries(AbstractTeam home, AbstractTeam away) {
        engine.determineInjuries(home, away);
    }

    // çıkan oyuncu kadroda mı, giren sakat mı falan diye bakıyoruz
    @Override
    protected void validateSubstitution(AbstractTeam team, AbstractPlayer playerOut, AbstractPlayer playerIn) {
        if (playerOut == null || playerIn == null) {
            throw new IllegalArgumentException("Players cannot be null");
        }

        List<AbstractPlayer> squad = team.getSquad();
        if (!squad.contains(playerOut)) {
            throw new IllegalStateException("Player not found in squad: " + playerOut.getName());
        }
        if (!squad.contains(playerIn)) {
            throw new IllegalStateException("Substitute not in squad: " + playerIn.getName());
        }
        if (playerIn.isInjured()) {
            throw new IllegalStateException("Cannot sub in injured player: " + playerIn.getName());
        }
    }

    // belki dışarıdan rating hesaplamak isteriz 
    public FootballMatchEngine getEngine() {
        return engine;
    }
}
