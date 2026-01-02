package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

public record MatchStatPayload(
    Long matchId,
    Long playerId,
    int gamesPlayedDelta,
    int gamesWonDelta,
    int sarcinasCreatedDelta,
    int timePlayedDelta,
    int bacteriasCreatedDelta
) {
}
