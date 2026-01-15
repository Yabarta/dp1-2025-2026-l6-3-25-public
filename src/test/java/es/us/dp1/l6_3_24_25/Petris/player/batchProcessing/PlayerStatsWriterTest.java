package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import es.us.dp1.l6_3_24_25.Petris.player.model.Achievement;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.model.Statistics;
import es.us.dp1.l6_3_24_25.Petris.player.service.AchievementService;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import es.us.dp1.l6_3_24_25.Petris.player.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import io.qameta.allure.Owner;
import org.springframework.batch.item.Chunk;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@Owner("DiegoVicenteCamara(RXW1249)")
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
    void write_initializesStatisticsWhenMissing() throws Exception {
        Player player = new Player();
        player.setId(3);
        player.setAchievements(new ArrayList<>());
        // No statistics set to force ensureStatistics() path

        when(playerService.getPlayerById(3)).thenReturn(player);
        when(achievementService.getAllAchievements()).thenReturn(List.of());

        PlayerStatsUpdate update = new PlayerStatsUpdate(101L, 3L, 1, 0, 0, 0, 0);
        writer.write(new Chunk<>(List.of(update)));

        assertEquals(1, player.getStatistics().getGamesPlayed());
        verify(statisticsService).saveStatistics(player.getStatistics());
        verify(playerService).save(player);
    }

    @Test
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
