package es.us.dp1.l6_3_24_25.Petris.player.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.us.dp1.l6_3_24_25.Petris.player.model.PlayerRanking;
import es.us.dp1.l6_3_24_25.Petris.player.service.RankingService;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

@WebMvcTest(controllers = RankingController.class)
@Epic("Ranking controller")
class RankingControllerTest {

    private static final String BASE_URL = "/api/v1/ranking";

    @MockitoBean
    private RankingService rankingService;

    @Autowired
    private MockMvc mockMvc;

    List<PlayerRanking> playerRankingList;


    @Test
    @Feature("Ranking getters")
    @DisplayName("Get global ranking")
    @Description("This method retrieves the global ranking")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco(FBN5868)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/157")
    @WithMockUser
    void getGlobalRanking_ReturnsRankingList_Status200() throws Exception {
        PlayerRanking player1 = new PlayerRanking(1, "player1", 100, 50, 30, 78.0, "foto.jpg");
        PlayerRanking player2 = new PlayerRanking( 2, "player2", 80, 40, 20, 65.0, "foto2.jpg");
        playerRankingList = List.of(player1, player2);

        when(rankingService.getGlobalRanking()).thenReturn(playerRankingList);

        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$[0].nickname").value("player1"))
            .andExpect(jsonPath("$[1].nickname").value("player2"))
            .andExpect(jsonPath("$[0].rankingPosition").value(1))
            .andExpect(jsonPath("$[1].rankingPosition").value(2))
            .andExpect(jsonPath("$[0].partidasJugadas").value(100))
            .andExpect(jsonPath("$[1].partidasJugadas").value(80))
            .andExpect(jsonPath("$[0].partidasGanadas").value(50))
            .andExpect(jsonPath("$[1].partidasGanadas").value(40))
            .andExpect(jsonPath("$[0].sarcinasCreadas").value(30))
            .andExpect(jsonPath("$[1].sarcinasCreadas").value(20))
            .andExpect(jsonPath("$[0].score").value(78.0))
            .andExpect(jsonPath("$[1].score").value(65.0));

    }
}
