package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

/**
 * Aggregated statistics ready to be persisted in the player profile.
 */
public record PlayerStatsUpdate(
    Long matchId,
    Long playerId,
    int gamesPlayedDelta,
    int gamesWonDelta,
    int sarcinesCreatedDelta
) {
}
