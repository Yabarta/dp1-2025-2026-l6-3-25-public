package es.us.dp1.l6_3_24_25.Petris.player.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

import static org.junit.jupiter.api.Assertions.*;


@Epic("Statistics model")
@Feature("Statistics computed fields")
class StatisticsTest {

    @Test
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Story("Score")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("getScore returns null when gamesPlayed < 10")
    @Description("Verifies that getScore returns null when the player has fewer than 10 games played")
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
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Story("Score")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("getScore computes expected value when gamesPlayed >= 10")
    @Description("Verifies that getScore computes the expected formula when the player has enough games played")
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
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Story("Dynamic statistic access")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("getStatisticByName returns expected values")
    @Description("Verifies that getStatisticByName returns the correct value for supported statistic keys")
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
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Story("Dynamic statistic access")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("getStatisticByName returns null for unknown keys")
    @Description("Verifies that getStatisticByName returns null when an unsupported statistic key is provided")
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
