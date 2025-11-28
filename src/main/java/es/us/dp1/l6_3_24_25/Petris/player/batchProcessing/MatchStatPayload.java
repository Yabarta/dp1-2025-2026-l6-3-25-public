package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;
public record MatchStatPayload(
    Long matchId,
    Long playerId,
    boolean playerWon,
    int sarcinesCreated
) {
}
