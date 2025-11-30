package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

/**
 * Calculates the aggregated deltas that must be applied to a player's persistent statistics.
 */
public class StatProcessor implements ItemProcessor<MatchStatPayload, PlayerStatsUpdate> {

    private static final Logger log = LoggerFactory.getLogger(StatProcessor.class);

    // Statistic identifiers stored in the DB (see data.sql and Statistics entity)
    public static final String GAMES_PLAYED = "games_played";
    public static final String GAMES_WON = "games_won";
    public static final String SARCINES_CREATED = "sarcines_created";

    @Override
    public PlayerStatsUpdate process(@NonNull MatchStatPayload payload) {
        Map<String, Integer> deltas = new HashMap<>();
        deltas.put(GAMES_PLAYED, 1);
        deltas.put(SARCINES_CREATED, payload.sarcinesCreated());

        if (payload.playerWon()) {
            deltas.put(GAMES_WON, 1);
        }

        log.info("Aggregated deltas for player {} -> {}", payload.playerId(), deltas);
        return new PlayerStatsUpdate(payload.matchId(), payload.playerId(), deltas);
    }
}
