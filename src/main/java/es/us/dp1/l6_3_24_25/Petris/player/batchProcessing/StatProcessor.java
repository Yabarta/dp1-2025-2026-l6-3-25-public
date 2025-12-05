package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

/**
 * Calculates the aggregated deltas that must be applied to a player's persistent statistics.
 */
public class StatProcessor implements ItemProcessor<MatchStatPayload, PlayerStatsUpdate> {

    private static final Logger log = LoggerFactory.getLogger(StatProcessor.class);

    @Override
    public PlayerStatsUpdate process(@NonNull MatchStatPayload payload) {
        int gamesPlayedDelta = sanitizeDelta(payload.gamesPlayedDelta(), "gamesPlayed");
        int gamesWonDelta = sanitizeDelta(payload.gamesWonDelta(), "gamesWon");
        int sarcinesDelta = sanitizeDelta(payload.sarcinesCreatedDelta(), "sarcinesCreated");

        PlayerStatsUpdate update = new PlayerStatsUpdate(
            payload.matchId(),
            payload.playerId(),
            gamesPlayedDelta,
            gamesWonDelta,
            sarcinesDelta
        );

        log.info(
            "Aggregated deltas for player {} -> gamesPlayed={}, gamesWon={}, sarcinesCreated={}",
            payload.playerId(), gamesPlayedDelta, gamesWonDelta, sarcinesDelta
        );
        return update;
    }

    private int sanitizeDelta(int rawDelta, String fieldName) {
        if (rawDelta < 0) {
            log.warn("Delta for {} was negative ({}). Clamping to zero.", fieldName, rawDelta);
            return 0;
        }
        return rawDelta;
    }
}
