package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
//Delta se refiere al valor a añadir a la nueva estadística
    @Override
    public void write(@NonNull Chunk<? extends PlayerStatsUpdate> items) {
        for (PlayerStatsUpdate update : items) {
            applyUpdate(update);
        }
    }

    private void applyUpdate(PlayerStatsUpdate update) {
        Player player = playerService.getPlayerById(Math.toIntExact(update.playerId()));
        Map<String, Integer> deltas = update.deltaByStatistic();
        if (deltas == null || deltas.isEmpty()) {
            return;
        }

        List<Statistics> stats = ensureStatsList(player);

        for (Map.Entry<String, Integer> entry : deltas.entrySet()) {
            String statName = entry.getKey();
            Integer delta = entry.getValue();
            if (statName == null || delta == null) {
                continue;
            }

            Statistics statistic = findOrCreateStatistic(stats, statName);
            int currentValue = statistic.getValor() == null ? 0 : statistic.getValor();
            statistic.setValor(currentValue + delta);
            statisticsService.saveStatistics(statistic);
        }

        playerService.save(player);
        log.info("Persisted stats for player {} after match {} -> {}",
            player.getId(), update.matchId(), deltas);
    }

    private List<Statistics> ensureStatsList(Player player) {
        List<Statistics> stats = player.getStatistics();
        if (stats == null) {
            stats = new ArrayList<>();
            player.setStatistics(stats);
        }
        return stats;
    }

    private Statistics findOrCreateStatistic(List<Statistics> stats, String statName) {
        return stats.stream()
            .filter(stat -> statName.equalsIgnoreCase(stat.getName()))
            .findFirst()
            .orElseGet(() -> {
                Statistics statistic = new Statistics();
                statistic.setName(statName);
                statistic.setValor(0);
                stats.add(statistic);
                return statistic;
            });
    }
}
