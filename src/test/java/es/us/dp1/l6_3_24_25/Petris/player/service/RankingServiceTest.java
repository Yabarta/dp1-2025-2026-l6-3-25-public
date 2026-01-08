package es.us.dp1.l6_3_24_25.Petris.player.service;

import es.us.dp1.l6_3_24_25.Petris.player.model.PlayerRanking;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Epic("Ranking Service")
class RankingServiceTest {

    @Autowired
    private RankingService rankingService;

    @Test
    @Feature("Ranking getter")
    @DisplayName("Get global ranking")
    @Description("This method received the global ranking of players")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco(FBN588)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/157")
    void testGetGlobalRanking() {
        List<PlayerRanking> ranking = rankingService.getGlobalRanking();
        assertEquals(7, ranking.size(), "Incorrect number of players in ranking");

        for (int i = 1; i < ranking.size(); i++) {
            assertTrue(ranking.get(i - 1).getScore() >= ranking.get(i).getScore(),
                    "Ranking is not sorted by score descending");
        }

        for (int i = 0; i < ranking.size(); i++) {
            assertEquals(i + 1, ranking.get(i).getRankingPosition(),
                    "Incorrect ranking position for player " + ranking.get(i).getNickname());
        }
    }
}
