package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Match statistics batch")
@Feature("Batch processor")
class StatProcessorTest {

    @Test
    @Story("Clamp invalid deltas")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("process ajusta negativos a cero")
    @Description("Verifies that negative deltas are clamped to zero when processing payloads.")
    @Owner("DiegoVicenteCamara(RXW1249)")
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
    @Story("Preserve positive deltas")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("process conserva deltas positivos")
    @Description("Verifies that positive deltas are preserved during processing.")
    @Owner("DiegoVicenteCamara(RXW1249)")
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
