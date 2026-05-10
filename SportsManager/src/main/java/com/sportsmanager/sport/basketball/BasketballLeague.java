package com.sportsmanager.sport.basketball;

import com.sportsmanager.core.model.AbstractLeague;
import com.sportsmanager.core.model.AbstractMatch;
import com.sportsmanager.core.model.AbstractTeam;

public class BasketballLeague extends AbstractLeague {

    public BasketballLeague(String name) {
        super(name);
    }

    @Override
    protected int getWinPoints() {
        return 2;
    }

    @Override
    protected int getDrawPoints() {
        // Basketbolda beraberlik yok (OT ile çözülür)
        return 0;
    }

    @Override
    protected AbstractMatch createMatch(AbstractTeam home, AbstractTeam away) {
        return new BasketballMatch(home, away);
    }
}
