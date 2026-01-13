package es.us.dp1.l6_3_24_25.Petris.player.model;

import org.junit.jupiter.api.Test;
import io.qameta.allure.Owner;

import static org.junit.jupiter.api.Assertions.*;

@Owner("DiegoVicenteCamara(RXW1249)")
class StatisticsTest {

    @Test
    void getScore_returnsNullWhenLessThanTenGames() {
        Statistics stats = Statistics.builder()
            .gamesPlayed(9)
            .gamesWon(5)
            .timePlayed(0)
            .sarcinasCreated(0)
            .bacteriasCreated(0)
            .build();

        assertNull(stats.getScore());
    }

    @Test
    void getScore_computesWhenEnoughGames() {
        Statistics stats = Statistics.builder()
            .gamesPlayed(20)
            .gamesWon(10)
            .timePlayed(0)
            .sarcinasCreated(0)
            .bacteriasCreated(0)
            .build();

        double expected = ((double) 10 / 20) * 100.0 + 20.0 * Math.log10(20.0);
        assertEquals(expected, stats.getScore(), 1e-6);
    }

    @Test
    void getStatisticByName_returnsValues() {
        Statistics stats = Statistics.builder()
            .gamesPlayed(11)
            .gamesWon(6)
            .timePlayed(123)
            .sarcinasCreated(4)
            .bacteriasCreated(9)
            .build();

        assertEquals(11, stats.getStatisticByName("games_played"));
        assertEquals(6, stats.getStatisticByName("games_won"));
        assertEquals(123, stats.getStatisticByName("time_played"));
        assertEquals(4, stats.getStatisticByName("sarcinas_created"));
        assertEquals(9, stats.getStatisticByName("bacterias_created"));
    }

    @Test
    void getStatisticByName_unknownReturnsNull() {
        Statistics stats = Statistics.builder()
            .gamesPlayed(1)
            .gamesWon(1)
            .timePlayed(0)
            .sarcinasCreated(0)
            .bacteriasCreated(0)
            .build();

        assertNull(stats.getStatisticByName("unknown"));
    }
}
