package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import io.qameta.allure.Owner;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Owner("DiegoVicenteCamara(RXW1249)")
class StatDataReaderTest {

    @Test
    void read_deliversItemsThenNull() {
        MatchStatPayload first = new MatchStatPayload(1L, 1L, 1, 0, 0, 0, 0);
        MatchStatPayload second = new MatchStatPayload(2L, 2L, 1, 1, 0, 0, 0);
        StatDataReader reader = new StatDataReader(List.of(first, second));

        assertEquals(first, reader.read());
        assertEquals(second, reader.read());
        assertNull(reader.read());
    }
}
