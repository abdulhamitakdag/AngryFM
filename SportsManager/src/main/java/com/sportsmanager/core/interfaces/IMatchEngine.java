package com.sportsmanager.core.interfaces;

import com.sportsmanager.core.model.AbstractPlayer;
import com.sportsmanager.core.model.AbstractTeam;
import com.sportsmanager.core.model.Injury;
import com.sportsmanager.core.model.PeriodResult;

import java.util.List;

// maç simülasyonu için motor arayüzü
public interface IMatchEngine {
    PeriodResult simulatePeriod(AbstractTeam home, AbstractTeam away);
    List<Injury> determineInjuries(AbstractTeam home, AbstractTeam away);
    double calculatePlayerRating(AbstractPlayer player);
}
