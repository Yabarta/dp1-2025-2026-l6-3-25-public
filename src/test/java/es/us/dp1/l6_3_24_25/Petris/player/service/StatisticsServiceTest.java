package es.us.dp1.l6_3_24_25.Petris.player.service;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.player.model.Statistics;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    @Owner("dlozaco")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/136")
    void getAllStatistics() {
        List<Statistics> statisticsList = statisticsService.getAllStatistics();
        assertEquals(10, statisticsList.size(), "Incorrect number of statistics");
    }

    @Test
    @Feature("Statistics getters")
    @DisplayName("Get statistic by id")
    @Description("This method received an statistic by a correct id")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco")
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
    @Owner("dlozaco")
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
    @Owner("dlozaco")
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
}
