package es.us.dp1.l6_3_24_25.Petris.player.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import es.us.dp1.l6_3_24_25.Petris.player.model.GlobalStatistic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.player.model.Statistics;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

@SpringBootTest
@Epic("Statistics Service")
class StatisticsServiceTest {

    @Autowired
    private StatisticsService statisticsService;

    @Test
    @Feature("Statistics getters")
    @DisplayName("Get all Statistics")
    @Description("This method received all the statistics from players")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco(FBN588)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/136")
    void getAllStatistics() {
        List<Statistics> statisticsList = statisticsService.getAllStatistics();
        assertEquals(11, statisticsList.size(), "Incorrect number of statistics");
    }

    @Test
    @Feature("Statistics getters")
    @DisplayName("Get statistic by id")
    @Description("This method received an statistic by a correct id")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco(FBN588)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/136")
    void getAchievementById_ExistingId() {
        Integer id = 1;
        Statistics statistics = statisticsService.getStatisticsById(id);
        assertEquals(id, statistics.getId(), "Ids don't match");
    }

    @Test
    @Feature("Statistics getters")
    @DisplayName("Get statistic by id")
    @Description("This method received an statistic by a correct id")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco(FBN588)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/136")
    void getAchievementById_WrongId() {
        Integer id = 12;
        assertThrows(ResourceNotFoundException.class, () -> statisticsService.getStatisticsById(id));
    }

    @Test
    @Transactional
    @Feature("Statistics persistence")
    @DisplayName("Save statistics")
    @Description("This method saves a statistics object in the database")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("dlozaco(FBN588)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/136")
    void saveStatistics() {
        Statistics statistics = new Statistics();
        Integer gamesPlated = 5;
        Integer gamesWon = 3;
        Integer timePlayed = 120;
        Integer sarcinasCreated = 1;
        Integer bacteriasCreated = 30;
        statistics.setGamesPlayed(gamesPlated);
        statistics.setGamesWon(gamesWon);
        statistics.setTimePlayed(timePlayed);
        statistics.setSarcinasCreated(sarcinasCreated);
        statistics.setBacteriasCreated(bacteriasCreated);

        Statistics savedStatistics = statisticsService.saveStatistics(statistics);
        assertEquals(gamesPlated, savedStatistics.getGamesPlayed(), "Games played don't match");
        assertEquals(gamesWon, savedStatistics.getGamesWon(), "Games won don't match");
        assertEquals(timePlayed, savedStatistics.getTimePlayed(), "Time played don't match");
        assertEquals(sarcinasCreated, savedStatistics.getSarcinasCreated(), "Sarcinas created don't match");
        assertEquals(bacteriasCreated, savedStatistics.getBacteriasCreated(), "Bacterias created don't match");
    }

    @ParameterizedTest
    @Feature("Statistics getters")
    @DisplayName("Get box plot stats for field")
    @Description("This method received the box plot statistics for a given field")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco(FBN588)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/157")
    @MethodSource("provideFieldsForBoxPlotStats")
    void testGetBoxPlotStatsForGoodField(String fieldName, List<Double> expectedStats) {
        List<Double> boxPlotStats = statisticsService.getBoxPlotStatsForField(fieldName);

        assertEquals(5, boxPlotStats.size(), "Incorrect number of box plot statistics returned");
        assertEquals(expectedStats, boxPlotStats, "Box plot statistics don't match expected values");
    }

    static List<Arguments> provideFieldsForBoxPlotStats() {
        return List.of(
            Arguments.of("gamesPlayed", List.of(2.0, 8.0, 12.0, 19.0, 24.0)),
            Arguments.of("gamesWon", List.of(0.0, 3.5, 6.0, 8.5, 16.0)),
            Arguments.of("timePlayed", List.of(147.0, 4200.0, 7200.0, 11400.0, 14400.0)),
            Arguments.of("sarcinasCreated", List.of(1.0, 3.5, 8.0, 13.0, 20.0)),
            Arguments.of("bacteriasCreated", List.of(14.0, 61.0, 110.0, 178.0, 230.0))
        );
    }


    @Test
    @Feature("Statistics getters")
    @DisplayName("Get global statistics")
    @Description("This method received the global statistics of the game")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco(FBN588)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/157")
    void testGetGlobalStatistics() {
        GlobalStatistic expectedStatistics = new GlobalStatistic(72, 11, 95, 11);
        GlobalStatistic actualStatistics = statisticsService.getGlobalStatistics();
        assertEquals(expectedStatistics.totalGamesPlayed(), actualStatistics.totalGamesPlayed(), "Total games played don't match");
        assertEquals(expectedStatistics.totalTimePlayed(), actualStatistics.totalTimePlayed(), "Total time played don't match");
        assertEquals(expectedStatistics.totalSarcinasCreated(), actualStatistics.totalSarcinasCreated(), "Total sarcinas created don't match");
        assertEquals(expectedStatistics.totalPlayers(), actualStatistics.totalPlayers(), "Total players don't match");
    }

    @Test
    @Feature("Statistics getters")
    @DisplayName("Get box plot stats for invalid field")
    @Description("This method throws an exception when an invalid field name is provided")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("dlozaco(FBN588)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/157")
    void testGetBoxPlotStatsForInvalidField() {
        String invalidFieldName = "invalidField";
        assertThrows(IllegalArgumentException.class,
            () -> statisticsService.getBoxPlotStatsForField(invalidFieldName),
            "Expected IllegalArgumentException for invalid field name");
    }

}
