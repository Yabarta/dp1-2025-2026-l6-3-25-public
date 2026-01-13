package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import io.qameta.allure.Owner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Owner("DiegoVicenteCamara(RXW1249)")
class StatProcessorTest {

    @Test
    void process_clampsNegativesToZero() throws Exception {
        StatProcessor processor = new StatProcessor();
        MatchStatPayload payload = new MatchStatPayload(1L, 2L, -1, -2, -3, -4, -5);

        PlayerStatsUpdate update = processor.process(payload);

        assertNotNull(update);
        assertEquals(0, update.gamesPlayedDelta());
        assertEquals(0, update.gamesWonDelta());
        assertEquals(0, update.sarcinasCreatedDelta());
        assertEquals(0, update.timePlayedDelta());
        assertEquals(0, update.bacteriasCreatedDelta());
    }

    @Test
    void process_keepsPositiveDeltas() throws Exception {
        StatProcessor processor = new StatProcessor();
        MatchStatPayload payload = new MatchStatPayload(3L, 4L, 1, 2, 3, 4, 5);

        PlayerStatsUpdate update = processor.process(payload);

        assertEquals(1, update.gamesPlayedDelta());
        assertEquals(2, update.gamesWonDelta());
        assertEquals(3, update.sarcinasCreatedDelta());
        assertEquals(4, update.timePlayedDelta());
        assertEquals(5, update.bacteriasCreatedDelta());
    }
}
