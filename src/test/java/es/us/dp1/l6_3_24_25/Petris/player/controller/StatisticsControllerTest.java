package es.us.dp1.l6_3_24_25.Petris.player.controller;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.player.model.GlobalStatistic;
import es.us.dp1.l6_3_24_25.Petris.player.model.Statistics;
import es.us.dp1.l6_3_24_25.Petris.player.service.StatisticsService;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatisticsController.class)
@Epic("Statistics Controller")
class StatisticsControllerTest {

    private final static String BASE_URL = "/api/v1/statistics";

    @MockitoBean
    private StatisticsService statisticsService;

    @Autowired
    private MockMvc mockMvc;

    private List<Statistics> statistics;
    private GlobalStatistic globalStatistic;

    @BeforeEach
    void setUp() {
        Statistics stat1 = Statistics.builder()
            .gamesPlayed(20)
            .gamesWon(10)
            .timePlayed(500)
            .sarcinasCreated(50)
            .bacteriasCreated(30)
            .build();
        Statistics stat2 = Statistics.builder()
            .gamesPlayed(15)
            .gamesWon(5)
            .timePlayed(300)
            .sarcinasCreated(20)
            .bacteriasCreated(10)
            .build();
        Statistics stat3 = Statistics.builder()
            .gamesPlayed(5)
            .gamesWon(1)
            .timePlayed(100)
            .sarcinasCreated(5)
            .bacteriasCreated(2)
            .build();
        statistics = List.of(stat1, stat2, stat3);
        globalStatistic = new GlobalStatistic(
            40,
            900,
            25,
            1);
    }


    @Test
    @Feature("HU-26: Ver estadísticas personales (jugador)")
    @DisplayName("Get all statistics")
    @Description("This method retrieves all player statistics")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco(FBN5868)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/157")
    @WithMockUser
    void getAllStatistics_ReturnsStatisticsList_Status200() throws Exception {
        when(statisticsService.getAllStatistics()).thenReturn(statistics);

        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(statistics.size()))
            .andExpect(jsonPath("$[0].gamesPlayed").value(20))
            .andExpect(jsonPath("$[1].gamesWon").value(5));
    }

    @Test
    @Feature("HU-26: Ver estadísticas personales (jugador)")
    @DisplayName("Get global statistics array")
    @Description("This method retrieves the global statistics array")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco(FBN5868)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/157")
    @WithMockUser
    void getGlobalStatisticsArray_ReturnsGlobalStatistic_Status200() throws Exception {

        when(statisticsService.getGlobalStatistics()).thenReturn(globalStatistic);

        mockMvc.perform(get(BASE_URL + "/global"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalGamesPlayed").value(40))
            .andExpect(jsonPath("$.totalSarcinasCreated").value(25))
            .andExpect(jsonPath("$.totalTimePlayed").value(900))
            .andExpect(jsonPath("$.totalPlayers").value(1));
    }

    @Test
    @Feature("HU-26: Ver estadísticas personales (jugador)")
    @DisplayName("Get statistics by ID")
    @Description("This method retrieves statistics by ID")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco(FBN5868)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/157")
    @WithMockUser
    void getStatisticsById_ExistingId_ReturnsStatistics_Status200() throws Exception {
        Statistics stat = statistics.getFirst();
        when(statisticsService.getStatisticsById(1)).thenReturn(stat);

        mockMvc.perform(get(BASE_URL + "/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.gamesPlayed").value(20))
            .andExpect(jsonPath("$.gamesWon").value(10));
    }

    @Test
    @Feature("HU-26: Ver estadísticas personales (jugador)")
    @DisplayName("Get statistics by invalid ID")
    @Description("This method verifies the behavior when an invalid ID is provided")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("dlozaco(FBN5868)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/157")
    @WithMockUser
    void getStatisticsById_InvalidId_ReturnsNotFound_Status404() throws Exception {
        int invalidId = 999;
        when(statisticsService.getStatisticsById(invalidId))
            .thenThrow(new ResourceNotFoundException("Statistics not found for ID: " + invalidId));

        mockMvc.perform(get(BASE_URL + "/" + invalidId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Statistics not found for ID: " + invalidId));
    }

    @ParameterizedTest
    @Feature("HU-26: Ver estadísticas personales (jugador)")
    @DisplayName("Get statistics distribution")
    @Description("This method retrieves the statistics distribution for a given field")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco(FBN5868)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/157")
    @WithMockUser
    @CsvSource({
        "gamesPlayed",
        "gamesWon",
        "timePlayed",
        "sarcinasCreated",
        "bacteriasCreated"
    })
    void getStatisticsDistribution_ReturnsDistribution_Status200(String fieldName) throws Exception {
        List<Double> distribution = List.of(10.0, 20.0, 30.0);
        when(statisticsService.getBoxPlotStatsForField(fieldName)).thenReturn(distribution);

        mockMvc.perform(get(BASE_URL + "/distribution/" + fieldName))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(3))
            .andExpect(jsonPath("$[0]").value(10.0))
            .andExpect(jsonPath("$[1]").value(20.0));
    }

    @Test
    @Feature("HU-26: Ver estadísticas personales (jugador)")
    @DisplayName("Get statistics distribution with invalid field")
    @Description("This method verifies the behavior when an invalid field name is provided")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("dlozaco(FBN5868)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/157")
    @WithMockUser
    void getStatisticsDistribution_InvalidField_ReturnsBadRequest_Status400() throws Exception {
        String invalidField = "invalidField";
        when(statisticsService.getBoxPlotStatsForField(invalidField))
            .thenThrow(new IllegalArgumentException("Invalid field name: " + invalidField));

        mockMvc.perform(get(BASE_URL + "/distribution/" + invalidField))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid field name: " + invalidField));
    }
}
