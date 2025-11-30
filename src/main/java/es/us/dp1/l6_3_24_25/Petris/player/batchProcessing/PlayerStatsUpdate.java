package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import java.util.Map;

/**
 * Aggregated statistics ready to be persisted in the player profile.
 */
public record PlayerStatsUpdate(
    Long matchId,
    Long playerId,
    Map<String, Integer> deltaByStatistic
) {
}
