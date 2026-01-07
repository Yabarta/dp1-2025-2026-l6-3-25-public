package es.us.dp1.l6_3_24_25.Petris.match.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;

@Epic("Match module")
@Feature("Player Summary DTO")
@SpringBootTest
@DisplayName("PlayerSummaryDTO Tests")
class PlayerSummaryDTOTest {

    private Player player;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("testuser");

        player = new Player();
        player.setId(1);
        player.setNickname("TestNickname");
        player.setUser(user);
    }

    @Test
    @DisplayName("Should convert Player to PlayerSummaryDTO successfully")
    @Description("Test that a Player entity is correctly converted to PlayerSummaryDTO")
    @Story("Convert Player to Summary DTO")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToPlayerSummary_Success() {
        PlayerSummaryDTO dto = PlayerSummaryDTO.toPlayerSummary(player);

        assertNotNull(dto);
        assertEquals(player.getId(), dto.getId());
        assertEquals(player.getNickname(), dto.getNickname());
        assertEquals(player.getUser().getUsername(), dto.getUsername());
    }

    @Test
    @DisplayName("Should handle null Player")
    @Description("Test that null Player returns null")
    @Story("Handle null Player")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToPlayerSummary_NullPlayer() {
        PlayerSummaryDTO dto = PlayerSummaryDTO.toPlayerSummary(null);

        assertNull(dto);
    }

    @Test
    @DisplayName("Should handle Player with null User")
    @Description("Test that Player with null User is handled correctly")
    @Story("Handle null User")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToPlayerSummary_NullUser() {
        player.setUser(null);
        PlayerSummaryDTO dto = PlayerSummaryDTO.toPlayerSummary(player);

        assertNotNull(dto);
        assertEquals(player.getId(), dto.getId());
        assertEquals(player.getNickname(), dto.getNickname());
        assertNull(dto.getUsername());
    }

    @Test
    @DisplayName("Should set and get all DTO fields")
    @Description("Test that all DTO fields can be set and retrieved")
    @Story("DTO field operations")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testPlayerSummaryDTOGettersSetters() {
        PlayerSummaryDTO dto = new PlayerSummaryDTO();
        dto.setId(5);
        dto.setNickname("NewNickname");
        dto.setUsername("newuser");

        assertEquals(5, dto.getId());
        assertEquals("NewNickname", dto.getNickname());
        assertEquals("newuser", dto.getUsername());
    }

    @Test
    @DisplayName("Should preserve all player data in conversion")
    @Description("Test that all player data is preserved during conversion")
    @Story("Data preservation")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToPlayerSummary_DataPreservation() {
        player.setId(99);
        player.setNickname("UniqueNickname");
        user.setUsername("uniqueuser");

        PlayerSummaryDTO dto = PlayerSummaryDTO.toPlayerSummary(player);

        assertEquals(99, dto.getId());
        assertEquals("UniqueNickname", dto.getNickname());
        assertEquals("uniqueuser", dto.getUsername());
    }
}
