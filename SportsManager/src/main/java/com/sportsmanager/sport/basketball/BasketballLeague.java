package com.sportsmanager.sport.basketball;

import com.sportsmanager.core.model.AbstractLeague;
import com.sportsmanager.core.model.AbstractMatch;
import com.sportsmanager.core.model.AbstractTeam;

// TODO (Person B): createMatch'i BasketballMatch ile tamamla
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
        // TODO (Person B): return new BasketballMatch(home, away);
        throw new UnsupportedOperationException("BasketballMatch henüz implement edilmedi.");
    }
}
