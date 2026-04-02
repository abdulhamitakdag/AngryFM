package com.sportsmanager.core.interfaces;

import com.sportsmanager.core.model.TeamInLeagueTable;
import com.sportsmanager.core.model.MatchResult;
import com.sportsmanager.core.model.AbstractTeam;

import java.util.List;

// lig işlemleri için temel arayüz
public interface ILeague {
    List<TeamInLeagueTable> getStandings();
    void recordResult(MatchResult result);
    void generateFixtures(List<AbstractTeam> teams);
    boolean isSeasonOver();
}
