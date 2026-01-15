package es.us.dp1.l6_3_24_25.Petris.match.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.model.TurnType;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;

@Epic("Match module")
@Feature("Match DTO")
@SpringBootTest
@DisplayName("MatchDTO Tests")
class MatchDTOTest {

    private MatchDTO matchDTO;
    private Match match;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        matchDTO = new MatchDTO();

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
        match.setTurn(1);
        match.setTurnType(TurnType.P1_PROPAGATION);
        match.setPlayer1Score(10);
        match.setPlayer2Score(5);
        match.setWinner(1);
        match.setCreatedAt(LocalDateTime.now().minusHours(1));
        match.setStartedAt(LocalDateTime.now().minusMinutes(30));
        match.setEndedAt(LocalDateTime.now());

        List<PetriDish> board = new ArrayList<>();
        PetriDish dish = new PetriDish();
        dish.setPlayer1Bacteria(10);
        dish.setPlayer2Bacteria(5);
        board.add(dish);
        match.setBoardState(board);
    }

    @Test
    @DisplayName("Should convert Match to MatchDTO successfully")
    @Description("Test that a Match entity is correctly converted to MatchDTO")
    @Story("Convert Match to DTO")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToMatchDTO_Success() {
        MatchDTO dto = MatchDTO.toMatchDTO(match);

        assertNotNull(dto);
        assertEquals(match.getId(), dto.getId());
        assertEquals(match.getCode(), dto.getCode());
        assertEquals(match.getTurn(), dto.getTurn());
        assertEquals(match.getTurnType(), dto.getTurnType());
        assertEquals(match.getPlayer1Score(), dto.getPlayer1Score());
        assertEquals(match.getPlayer2Score(), dto.getPlayer2Score());
        assertEquals(match.getWinner(), dto.getWinner());
        assertEquals(match.getCreatedAt(), dto.getCreatedAt());
        assertEquals(match.getStartedAt(), dto.getStartedAt());
        assertEquals(match.getEndedAt(), dto.getEndedAt());
    }

    @Test
    @DisplayName("Should convert Match players to PlayerSummaryDTO")
    @Description("Test that Match players are correctly converted to PlayerSummaryDTO")
    @Story("Convert players to summary DTO")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToMatchDTO_PlayersConversion() {
        MatchDTO dto = MatchDTO.toMatchDTO(match);

        assertNotNull(dto.getPlayer1());
        assertNotNull(dto.getPlayer2());
        assertEquals("Player1", dto.getPlayer1().getNickname());
        assertEquals("Player2", dto.getPlayer2().getNickname());
    }

    @Test
    @DisplayName("Should convert board state to PetriDishDTO list")
    @Description("Test that the board state is correctly converted to PetriDishDTO list")
    @Story("Convert board to DTO")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToMatchDTO_BoardConversion() {
        MatchDTO dto = MatchDTO.toMatchDTO(match);

        assertNotNull(dto.getBoard());
        assertEquals(1, dto.getBoard().size());
        assertEquals(0, dto.getBoard().get(0).getIndex());
        assertEquals(10, dto.getBoard().get(0).getPlayer1Bacteria());
        assertEquals(5, dto.getBoard().get(0).getPlayer2Bacteria());
    }

    @Test
    @DisplayName("Should handle null board state")
    @Description("Test that null board state is handled correctly")
    @Story("Handle null board")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToMatchDTO_NullBoardState() {
        match.setBoardState(null);
        MatchDTO dto = MatchDTO.toMatchDTO(match);

        assertNotNull(dto);
        assertNotNull(dto.getBoard());
        assertEquals(0, dto.getBoard().size());
    }

    @Test
    @DisplayName("Should handle empty board state")
    @Description("Test that empty board state is handled correctly")
    @Story("Handle empty board")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToMatchDTO_EmptyBoardState() {
        match.setBoardState(new ArrayList<>());
        MatchDTO dto = MatchDTO.toMatchDTO(match);

        assertNotNull(dto);
        assertNotNull(dto.getBoard());
        assertEquals(0, dto.getBoard().size());
    }

    @Test
    @DisplayName("Should handle multiple board dishes")
    @Description("Test that multiple board dishes are correctly converted with proper indices")
    @Story("Convert multiple board dishes")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToMatchDTO_MultipleBoardDishes() {
        List<PetriDish> board = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            PetriDish dish = new PetriDish();
            dish.setPlayer1Bacteria(10 + i);
            dish.setPlayer2Bacteria(5 + i);
            board.add(dish);
        }
        match.setBoardState(board);

        MatchDTO dto = MatchDTO.toMatchDTO(match);

        assertNotNull(dto.getBoard());
        assertEquals(3, dto.getBoard().size());
        for (int i = 0; i < 3; i++) {
            assertEquals(i, dto.getBoard().get(i).getIndex());
            assertEquals(10 + i, dto.getBoard().get(i).getPlayer1Bacteria());
            assertEquals(5 + i, dto.getBoard().get(i).getPlayer2Bacteria());
        }
    }

    @Test
    @DisplayName("Should set and get all DTO fields")
    @Description("Test that all DTO fields can be set and retrieved")
    @Story("DTO field operations")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testMatchDTOGettersSetters() {
        matchDTO.setId(5);
        matchDTO.setCode("XXXX");
        matchDTO.setTurn(10);
        matchDTO.setTurnType(TurnType.BINARY_FISSION);
        matchDTO.setPlayer1Score(50);
        matchDTO.setPlayer2Score(40);
        matchDTO.setWinner(2);

        assertEquals(5, matchDTO.getId());
        assertEquals("XXXX", matchDTO.getCode());
        assertEquals(10, matchDTO.getTurn());
        assertEquals(TurnType.BINARY_FISSION, matchDTO.getTurnType());
        assertEquals(50, matchDTO.getPlayer1Score());
        assertEquals(40, matchDTO.getPlayer2Score());
        assertEquals(2, matchDTO.getWinner());
    }

    @Test
    @DisplayName("Should handle null players in Match")
    @Description("Test that null players are handled when converting to MatchDTO")
    @Story("Handle null players")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToMatchDTO_NullPlayers() {
        match.setPlayer1(null);
        match.setPlayer2(null);
        MatchDTO dto = MatchDTO.toMatchDTO(match);

        assertNotNull(dto);
        assertNull(dto.getPlayer1());
        assertNull(dto.getPlayer2());
    }

    @Test
    @DisplayName("Should convert one null player")
    @Description("Test that one null player is handled correctly")
    @Story("Handle one null player")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testToMatchDTO_OneNullPlayer() {
        match.setPlayer2(null);
        MatchDTO dto = MatchDTO.toMatchDTO(match);

        assertNotNull(dto);
        assertNotNull(dto.getPlayer1());
        assertNull(dto.getPlayer2());
    }
}
