package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.lang.NonNull;

import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.model.Statistics;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import es.us.dp1.l6_3_24_25.Petris.player.service.StatisticsService;

public class PlayerStatsWriter implements ItemWriter<PlayerStatsUpdate> {

    private static final Logger log = LoggerFactory.getLogger(PlayerStatsWriter.class);

    private final PlayerService playerService;
    private final StatisticsService statisticsService;

    public PlayerStatsWriter(PlayerService playerService, StatisticsService statisticsService) {
        this.playerService = playerService;
        this.statisticsService = statisticsService;
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

        boolean changed = false;
        if (update.gamesPlayedDelta() > 0) {
            statistics.setGamesPlayed(safeAdd(statistics.getGamesPlayed(), update.gamesPlayedDelta(), "gamesPlayed"));
            changed = true;
        }
        if (update.gamesWonDelta() > 0) {
            statistics.setGamesWon(safeAdd(statistics.getGamesWon(), update.gamesWonDelta(), "gamesWon"));
            changed = true;
        }
        if (update.sarcinesCreatedDelta() > 0) {
            statistics.setSarcinesCreated(safeAdd(statistics.getSarcinesCreated(), update.sarcinesCreatedDelta(), "sarcinesCreated"));
            changed = true;
        }

        if (!changed) {
            log.info("No statistic deltas to persist for player {} in match {}", player.getId(), update.matchId());
            return;
        }

        statisticsService.saveStatistics(statistics);
        playerService.save(player);
        log.info(
            "Persisted stats for player {} after match {} -> gamesPlayed={}, gamesWon={}, sarcinesCreated={}",
            player.getId(),
            update.matchId(),
            statistics.getGamesPlayed(),
            statistics.getGamesWon(),
            statistics.getSarcinesCreated()
        );
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
