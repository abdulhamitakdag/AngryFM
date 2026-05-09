package com.sportsmanager.core.model;

import com.sportsmanager.sport.basketball.BasketballAttributes;
import com.sportsmanager.sport.basketball.BasketballPlayer;
import com.sportsmanager.sport.basketball.BasketballPositions;
import com.sportsmanager.sport.basketball.BasketballTeam;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestBasketballTeam extends BaseTest {

    private BasketballPlayer player(BasketballPositions position, int shirt) {
        return new BasketballPlayer("P" + shirt, 22, Gender.MALE, shirt, position,
                new BasketballAttributes(position, 70, 70, 70, 70, 70));
    }

    private List<AbstractPlayer> validLineup() {
        return List.of(
                player(BasketballPositions.PG, 1),
                player(BasketballPositions.SG, 2),
                player(BasketballPositions.SF, 3),
                player(BasketballPositions.PF, 4),
                player(BasketballPositions.C, 5)
        );
    }

    @Test
    void maxSquadSizeIsTwelve() {
        BasketballTeam team = new BasketballTeam("Basket Team");

        assertEquals(12, team.getMaxSquadSize());
    }

    @Test
    void validLineupDoesNotThrow() {
        BasketballTeam team = new BasketballTeam("Basket Team");

        assertDoesNotThrow(() -> team.validateLineup(validLineup()));
    }

    @Test
    void lineupMustContainExactlyFivePlayers() {
        BasketballTeam team = new BasketballTeam("Basket Team");

        assertThrows(IllegalArgumentException.class, () -> team.validateLineup(null));
        assertThrows(IllegalArgumentException.class, () -> team.validateLineup(validLineup().subList(0, 4)));
    }

    @Test
    void lineupMustContainPointGuardAndCenter() {
        BasketballTeam team = new BasketballTeam("Basket Team");

        List<AbstractPlayer> noPointGuard = List.of(
                player(BasketballPositions.SG, 1), player(BasketballPositions.SG, 2),
                player(BasketballPositions.SF, 3), player(BasketballPositions.PF, 4),
                player(BasketballPositions.C, 5));

        List<AbstractPlayer> noCenter = List.of(
                player(BasketballPositions.PG, 1), player(BasketballPositions.SG, 2),
                player(BasketballPositions.SF, 3), player(BasketballPositions.PF, 4),
                player(BasketballPositions.PF, 5));

        assertThrows(IllegalArgumentException.class, () -> team.validateLineup(noPointGuard));
        assertThrows(IllegalArgumentException.class, () -> team.validateLineup(noCenter));
    }
}
