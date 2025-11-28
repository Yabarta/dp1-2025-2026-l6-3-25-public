package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * In-memory store that simulates the place where the match service drops the stats payloads
 * before the batch job consumes them. Replace with Redis, Kafka, or a DB table in production.
 */
@Component
public class TemporaryMatchStatStore {

    private static final Logger log = LoggerFactory.getLogger(TemporaryMatchStatStore.class);

    private final Map<String, List<MatchStatPayload>> buffer = new ConcurrentHashMap<>();

    public void stage(MatchStatPayload payload) {
        buffer.compute(key(payload.matchId(), payload.playerId()), (k, list) -> {
            List<MatchStatPayload> target = list == null ? new ArrayList<>() : new ArrayList<>(list);
            target.add(payload);
            return target;
        });
        log.debug("Payload staged for match {} and player {}", payload.matchId(), payload.playerId());
    }

    public List<MatchStatPayload> consume(Long matchId, Long playerId) {
        if (matchId == null || playerId == null) {
            log.warn("Trying to consume stats with missing parameters (matchId={}, playerId={})", matchId, playerId);
            return Collections.emptyList();
        }
        List<MatchStatPayload> removed = buffer.remove(key(matchId, playerId));
        return removed == null ? Collections.emptyList() : removed;
    }

    private String key(Long matchId, Long playerId) {
        return matchId + "::" + playerId;
    }
}
