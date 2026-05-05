package com.sportsmanager.core.gamesession;

import com.sportsmanager.core.interfaces.ISport;
import com.sportsmanager.core.model.AbstractLeague;
import com.sportsmanager.core.model.AbstractTeam;
import com.sportsmanager.core.model.Gender;
import com.sportsmanager.factory.SportFactory;
import com.sportsmanager.persistence.FootballSaveManager;
import com.sportsmanager.persistence.GameState;
import com.sportsmanager.sport.football.FootballLeague;
import com.sportsmanager.util.RandomGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameController {
    private final ISport sport;
    private final Gender gender;
    private final AbstractLeague league;
    private final List<AbstractTeam> teams;
    private AbstractTeam userTeam;

    private GameController(ISport sport, Gender gender,
                        AbstractLeague league, List<AbstractTeam> teams) {
        this.sport = sport;
        this.gender = gender;
        this.league = league;
        this.teams = teams;
    }

    public static GameController startNew(String sportId, Gender gender, int teamCount) {
        ISport sport = SportFactory.createSport(sportId);
        List<AbstractTeam> teams = new ArrayList<>();
        for (int i = 0; i < teamCount; i++) {
            AbstractTeam t = RandomGenerator.generateTeam(sport, gender);
            t.setCurrentTactic(sport.createDefaultTactic());
            teams.add(t);
        }
        String leagueName = (gender == Gender.MALE)
                ? "AngryFM Men's Super League"
                : "AngryFM Women's Super League";
        AbstractLeague league = sport.createLeague(leagueName);
        league.generateFixtures(teams);
        return new GameController(sport, gender, league, teams);
    }

    public void setUserTeam(AbstractTeam team) { this.userTeam = team; }
    public AbstractTeam getUserTeam() { return userTeam; }
    public ISport getSport() { return sport; }
    public AbstractLeague getLeague() { return league; }
    public List<AbstractTeam> getTeams() { return teams; }
    public Gender getGender() { return gender; }
}



