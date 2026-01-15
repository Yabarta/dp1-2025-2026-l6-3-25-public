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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Match statistics batch")
@Feature("Temporary stats store")
class TemporaryMatchStatStoreTest {

    @Test
    @Story("Stage and consume")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("stage/consume devuelve payloads y limpia")
    @Description("Verifies that staged payloads are returned once and the store is cleared after consume.")
    @Owner("DiegoVicenteCamara(RXW1249)")
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
    @Story("Consume with missing parameters")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("consume devuelve vacío con parámetros nulos")
    @Description("Verifies that consume returns an empty list when match or player IDs are null.")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void consumeWithMissingParamsReturnsEmpty() {
        TemporaryMatchStatStore store = new TemporaryMatchStatStore();
        assertTrue(store.consume(null, null).isEmpty());
    }
}
