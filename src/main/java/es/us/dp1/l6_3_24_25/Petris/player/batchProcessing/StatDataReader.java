package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;

public class StatDataReader implements ItemReader<MatchStatPayload> {

    private static final Logger log = LoggerFactory.getLogger(StatDataReader.class);

    private final Iterator<MatchStatPayload> iterator;

    public StatDataReader(List<MatchStatPayload> stagedPayloads) {
        this.iterator = stagedPayloads.iterator();
        log.info("StatDataReader initialized with {} payload(s)", stagedPayloads.size());
    }

    @Override
    public MatchStatPayload read() {
        if (iterator.hasNext()) {
            MatchStatPayload next = iterator.next();
            log.info("Delivering payload for match {} and player {}", next.matchId(), next.playerId());
            return next;
        }
        return null;
    }
}
