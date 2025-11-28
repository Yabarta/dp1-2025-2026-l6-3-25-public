package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import java.time.Duration;

/**
 * Raw data captured when a match ends. In a real implementation this would come directly
 * from the match microservice or an event payload.
 */
public record MatchStatPayload(
    Long matchId,
    Long playerId,
    boolean playerWon,
    int playerScore,
    int sarcinesCreated,
    int bacteriaPlaced,
    Duration matchDuration,
    int totalTurns
) {
}
