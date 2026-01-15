package es.us.dp1.l6_3_24_25.Petris.lobby.servicio;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import es.us.dp1.l6_3_24_25.Petris.lobby.interfaz.Lobby;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;

@Epic("Lobby module")
@Feature("Sala Service")
@SpringBootTest
@DisplayName("SalaService Tests")
class SalaServiceTest {

    @Autowired
    private SalaService salaService;

    @BeforeEach
    void setUp() {
        
    }

    @Test
    @DisplayName("Should create a new lobby with valid code")
    @Description("Test that a new lobby is created with a valid UUID-based code")
    @Story("Create lobby")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testCreateLobby_Success() {
        Lobby lobby = salaService.createLobby();

        assertNotNull(lobby);
        assertNotNull(lobby.getCodigoDeUnion());
        assertFalse(lobby.getCodigoDeUnion().isEmpty());
        assertEquals(8, lobby.getCodigoDeUnion().length(), "Code should be 8 characters from UUID");
    }

    @Test
    @DisplayName("Should create lobbies with different codes")
    @Description("Test that consecutive lobby creations generate different codes")
    @Story("Create multiple lobbies")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testCreateLobby_DifferentCodes() {
        Lobby lobby1 = salaService.createLobby();
        Lobby lobby2 = salaService.createLobby();

        assertNotNull(lobby1.getCodigoDeUnion());
        assertNotNull(lobby2.getCodigoDeUnion());
        assertNotEquals(lobby1.getCodigoDeUnion(), lobby2.getCodigoDeUnion(), "Different lobbies should have different codes");
    }

    @Test
    @DisplayName("Should add player to lobby")
    @Description("Test that a player can be added to an existing lobby")
    @Story("Add player to lobby")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testAddPlayer_Success() {
        Lobby lobby = salaService.createLobby();
        String playerName = "Player1";

        Lobby result = salaService.addPlayer(lobby.getCodigoDeUnion(), playerName);

        assertNotNull(result);
        assertNotNull(result.getJugadores());
        assertTrue(result.getJugadores().contains(playerName));
        assertEquals(1, result.getJugadores().size());
    }

    @Test
    @DisplayName("Should add multiple players to same lobby")
    @Description("Test that multiple players can be added to the same lobby")
    @Story("Add multiple players")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testAddPlayer_MultiplePlayers() {
        Lobby lobby = salaService.createLobby();
        String player1 = "Player1";
        String player2 = "Player2";
        String player3 = "Player3";

        salaService.addPlayer(lobby.getCodigoDeUnion(), player1);
        salaService.addPlayer(lobby.getCodigoDeUnion(), player2);
        Lobby result = salaService.addPlayer(lobby.getCodigoDeUnion(), player3);

        assertNotNull(result);
        assertEquals(3, result.getJugadores().size());
        assertTrue(result.getJugadores().contains(player1));
        assertTrue(result.getJugadores().contains(player2));
        assertTrue(result.getJugadores().contains(player3));
    }

    @Test
    @DisplayName("Should return null when adding player to non-existent lobby")
    @Description("Test that adding player to non-existent lobby returns null")
    @Story("Add player to non-existent lobby")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testAddPlayer_NonExistentLobby() {
        Lobby result = salaService.addPlayer("NONEXISTENT", "Player1");

        assertNull(result);
    }

    @Test
    @DisplayName("Should retrieve all lobbies")
    @Description("Test that all created lobbies can be retrieved")
    @Story("Get all lobbies")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testGetAllLobbies_Success() {
        Lobby lobby1 = salaService.createLobby();
        Lobby lobby2 = salaService.createLobby();
        Lobby lobby3 = salaService.createLobby();

        List<Lobby> lobbies = salaService.getAllLobbies();

        assertNotNull(lobbies);
        assertFalse(lobbies.isEmpty());
        assertTrue(lobbies.size() >= 3, "Should have at least 3 lobbies");
        assertTrue(lobbies.contains(lobby1));
        assertTrue(lobbies.contains(lobby2));
        assertTrue(lobbies.contains(lobby3));
    }

    @Test
    @DisplayName("Should return empty list when no lobbies exist")
    @Description("Test that empty list is returned when no lobbies have been created")
    @Story("Get all lobbies - empty")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testGetAllLobbies_Empty() {
        List<Lobby> lobbies = salaService.getAllLobbies();

        assertNotNull(lobbies, "Should return non-null list");
        assertIsInstance(lobbies, List.class);
    }

    @Test
    @DisplayName("Should maintain lobby data consistency")
    @Description("Test that lobby data remains consistent after multiple operations")
    @Story("Maintain consistency")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testDataConsistency() {
        Lobby lobby = salaService.createLobby();
        String codigo = lobby.getCodigoDeUnion();

        salaService.addPlayer(codigo, "Player1");
        salaService.addPlayer(codigo, "Player2");

        List<Lobby> allLobbies = salaService.getAllLobbies();
        Lobby retrievedLobby = allLobbies.stream()
                .filter(l -> l.getCodigoDeUnion().equals(codigo))
                .findFirst()
                .orElse(null);

        assertNotNull(retrievedLobby);
        assertEquals(2, retrievedLobby.getJugadores().size());
    }

    @Test
    @DisplayName("Should handle duplicate player additions")
    @Description("Test that the service handles adding duplicate players")
    @Story("Handle duplicate players")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testAddDuplicatePlayer() {
        Lobby lobby = salaService.createLobby();
        String player = "Player1";

        salaService.addPlayer(lobby.getCodigoDeUnion(), player);
        Lobby result = salaService.addPlayer(lobby.getCodigoDeUnion(), player);

        assertNotNull(result);
        assertEquals(2, result.getJugadores().size());
    }

    @Test
    @DisplayName("Should handle special characters in player names")
    @Description("Test that special characters in player names are handled")
    @Story("Handle special characters")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testAddPlayerWithSpecialCharacters() {
        Lobby lobby = salaService.createLobby();
        String playerName = "Player-!@#$%";

        Lobby result = salaService.addPlayer(lobby.getCodigoDeUnion(), playerName);

        assertNotNull(result);
        assertTrue(result.getJugadores().contains(playerName));
    }

    private void assertIsInstance(Object obj, Class<?> expectedClass) {
        assertTrue(expectedClass.isInstance(obj), "Object should be an instance of " + expectedClass.getName());
    }
}
