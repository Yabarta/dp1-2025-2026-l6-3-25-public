package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import es.us.dp1.l6_3_24_25.Petris.player.model.Achievement;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.model.Statistics;
import es.us.dp1.l6_3_24_25.Petris.player.service.AchievementService;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import es.us.dp1.l6_3_24_25.Petris.player.service.StatisticsService;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Epic("Match statistics batch")
@Feature("Player stats writer")
@ExtendWith(MockitoExtension.class)
class PlayerStatsWriterTest {

    @Mock
    private PlayerService playerService;

    @Mock
    private StatisticsService statisticsService;

    @Mock
    private AchievementService achievementService;

    private PlayerStatsWriter writer;

    @BeforeEach
    void setUp() {
        writer = new PlayerStatsWriter(playerService, statisticsService, achievementService);
    }

    @Test
    @Story("Apply stats updates")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("write aplica deltas y asigna logros")
    @Description("Verifies that write applies deltas and awards achievements when thresholds are reached.")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void write_appliesDeltasAndAwardsAchievements() throws Exception {
        Player player = new Player();
        player.setId(1);
        player.setAchievements(new ArrayList<>());

        Statistics statistics = new Statistics();
        statistics.setGamesPlayed(4);
        statistics.setGamesWon(4);
        statistics.setSarcinasCreated(0);
        statistics.setTimePlayed(0);
        statistics.setBacteriasCreated(0);
        player.setStatistics(statistics);

        Achievement achievement = new Achievement();
        achievement.setId(10);
        achievement.setName("Win 5");
        achievement.setValor(5);
        achievement.setStatisticName("games_won");
        achievement.setImage("img");

        when(playerService.getPlayerById(1)).thenReturn(player);
        when(achievementService.getAllAchievements()).thenReturn(List.of(achievement));

        PlayerStatsUpdate update = new PlayerStatsUpdate(99L, 1L, 1, 1, 0, 0, 0);
        writer.write(new Chunk<>(List.of(update)));

        assertEquals(5, player.getStatistics().getGamesWon());
        assertTrue(player.getAchievements().contains(achievement));
        verify(statisticsService).saveStatistics(statistics);
        verify(playerService).save(player);
    }

    @Test
    @Story("Apply stats updates")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("write no guarda si no hay cambios")
    @Description("Verifies that write skips persistence when the update has zero deltas.")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void write_skipsSaveWhenNoChanges() throws Exception {
        Player player = new Player();
        player.setId(2);
        player.setAchievements(new ArrayList<>());

        Statistics statistics = new Statistics();
        statistics.setGamesPlayed(5);
        statistics.setGamesWon(5);
        statistics.setSarcinasCreated(0);
        statistics.setTimePlayed(0);
        statistics.setBacteriasCreated(0);
        player.setStatistics(statistics);

        Achievement achievement = new Achievement();
        achievement.setId(11);
        achievement.setName("Win 5");
        achievement.setValor(5);
        achievement.setStatisticName("games_won");
        achievement.setImage("img");

        player.getAchievements().add(achievement);

        when(playerService.getPlayerById(2)).thenReturn(player);
        when(achievementService.getAllAchievements()).thenReturn(List.of(achievement));

        PlayerStatsUpdate update = new PlayerStatsUpdate(100L, 2L, 0, 0, 0, 0, 0);
        writer.write(new Chunk<>(List.of(update)));

        verify(statisticsService, never()).saveStatistics(any());
        verify(playerService, never()).save(any(Player.class));
    }

    @Test
    @Story("Apply stats updates")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("write inicializa estadísticas si faltan")
    @Description("Verifies that write initializes statistics when the player does not have them yet.")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void write_initializesStatisticsWhenMissing() throws Exception {
        Player player = new Player();
        player.setId(3);
        player.setAchievements(new ArrayList<>());

        when(playerService.getPlayerById(3)).thenReturn(player);
        when(achievementService.getAllAchievements()).thenReturn(List.of());

        PlayerStatsUpdate update = new PlayerStatsUpdate(101L, 3L, 1, 0, 0, 0, 0);
        writer.write(new Chunk<>(List.of(update)));

        assertEquals(1, player.getStatistics().getGamesPlayed());
        verify(statisticsService).saveStatistics(player.getStatistics());
        verify(playerService).save(player);
    }

    @Test
    @Story("Apply stats updates")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("write no duplica logros existentes")
    @Description("Verifies that write does not add duplicate achievements already owned by the player.")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void write_doesNotDuplicateExistingAchievements() throws Exception {
        Player player = new Player();
        player.setId(4);
        player.setAchievements(new ArrayList<>());

        Statistics statistics = new Statistics();
        statistics.setGamesPlayed(10);
        statistics.setGamesWon(10);
        statistics.setSarcinasCreated(0);
        statistics.setTimePlayed(0);
        statistics.setBacteriasCreated(0);
        player.setStatistics(statistics);

        Achievement achievement = new Achievement();
        achievement.setId(12);
        achievement.setName("Win 5");
        achievement.setValor(5);
        achievement.setStatisticName("games_won");
        achievement.setImage("img");
        player.getAchievements().add(achievement);

        when(playerService.getPlayerById(4)).thenReturn(player);
        when(achievementService.getAllAchievements()).thenReturn(List.of(achievement));

        PlayerStatsUpdate update = new PlayerStatsUpdate(102L, 4L, 0, 0, 0, 0, 0);
        writer.write(new Chunk<>(List.of(update)));

        assertEquals(1, player.getAchievements().size());
        verify(achievementService).getAllAchievements();
        verify(playerService, never()).save(player);
    }
}
