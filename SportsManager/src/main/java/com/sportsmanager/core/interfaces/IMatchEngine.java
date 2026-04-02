package com.sportsmanager.core.interfaces;

import com.sportsmanager.core.model.AbstractTeam;
import com.sportsmanager.core.model.PeriodResult;
import com.sportsmanager.core.model.Injury;
import com.sportsmanager.core.model.AbstractPlayer;
import java.util.List;

/**
 * Maçların simülasyon mantığını barındıran motor arayüzü.
 */
public interface IMatchEngine {
    PeriodResult simulatePeriod(AbstractTeam home, AbstractTeam away);
    List<Injury> determineInjuries(AbstractTeam home, AbstractTeam away);
    double calculatePlayerRating(AbstractPlayer player);
}
