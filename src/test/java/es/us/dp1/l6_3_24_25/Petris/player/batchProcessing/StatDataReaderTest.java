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
@Feature("Batch reader")
class StatDataReaderTest {

    @Test
    @Story("Read staged payloads")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("read entrega elementos y luego null")
    @Description("Verifies that the reader returns all items in order and then null at the end.")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void read_deliversItemsThenNull() {
        MatchStatPayload first = new MatchStatPayload(1L, 1L, 1, 0, 0, 0, 0);
        MatchStatPayload second = new MatchStatPayload(2L, 2L, 1, 1, 0, 0, 0);
        StatDataReader reader = new StatDataReader(List.of(first, second));

        assertEquals(first, reader.read());
        assertEquals(second, reader.read());
        assertNull(reader.read());
    }
}
