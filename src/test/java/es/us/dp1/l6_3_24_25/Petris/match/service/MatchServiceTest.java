package es.us.dp1.l6_3_24_25.Petris.match.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

import es.us.dp1.l6_3_24_25.Petris.exceptions.AccessDeniedException;
import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import es.us.dp1.l6_3_24_25.Petris.match.dto.LobbyDTO;
import es.us.dp1.l6_3_24_25.Petris.match.dto.MatchDTO;
import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.model.TurnType;
import es.us.dp1.l6_3_24_25.Petris.match.repository.MatchRepository;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.user.Authorities;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;

import static generatedAssertions.org.assertj.Assertions.assertThat;

@Epic("Game")
@Feature("Match management")
@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;
    protected MatchService matchService;

    @BeforeEach
    void setup() {
        matchService = new MatchService(matchRepository);
    }

    private SimpMessagingTemplate messagingTemplate;
    private ObjectProvider<SimpMessagingTemplate> messagingTemplateProvider;
    private MatchService behaviourService;
    private MatchServiceHelper matchServiceHelper;

    @Test
    @DisplayName("Obtener todos las partidas")
    @Description("Método para obtener la lista de partidas")
    @Owner("dlozaco(FBN5868)")
    void testGetAllMatches() {
        List<Match> matches = matchService.getAllMatches();
        assertNotNull(matches, "List of matches must not be null");
        System.out.println(matches.getFirst().getCode());
    }

    @Test
    @DisplayName("Obtener partida por ID")
    @Description("Método para obtener partida por ID")
    @Owner("dlozaco(FBN5868)")
    void testGetMatchById() {
        Match match = matchService.getMatchById(1);
        assertEquals(1, match.getId());
        System.out.println(match.getId());
    }

    @Test
    @DisplayName("Obtener partida por ID incorrecto")
    @Description("Método para obtener partida por ID incorrecto")
    @Owner("dlozaco(FBN5868)")
    void testGetMatchByWrongId() {
        Exception ex = assertThrows(ResourceNotFoundException.class, () -> matchService.getMatchById(100));
        assertEquals("Match not found with Id: '100'", ex.getMessage());
    }

    @Test
    @DisplayName("Obtener partida por code incorrecto")
    @Description("Método para obtener partida por código no existente")
    @Owner("dlozaco(FBN5868)")
    void testGetMatchByWrongCode() {
        Exception ex = assertThrows(ResourceNotFoundException.class, () -> matchService.getMatchByCode("GBNW"));
        assertEquals("Match not found with Code: 'GBNW'", ex.getMessage());
    }

    @Test
    @DisplayName("Obtener partida por code")
    @Description("Método para obtener partida por código no existente")
    @Owner("dlozaco(FBN5868)")
    void testGetMatchByCode() {
        Match match = matchService.getMatchByCode("TRJU");
        assertEquals("TRJU", match.getCode());
        System.out.println(match.getCode());
    }

    @Test
    @DisplayName("Obtener todos las partidas en curso")
    @Description("Método para obtener la lista de partidas en curso")
    @Owner("dlozaco(FBN5868)")
    void testGetCurrentMatches() {
        List<Match> currentMatches = matchService.getCurrentMatches();
        assertNotNull(currentMatches, "List of current matches can not be null");
    }

    @Test
    @DisplayName("Obtener todas las partidas sin empezar")
    @Description("Metodo para obtener todas las partidas sin empezar")
    @Owner("dlozaco(FBN5868)")
    void testGetNotStartedMatches() {
        List<Match> notStartedMatches = matchService.getNotStartedMatches();
        assertNotNull(notStartedMatches, "List of not started matches can not be null");
    }

    @Test
    @DisplayName("Should not create match with creator already in a match")
    @Description("Test that if a player that is currently in a match attempts to create a match, AccessDeniedException is thrown and the match is not created")
    @Owner("josbardel1(WHS7046)")
    void testCreateMatchNegative() {
        verify(matchRepository, never()).save(any(Match.class));
        verifyNoMoreInteractions(matchRepository);

        Boolean isPrivate = false;
        Player creator = new Player();
        creator.setIsCurrentlyInMatch(true);

        assertThatExceptionOfType(AccessDeniedException.class).isThrownBy(() -> matchService.createMatch(creator, isPrivate))
            .withMessage("Already in a match");
    }

    @Test
    @DisplayName("Should create match with creator not already in a match")
    @Description("Test that if a player that is not currently in a match attempts to create a match, the match is created with that player as creator")
    @Owner("josbardel1(WHS7046)")
    void testCreateMatchPositive() {
        lenient().when(matchRepository.save(any(Match.class))).then(returnsFirstArg());

        Boolean isPrivate = false;
        Player creator = new Player();
        creator.setIsCurrentlyInMatch(false);

        assertThat(matchService.createMatch(creator, isPrivate)).hasCreator(creator);
    }

    private ObjectProvider<MatchServiceHelper> matchServiceHelperProvider;

    private WebSocketMatchService webSocketService;

    @BeforeEach
    void setupMocks() {
        matchRepository = mock(MatchRepository.class);
        messagingTemplate = mock(SimpMessagingTemplate.class);
        messagingTemplateProvider = mockMessagingTemplateProvider();
        matchServiceHelper = mock(MatchServiceHelper.class);
        when(matchServiceHelper.getTurnTypeList()).thenReturn(new MatchServiceHelper().getTurnTypeList());

        when(messagingTemplateProvider.getIfAvailable()).thenReturn(messagingTemplate);
        matchServiceHelperProvider = mockMatchServiceHelperProvider();
        when(matchServiceHelperProvider.getIfAvailable()).thenReturn(null);
        behaviourService = new MatchService(matchRepository);
        webSocketService = new WebSocketMatchService(messagingTemplateProvider, matchRepository, behaviourService);
    }

    @SuppressWarnings("unchecked")
    private ObjectProvider<SimpMessagingTemplate> mockMessagingTemplateProvider() {
        return (ObjectProvider<SimpMessagingTemplate>) mock(ObjectProvider.class);
    }

    @SuppressWarnings("unchecked")
    private ObjectProvider<MatchServiceHelper> mockMatchServiceHelperProvider() {
        return (ObjectProvider<MatchServiceHelper>) mock(ObjectProvider.class);
    }

    @Test
    void startMatch_setsTimestampAndPersistsWithoutMessaging() {
        Match match = buildMatch(5, buildPlayer(10, "creator", true), buildPlayer(20, "guest", true));
        stubSaveReturnsArgument();

        Match result = behaviourService.startMatch(match);

        assertNotNull(result.getStartedAt(), "startMatch should stamp the start time");
        verify(matchRepository).save(match);
        verifyNoInteractions(messagingTemplate);
    }

    @Test
    void leaveMatch_promotesRemainingPlayerWhenCreatorLeaves() {
        Player creator = buildPlayer(11, "player1", true);
        Player second = buildPlayer(22, "player2", true);
        Match match = buildMatch(9, creator, second);
        when(matchRepository.findByStartedAtNull()).thenReturn(List.of(match));
        stubSaveReturnsArgument();

        Optional<Match> optional = behaviourService.leaveMatch(match, creator);

        assertTrue(optional.isPresent(), "Lobby should remain open with the remaining player");
        Match updated = optional.get();
        assertSame(second, updated.getPlayer1(), "Second player should be promoted to player1");
        assertNull(updated.getPlayer2(), "Lobby should have a single participant after promotion");
        assertSame(second, updated.getCreator(), "Creator should transfer to the remaining player");
        verify(matchRepository).save(match);
        verifyNoInteractions(messagingTemplate);
    }

    @Test
    void leaveMatch_deletesLobbyWhenEmpty() {
        Player solo = buildPlayer(33, "solo", true);
        Match match = buildMatch(15, solo, null);
        when(matchRepository.findByStartedAtNull()).thenReturn(List.of());

        Optional<Match> optional = behaviourService.leaveMatch(match, solo);

        assertTrue(optional.isEmpty(), "Lobby should be deleted when the last player leaves");
        verify(matchRepository).delete(match);
        verifyNoInteractions(messagingTemplate);
    }

    @Test
    void broadcastLobbyAndMatchState_publishesLobbySnapshotListAndMatchSnapshot() {
        Player creator = buildPlayer(44, "creator", true);
        Player guest = buildPlayer(55, "guest", true);
        Match match = buildMatch(18, creator, guest);
        match.setBoardState(List.of(new PetriDish()));
        LobbyDTO expectedLobby = ensureNonNull(behaviourService.toLobbyDTO(match));
        MatchDTO expectedMatch = ensureNonNull(behaviourService.toMatchDTO(match));
        List<LobbyDTO> expectedLobbyList = List.of(expectedLobby);
        when(matchRepository.findByStartedAtNull()).thenReturn(List.of(match));

        webSocketService.broadcastLobbyAndMatchState(match);

        List<org.mockito.invocation.Invocation> sends = mockingDetails(messagingTemplate)
                .getInvocations()
                .stream()
                .filter(invocation -> invocation.getMethod().getName().equals("convertAndSend"))
                .toList();

        assertEquals(3, sends.size(), "broadcastLobbyAndMatchState should publish three messages");
        List<String> destinations = sends.stream()
                .map(invocation -> invocation.getArgument(0, String.class))
                .toList();
        assertEquals(List.of("/topic/lobby/18", "/topic/lobbies", "/topic/match/18"), destinations);

        LobbyDTO lobbyPayload = assertInstanceOf(LobbyDTO.class, sends.get(0).getArgument(1));
        assertEquals(expectedLobby, lobbyPayload);

        @SuppressWarnings("unchecked")
        List<LobbyDTO> lobbyListPayload = assertInstanceOf(List.class, sends.get(1).getArgument(1));
        assertEquals(expectedLobbyList, lobbyListPayload);

        MatchDTO matchPayload = assertInstanceOf(MatchDTO.class, sends.get(2).getArgument(1));
        assertEquals(expectedMatch, matchPayload);
    }

    @Test
    void broadcastLobbyClosed_publishesListRefreshAndClosure() {
        when(matchRepository.findByStartedAtNull()).thenReturn(List.of());

        webSocketService.broadcastLobbyClosed(27);

        List<org.mockito.invocation.Invocation> sends = mockingDetails(messagingTemplate)
                .getInvocations()
                .stream()
                .filter(invocation -> invocation.getMethod().getName().equals("convertAndSend"))
                .toList();

        assertEquals(2, sends.size(), "broadcastLobbyClosed should publish list refresh and closure");
        List<String> destinations = sends.stream()
                .map(invocation -> invocation.getArgument(0, String.class))
                .toList();
        assertEquals(List.of("/topic/lobbies", "/topic/lobby/27"), destinations);

        @SuppressWarnings("unchecked")
        List<LobbyDTO> lobbyListPayload = assertInstanceOf(List.class, sends.get(0).getArgument(1));
        assertTrue(lobbyListPayload.isEmpty(), "Lobby list payload should be empty");

        String closurePayload = assertInstanceOf(String.class, sends.get(1).getArgument(1));
        assertEquals("LOBBY_CLOSED", closurePayload);
    }

    @Test
    void generateLobbyCode_createsFourUppercaseCharacters() {
        String code = behaviourService.generateLobbyCode();

        assertEquals(4, code.length(), "Codes must contain four characters");
        assertTrue(code.chars().allMatch(ch -> ch >= 'A' && ch <= 'Z'), "Codes must be uppercase letters");
    }

    @Test
    void getPropagationErrors_validSingleMove_returnsEmptyList() {
        MatchRepository repository = mock(MatchRepository.class);
        MatchService service = buildServiceWithRealHelper(repository);
        List<PetriDish> currentBoard = createEmptyBoard();
        setCounts(currentBoard, 2, 3, 0);

        List<PetriDish> proposedBoard = copyBoard(currentBoard);
        setCounts(proposedBoard, 2, 2, 0);
        setCounts(proposedBoard, 3, 1, 0);

        List<String> errors = service.getPropagationErrors(currentBoard, proposedBoard, 1);

        assertTrue(errors.isEmpty(), "Expected a valid move to produce no validation errors");
    }

    @Test
    void getPropagationErrors_rejectsNonAdjacentMove() {
        MatchRepository repository = mock(MatchRepository.class);
        MatchService service = buildServiceWithRealHelper(repository);
        List<PetriDish> currentBoard = createEmptyBoard();
        setCounts(currentBoard, 2, 3, 0);

        List<PetriDish> proposedBoard = copyBoard(currentBoard);
        setCounts(proposedBoard, 2, 2, 0);
        setCounts(proposedBoard, 4, 1, 0);

        List<String> errors = service.getPropagationErrors(currentBoard, proposedBoard, 1);

        assertTrue(errors.stream().anyMatch(msg -> msg.contains("adyacent")),
            "Expected a non-adjacent move to be rejected");
    }

    @Test
    @SuppressWarnings("null")
    void nextTurn_advancesPropagationTurnAndPersistsBoard() {
        MatchRepository repository = mock(MatchRepository.class);
        MatchService service = buildServiceWithRealHelper(repository);

        Match match = buildLogicMatch(0, TurnType.P1_PROPAGATION);
        when(repository.save(match)).thenAnswer(invocation -> ensureNonNull(invocation.getArgument(0, Match.class)));
        List<PetriDish> currentBoard = createEmptyBoard();
        setCounts(currentBoard, 2, 3, 0);
        match.setBoardState(currentBoard);

        List<PetriDish> proposedBoard = copyBoard(currentBoard);
        setCounts(proposedBoard, 2, 2, 0);
        setCounts(proposedBoard, 3, 1, 0);

        Match updated = service.nextTurn(match, Optional.of(proposedBoard));

        assertEquals(proposedBoard.get(2).getPlayer1Bacteria(), updated.getBoardState().get(2).getPlayer1Bacteria());
        assertEquals(proposedBoard.get(3).getPlayer1Bacteria(), updated.getBoardState().get(3).getPlayer1Bacteria());
        assertEquals(Integer.valueOf(1), updated.getTurn());
        assertEquals(TurnType.P2_PROPAGATION, updated.getTurnType());
        verify(repository).save(match);
    }

    @Test
    @SuppressWarnings("null")
    void nextTurn_runsBinaryFissionWhenNoBoardProvided() {
        MatchRepository repository = mock(MatchRepository.class);
        MatchService service = buildServiceWithRealHelper(repository);

        Match match = buildLogicMatch(2, TurnType.BINARY_FISSION);
        when(repository.save(match)).thenAnswer(invocation -> ensureNonNull(invocation.getArgument(0, Match.class)));
        List<PetriDish> board = createEmptyBoard();
        setCounts(board, 0, 1, 0);
        match.setBoardState(board);

        Match updated = service.nextTurn(match, Optional.empty());

        assertEquals(2, updated.getBoardState().get(0).getPlayer1Bacteria(),
            "Binary fission should duplicate lone bacteria");
        assertEquals(Integer.valueOf(3), updated.getTurn());
        assertEquals(TurnType.P2_PROPAGATION, updated.getTurnType());
    }

    @Test
    void nextTurn_requiresBoardStateForPropagationTurns() {
        MatchRepository repository = mock(MatchRepository.class);
        MatchService service = buildServiceWithRealHelper(repository);
        Match match = buildLogicMatch(0, TurnType.P1_PROPAGATION);
        match.setBoardState(createEmptyBoard());

        assertThrows(IllegalArgumentException.class, () -> service.nextTurn(match, Optional.empty()));
        verifyNoInteractions(repository);
    }

    @Test
    void binaryFission_onlyGrowsIsolatedBacteria() {
        MatchServiceHelper helper = new MatchServiceHelper();
        Match match = new Match();
        List<PetriDish> board = createEmptyBoard();
        setCounts(board, 0, 1, 0);
        setCounts(board, 1, 0, 2);
        match.setBoardState(board);

        helper.binaryFission(match);

        assertEquals(2, match.getBoardState().get(0).getPlayer1Bacteria());
        assertEquals(3, match.getBoardState().get(1).getPlayer2Bacteria());
    }

    @Test
    void contamination_scoresHigherCountsAndClampsAtMax() {
        MatchServiceHelper helper = new MatchServiceHelper();
        Match match = new Match();
        List<PetriDish> board = createEmptyBoard();
        setCounts(board, 0, 2, 0);
        setCounts(board, 1, 0, 3);
        setCounts(board, 2, 4, 1);
        match.setBoardState(board);
        match.setPlayer1Score(8);
        match.setPlayer2Score(1);

        helper.contamination(match);

        assertEquals(9, match.getPlayer1Score());
        assertEquals(2, match.getPlayer2Score());
    }

    @Test
    void hasPossibleMoves_returnsFalseWhenOnlySarcinas() {
        MatchServiceHelper helper = new MatchServiceHelper();
        List<PetriDish> board = createEmptyBoard();
        for (int i = 0; i < board.size(); i++) {
            setCounts(board, i, 5, 0);
        }

        assertFalse(helper.hasPossibleMoves(board, 1));
    }

    @Test
    void hasPossibleMoves_detectsSimpleTransferOpportunity() {
        MatchServiceHelper helper = new MatchServiceHelper();
        List<PetriDish> board = createEmptyBoard();
        setCounts(board, 2, 3, 0);
        setCounts(board, 3, 0, 0);

        assertTrue(helper.hasPossibleMoves(board, 1));
    }

    @Test
    void toMatchDTO_includesBoardAndPlayers() {
        Integer TURN = 7;
        Player player1 = buildPlayer(41, "alpha", true);
        Player player2 = buildPlayer(42, "beta", true);
        Match match = buildMatch(21, player1, player2);
        match.setPlayer1Score(3);
        match.setPlayer2Score(5);
        match.setTurn(TURN);
        match.setTurnType(matchServiceHelper.getTurnTypeList().get(TURN));
        List<PetriDish> board = new ArrayList<>();
        PetriDish dish0 = new PetriDish();
        dish0.setPlayer1Bacteria(2);
        dish0.setPlayer2Bacteria(1);
        PetriDish dish1 = new PetriDish();
        dish1.setPlayer1Bacteria(0);
        dish1.setPlayer2Bacteria(4);
        board.add(dish0);
        board.add(dish1);
        match.setBoardState(board);

        MatchDTO dto = behaviourService.toMatchDTO(match);

        assertEquals(2, dto.getBoard().size(), "Board size should be preserved");
        assertEquals(0, dto.getBoard().get(0).getIndex());
        assertEquals(1, dto.getBoard().get(1).getIndex());
        assertEquals(2, dto.getBoard().get(0).getPlayer1Bacteria());
        assertEquals(4, dto.getBoard().get(1).getPlayer2Bacteria());
        assertEquals("alpha_user", dto.getPlayer1().getUsername());
        assertEquals("beta_user", dto.getPlayer2().getUsername());
        assertEquals(7, dto.getTurn());
        assertEquals(3, dto.getPlayer1Score());
        assertEquals(5, dto.getPlayer2Score());
    }

    @Test
    void toLobbyDTO_marksPrivacyAndPlayerList() {
        Player creator = buildPlayer(51, "creator", true);
        Player guest = buildPlayer(52, "guest", true);
        Match match = buildMatch(31, creator, guest);
        match.setCode("ZXCV");

        LobbyDTO dto = behaviourService.toLobbyDTO(match);

        assertEquals(31, dto.getId());
        assertTrue(dto.isPrivate(), "Code should mark the lobby as private");
        assertEquals("ZXCV", dto.getCode());
        assertEquals(creator.getId(), dto.getCreatorId());
        assertEquals(2, dto.getPlayers().size());
        assertEquals("creator_user", dto.getPlayers().get(0).getUsername());
    }

    private MatchService buildServiceWithRealHelper(MatchRepository repository) {
        @SuppressWarnings("unchecked")
        ObjectProvider<MatchServiceHelper> provider = (ObjectProvider<MatchServiceHelper>) mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(new MatchServiceHelper());
        return new MatchService(repository, provider);
    }

    private @NonNull Match buildMatch(int id, Player player1, Player player2) {
        Integer TURN = 0;
        Match match = new Match();
        match.setId(id);
        match.setCreator(player1);
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setBoardState(new ArrayList<>());
        match.setPlayer1Score(0);
        match.setPlayer2Score(0);
        match.setTurn(TURN);
        match.setTurnType(matchServiceHelper.getTurnTypeList().get(TURN));
        return match;
    }

    private @NonNull Player buildPlayer(int id, String prefix, boolean inMatch) {
        Authorities authority = new Authorities();
        authority.setAuthority("PLAYER");

        User user = new User();
        user.setId(id);
        user.setUsername(prefix + "_user");
        user.setAuthority(authority);

        Player player = new Player();
        player.setId(id);
        player.setNickname(prefix + "_nick");
        player.setEmail(prefix + "@example.com");
        player.setProfilePicture(prefix + ".png");
        player.setIsCurrentlyInMatch(inMatch);
        player.setUser(user);
        return player;
    }

    private Match buildLogicMatch(int turnIndex, TurnType turnType) {
        Match match = new Match();
        match.setTurn(turnIndex);
        match.setTurnType(turnType);
        match.setPlayer1Score(0);
        match.setPlayer2Score(0);
        match.setBoardState(new ArrayList<>());
        return match;
    }

    private List<PetriDish> createEmptyBoard() {
        List<PetriDish> board = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            board.add(new PetriDish());
        }
        return board;
    }

    private void setCounts(List<PetriDish> board, int index, int p1, int p2) {
        PetriDish dish = board.get(index);
        dish.setPlayer1Bacteria(p1);
        dish.setPlayer2Bacteria(p2);
    }

    private List<PetriDish> copyBoard(List<PetriDish> original) {
        List<PetriDish> copy = new ArrayList<>();
        for (PetriDish source : original) {
            PetriDish dish = new PetriDish();
            dish.setPlayer1Bacteria(source.getPlayer1Bacteria());
            dish.setPlayer2Bacteria(source.getPlayer2Bacteria());
            copy.add(dish);
        }
        return copy;
    }

    @SuppressWarnings("null")
    private void stubSaveReturnsArgument() {
        when(matchRepository.save(any(Match.class)))
                .thenAnswer(invocation -> ensureNonNull(invocation.getArgument(0, Match.class)));
    }

    private <T> T ensureNonNull(T value) {
        return Objects.requireNonNull(value);
    }
}
