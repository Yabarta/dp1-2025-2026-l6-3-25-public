package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import io.qameta.allure.Owner;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Owner("DiegoVicenteCamara(RXW1249)")
class TemporaryMatchStatStoreTest {

    @Test
    void stageAndConsumeReturnsStoredPayloadsAndClears() {
        TemporaryMatchStatStore store = new TemporaryMatchStatStore();
        MatchStatPayload payload = new MatchStatPayload(1L, 2L, 1, 0, 0, 0, 0);

        store.stage(payload);
        List<MatchStatPayload> consumed = store.consume(1L, 2L);

        assertEquals(1, consumed.size());
        assertEquals(payload, consumed.get(0));
        assertTrue(store.consume(1L, 2L).isEmpty(), "Store should be empty after consume");
    }

    @Test
    void consumeWithMissingParamsReturnsEmpty() {
        TemporaryMatchStatStore store = new TemporaryMatchStatStore();
        assertTrue(store.consume(null, null).isEmpty());
    }
}
