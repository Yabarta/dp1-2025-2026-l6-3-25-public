package es.us.dp1.l6_3_24_25.Petris.match.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;

@Epic("Lobby module")
@SpringBootTest
@Feature("Lobby DTO")
@DisplayName("LobbyDTO Tests")
class LobbyDTOTest {

    private LobbyDTO lobbyDTO;
    private Match match;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        lobbyDTO = new LobbyDTO();

        User user1 = new User();
        user1.setId(1);
        user1.setUsername("player1");

        User user2 = new User();
        user2.setId(2);
        user2.setUsername("player2");

        player1 = new Player();
        player1.setId(1);
        player1.setNickname("Player1");
        player1.setUser(user1);

        player2 = new Player();
        player2.setId(2);
        player2.setNickname("Player2");
        player2.setUser(user2);

        match = new Match();
        match.setId(1);
        match.setCode("ABCD");
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setCreator(player1);
        match.setCreatedAt(LocalDateTime.now().minusHours(1));
        match.setStartedAt(LocalDateTime.now().minusMinutes(30));
    }

    @Test
    @DisplayName("Should convert Match to LobbyDTO successfully")
    @Description("Test that a Match entity is correctly converted to LobbyDTO")
    @Story("Convert Match to Lobby DTO")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToLobbyDTO_Success() {
        LobbyDTO dto = LobbyDTO.toLobbyDTO(match);

        assertNotNull(dto);
        assertEquals(match.getId(), dto.getId());
        assertEquals(match.getCode(), dto.getCode());
        assertEquals(match.getCreator().getId(), dto.getCreatorId());
        assertEquals(match.getCreatedAt(), dto.getCreatedAt());
        assertEquals(match.getStartedAt(), dto.getStartedAt());
    }

    @Test
    @DisplayName("Should convert Match players to PlayerSummaryDTO list")
    @Description("Test that Match players are correctly converted to PlayerSummaryDTO list in Lobby")
    @Story("Convert players to lobby DTO")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToLobbyDTO_PlayersConversion() {
        LobbyDTO dto = LobbyDTO.toLobbyDTO(match);

        assertNotNull(dto.getPlayers());
        assertEquals(2, dto.getPlayers().size());
        assertEquals("Player1", dto.getPlayers().get(0).getNickname());
        assertEquals("Player2", dto.getPlayers().get(1).getNickname());
    }

    @Test
    @DisplayName("Should set isPrivate based on code existence")
    @Description("Test that isPrivate is set based on whether match has a code")
    @Story("Check privacy flag")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToLobbyDTO_PrivacyFlag() {
        LobbyDTO dto = LobbyDTO.toLobbyDTO(match);

        assertTrue(dto.isPrivate(), "Should be private when code exists");
    }

    @Test
    @DisplayName("Should handle Match with only one player")
    @Description("Test that LobbyDTO correctly handles Match with only player1")
    @Story("Handle single player lobby")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToLobbyDTO_SinglePlayer() {
        match.setPlayer2(null);
        LobbyDTO dto = LobbyDTO.toLobbyDTO(match);

        assertNotNull(dto.getPlayers());
        assertEquals(1, dto.getPlayers().size());
        assertEquals("Player1", dto.getPlayers().get(0).getNickname());
    }

    @Test
    @DisplayName("Should handle Match with no players")
    @Description("Test that LobbyDTO correctly handles Match with no players")
    @Story("Handle empty lobby")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToLobbyDTO_NoPlayers() {
        match.setPlayer1(null);
        match.setPlayer2(null);
        LobbyDTO dto = LobbyDTO.toLobbyDTO(match);

        assertNotNull(dto.getPlayers());
        assertEquals(0, dto.getPlayers().size());
    }

    @Test
    @Feature("HU-11")
    @DisplayName("Should handle null creator")
    @Description("Test that null creator is handled correctly")
    @Story("Handle null creator")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToLobbyDTO_NullCreator() {
        match.setCreator(null);
        LobbyDTO dto = LobbyDTO.toLobbyDTO(match);

        assertNotNull(dto);
        assertNull(dto.getCreatorId());
    }

    @Test
    @DisplayName("Should set isPrivate to false when code is null")
    @Description("Test that isPrivate is false when match has no code")
    @Story("Check public flag")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToLobbyDTO_PublicMatch() {
        match.setCode(null);
        LobbyDTO dto = LobbyDTO.toLobbyDTO(match);

        assertFalse(dto.isPrivate(), "Should not be private when code is null");
    }

    @Test
    @DisplayName("Should set and get all DTO fields")
    @Description("Test that all DTO fields can be set and retrieved")
    @Story("DTO field operations")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testLobbyDTOGettersSetters() {
        lobbyDTO.setId(5);
        lobbyDTO.setCode("XXXX");
        lobbyDTO.setPrivate(true);
        lobbyDTO.setCreatorId(1);
        LocalDateTime now = LocalDateTime.now();
        lobbyDTO.setCreatedAt(now);
        lobbyDTO.setStartedAt(now.plusMinutes(1));

        assertEquals(5, lobbyDTO.getId());
        assertEquals("XXXX", lobbyDTO.getCode());
        assertTrue(lobbyDTO.isPrivate());
        assertEquals(1, lobbyDTO.getCreatorId());
        assertEquals(now, lobbyDTO.getCreatedAt());
        assertEquals(now.plusMinutes(1), lobbyDTO.getStartedAt());
    }

    @Test
    @DisplayName("Should initialize players list as empty")
    @Description("Test that players list is initialized as empty ArrayList")
    @Story("Initialize players list")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testLobbyDTOPlayersInitialization() {
        LobbyDTO newDTO = new LobbyDTO();
        assertNotNull(newDTO.getPlayers());
        assertEquals(0, newDTO.getPlayers().size());
    }

    @Test
    @DisplayName("Should preserve all match data in conversion")
    @Description("Test that all match data is preserved during conversion")
    @Story("Data preservation")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToLobbyDTO_DataPreservation() {
        match.setId(99);
        match.setCode("UNIQUE");
        player1.setId(88);
        player1.setNickname("CreatorNick");

        LobbyDTO dto = LobbyDTO.toLobbyDTO(match);

        assertEquals(99, dto.getId());
        assertEquals("UNIQUE", dto.getCode());
        assertEquals(88, dto.getCreatorId());
    }

    @Test
    @DisplayName("Should handle player with null user in players list")
    @Description("Test that players with null user are handled correctly")
    @Story("Handle player without user")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToLobbyDTO_PlayerWithNullUser() {
        player2.setUser(null);
        LobbyDTO dto = LobbyDTO.toLobbyDTO(match);

        assertNotNull(dto.getPlayers());
        assertEquals(2, dto.getPlayers().size());
        assertNull(dto.getPlayers().get(1).getUsername());
    }
}
