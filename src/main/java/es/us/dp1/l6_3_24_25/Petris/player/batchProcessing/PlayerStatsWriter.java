package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.lang.NonNull;

import java.util.List;

import es.us.dp1.l6_3_24_25.Petris.player.model.Achievement;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.model.Statistics;
import es.us.dp1.l6_3_24_25.Petris.player.service.AchievementService;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import es.us.dp1.l6_3_24_25.Petris.player.service.StatisticsService;

public class PlayerStatsWriter implements ItemWriter<PlayerStatsUpdate> {

    private static final Logger log = LoggerFactory.getLogger(PlayerStatsWriter.class);

    private final PlayerService playerService;
    private final StatisticsService statisticsService;
    private final AchievementService achievementService;

    public PlayerStatsWriter(PlayerService playerService, StatisticsService statisticsService, AchievementService achievementService) {
        this.playerService = playerService;
        this.statisticsService = statisticsService;
        this.achievementService = achievementService;
    }
    @Override
    public void write(@NonNull Chunk<? extends PlayerStatsUpdate> items) {
        for (PlayerStatsUpdate update : items) {
            applyUpdate(update);
        }
    }

    private void applyUpdate(PlayerStatsUpdate update) {
        Player player = playerService.getPlayerById(Math.toIntExact(update.playerId()));
        Statistics statistics = ensureStatistics(player);

        boolean statsChanged = false;
        int totaDelta = update.gamesPlayedDelta() + update.gamesWonDelta() + update.sarcinasCreatedDelta()
                + update.timePlayedDelta() + update.bacteriasCreatedDelta();
                
        if (totaDelta > 0) {
            statistics.setGamesPlayed(safeAdd(statistics.getGamesPlayed(), update.gamesPlayedDelta(), "gamesPlayed"));
            statistics.setGamesWon(safeAdd(statistics.getGamesWon(), update.gamesWonDelta(), "gamesWon"));
            statistics.setSarcinasCreated(safeAdd(statistics.getSarcinasCreated(), update.sarcinasCreatedDelta(), "sarcinasCreated"));
            statistics.setTimePlayed(safeAdd(statistics.getTimePlayed(), update.timePlayedDelta(), "timePlayed"));
            statistics.setBacteriasCreated(safeAdd(statistics.getBacteriasCreated(), update.bacteriasCreatedDelta(), "bacteriasCreated"));
            statsChanged = true;
        }

        boolean achievementsAdded = awardNewAchievements(player, statistics);

        if (!statsChanged && !achievementsAdded) {
            log.info("No statistic deltas to persist for player {} in match {}", player.getId(), update.matchId());
            return;
        }

        if (statsChanged) {
            statisticsService.saveStatistics(statistics);
        }
        if (achievementsAdded || statsChanged) {
            playerService.save(player);
        }
        log.info(
            "Persisted stats for player {} after match {} -> gamesPlayed={}, gamesWon={}, sarcinesCreated={}, timePlayed={}, bacteriasCreated={}",
            player.getId(),
            update.matchId(),
            statistics.getGamesPlayed(),
            statistics.getGamesWon(),
            statistics.getSarcinasCreated(),
            statistics.getTimePlayed(),
            statistics.getBacteriasCreated()
        );
    }

    private boolean awardNewAchievements(Player player, Statistics statistics) {
        List<Achievement> unlocked = player.getAchievements();
        boolean added = false;

        for (Achievement achievement : achievementService.getAllAchievements()) {
            if (unlocked.contains(achievement)) {
                continue;
            }
            Integer currentValue = statistics.getStatisticByName(achievement.getStatisticName());
            if (currentValue != null && currentValue >= achievement.getValor()) {
                unlocked.add(achievement);
                added = true;
                log.info("Player {} unlocked achievement {}", player.getId(), achievement.getName());
            }
        }

        return added;
    }

    private Statistics ensureStatistics(Player player) {
        Statistics statistics = player.getStatistics();
        if (statistics == null) {
            statistics = new Statistics();
            player.setStatistics(statistics);
        }
        return statistics;
    }

    private int safeAdd(Integer currentValue, int delta, String fieldName) {
        int base = currentValue == null ? 0 : currentValue;
        int total = base + delta;
        if (total < 0) {
            log.warn("Statistic {} would become negative ({}). Clamping to zero.", fieldName, total);
            return 0;
        }
        return total;
    }
}
