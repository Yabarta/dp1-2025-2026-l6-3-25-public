package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.lang.NonNull;

/**
 * {@link ItemWriter} that simulates the persistence of the aggregated statistics.
 * Replace the logging statements with calls to PlayerService/StatisticsService to
 * make the update permanent in the domain model.
 */
public class PlayerStatsWriter implements ItemWriter<PlayerStatsUpdate> {

    private static final Logger log = LoggerFactory.getLogger(PlayerStatsWriter.class);

    @Override
    public void write(@NonNull Chunk<? extends PlayerStatsUpdate> items) {
        for (PlayerStatsUpdate update : items) {
            log.info("Persisting stats for player {} (match {}): {}",
                update.playerId(), update.matchId(), update.deltaByStatistic());
            // Example: playerService.applyStatDeltas(update.playerId(), update.deltaByStatistic());
        }
    }
}
