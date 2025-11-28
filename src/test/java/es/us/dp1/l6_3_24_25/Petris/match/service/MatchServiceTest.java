package es.us.dp1.l6_3_24_25.Petris.match.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

import es.us.dp1.l6_3_24_25.Petris.exceptions.AccessDeniedException;
import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import es.us.dp1.l6_3_24_25.Petris.match.dto.LobbyDTO;
import es.us.dp1.l6_3_24_25.Petris.match.dto.MatchDTO;
import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.model.TurnType;
import es.us.dp1.l6_3_24_25.Petris.match.repository.MatchRepository;
import es.us.dp1.l6_3_24_25.Petris.match.util.MatchDataUtil;
import es.us.dp1.l6_3_24_25.Petris.match.util.MatchMethodUtil;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.user.Authorities;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;

import static generatedAssertions.org.assertj.Assertions.assertThat;

@Epic("Game")
@Feature("Create, play and delete matches")
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

    @Test
    @DisplayName("Obtener todos las partidas")
    @Description("Método para obtener la lista de partidas")
    @Owner("dlozaco(FBN5868)")
    void testGetAllMatches() {
        List<Match> matches = matchService.getAllMatches();
        assertNotNull(matches, "List of matches must not be null");
    }

    @Test
    @DisplayName("Obtener partida por ID")
    @Description("Método para obtener partida por ID")
    @Owner("dlozaco(FBN5868)")
    void testGetMatchById() {
        int id = 1;
        when(matchRepository.findById(id)).thenReturn(Optional.of(new Match()));
        matchService.getMatchById(id);
        verify(matchRepository, times(1)).findById(id);
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
        String code = "TRJU";
        when(matchRepository.findByCode(code)).thenReturn(Optional.of(new Match()));
        matchService.getMatchByCode(code);
        verify(matchRepository, times(1)).findByCode(code);
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

        assertThatExceptionOfType(AccessDeniedException.class)
            .isThrownBy(() -> matchService.createMatch(creator, isPrivate))
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

    @Test
    @DisplayName("Should not join match if the player is already in a match")
    @Description("Test that if a player that is currently in a match attempts to join a match, AccessDeniedException is thrown and the player doesn't join")
    @Owner("josbardel1(WHS7046)")
    void testJoinMatchNegative() {
        verify(matchRepository, never()).save(any(Match.class));
        verifyNoMoreInteractions(matchRepository);

        String code = "AAAA";
        Match match = new Match();
        match.setCode(code);
        Player playerToJoin = new Player();
        playerToJoin.setIsCurrentlyInMatch(true);

        assertThatExceptionOfType(AccessDeniedException.class)
            .isThrownBy(() -> matchService.joinMatch(match, playerToJoin, code))
            .withMessage("Already in a match");
    }

    @Test
    @DisplayName("Should not join match if the match has already started")
    @Description("Test that if a player attempts to join a match that has started, AccessDeniedException is thrown and the player doesn't join")
    @Owner("josbardel1(WHS7046)")
    void testJoinMatchNegative2() {
        verify(matchRepository, never()).save(any(Match.class));
        verifyNoMoreInteractions(matchRepository);

        String code = "AAAA";
        LocalDateTime startedAt = LocalDateTime.now();
        Match match = new Match();
        match.setCode(code);
        match.setStartedAt(startedAt);
        Player playerToJoin = new Player();
        playerToJoin.setIsCurrentlyInMatch(false);

        assertThatExceptionOfType(AccessDeniedException.class)
            .isThrownBy(() -> matchService.joinMatch(match, playerToJoin, code))
            .withMessage("The match has already started");
    }

    @Test
    @DisplayName("Should not join match if the match has already ended")
    @Description("Test that if a player attempts to join a match that has ended, AccessDeniedException is thrown and the player doesn't join")
    @Owner("josbardel1(WHS7046)")
    void testJoinMatchNegative3() {
        verify(matchRepository, never()).save(any(Match.class));
        verifyNoMoreInteractions(matchRepository);

        String code = "AAAA";
        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime endedAt = LocalDateTime.now();
        Match match = new Match();
        match.setCode(code);
        match.setStartedAt(startedAt);
        match.setEndedAt(endedAt);
        Player playerToJoin = new Player();
        playerToJoin.setIsCurrentlyInMatch(false);

        assertThatExceptionOfType(AccessDeniedException.class)
            .isThrownBy(() -> matchService.joinMatch(match, playerToJoin, code))
            .withMessage("The match has already ended");
    }

    @Test
    @DisplayName("Should not join match if the code is incorrect")
    @Description("Test that if a player attempts to join a match with a code that is not the match code, AccessDeniedException is thrown and the player doesn't join")
    @Owner("josbardel1(WHS7046)")
    void testJoinMatchNegative4() {
        verify(matchRepository, never()).save(any(Match.class));
        verifyNoMoreInteractions(matchRepository);

        String code = "AAAA";
        String incorrectCode = "BBBB";
        Match match = new Match();
        match.setCode(code);
        Player playerToJoin = new Player();
        playerToJoin.setIsCurrentlyInMatch(false);

        assertThatExceptionOfType(AccessDeniedException.class)
            .isThrownBy(() -> matchService.joinMatch(match, playerToJoin, incorrectCode))
            .withMessage("Incorrect code for private match");
    }

    @Test
    @DisplayName("Should not join match if full")
    @Description("Test that if a player attempts to join a match with two players, AccessDeniedException is thrown and the player doesn't join")
    @Owner("josbardel1(WHS7046)")
    void testJoinMatchNegative5() {
        verify(matchRepository, never()).save(any(Match.class));
        verifyNoMoreInteractions(matchRepository);

        Player player1 = new Player();
        Player player2 = new Player();
        String code = "AAAA";
        Match match = new Match();
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setCode(code);
        Player playerToJoin = new Player();
        playerToJoin.setIsCurrentlyInMatch(false);

        assertThatExceptionOfType(AccessDeniedException.class)
            .isThrownBy(() -> matchService.joinMatch(match, playerToJoin, code))
            .withMessage("The match is already full");
    }

    @Test
    @DisplayName("Should join match if the code is correct")
    @Description("Test that if a player attempts to join a match with a code that is the match code, the player joins as player 2")
    @Owner("josbardel1(WHS7046)")
    void testJoinMatchPositive() {
        lenient().when(matchRepository.save(any(Match.class))).then(returnsFirstArg());

        String code = "AAAA";
        Match match = new Match();
        match.setCode(code);
        Player playerToJoin = new Player();
        playerToJoin.setIsCurrentlyInMatch(false);

        assertThat(matchService.joinMatch(match, playerToJoin, code)).hasPlayer2(playerToJoin);
    }

    @Test
    @DisplayName("Should join match if it is public (if it has no code)")
    @Description("Test that if a player attempts to join a match with null code, the player joins as player 2")
    @Owner("josbardel1(WHS7046)")
    void testJoinMatchPositive2() {
        lenient().when(matchRepository.save(any(Match.class))).then(returnsFirstArg());

        String code = null;
        Match match = new Match();
        Player playerToJoin = new Player();
        playerToJoin.setIsCurrentlyInMatch(false);

        assertThat(matchService.joinMatch(match, playerToJoin, code)).hasPlayer2(playerToJoin);
    }

    @Test
    @DisplayName("Should not leave match if the player is not in the match")
    @Description("Test that if a player attempts to leave a match where they are not a player, AccessDeniedException is thrown and no player leaves")
    @Owner("josbardel1(WHS7046)")
    void testLeaveMatchNegative() {
        verify(matchRepository, never()).save(any(Match.class));
        verifyNoMoreInteractions(matchRepository);

        Match match = new Match();
        Player playerToLeave = new Player();

        assertThatExceptionOfType(AccessDeniedException.class)
            .isThrownBy(() -> matchService.leaveMatch(match, playerToLeave))
            .withMessage("Not in this match");
    }

    @Test
    @DisplayName("Should not leave match if the match has already started")
    @Description("Test that if a player attempts to leave a match that has started, AccessDeniedException is thrown and the player doesn't leave")
    @Owner("josbardel1(WHS7046)")
    void testLeaveMatchNegative2() {
        verify(matchRepository, never()).save(any(Match.class));
        verifyNoMoreInteractions(matchRepository);

        LocalDateTime startedAt = LocalDateTime.now();
        Player playerToLeave = new Player();
        Match match = new Match();
        match.setPlayer2(playerToLeave);
        match.setStartedAt(startedAt);

        assertThatExceptionOfType(AccessDeniedException.class)
            .isThrownBy(() -> matchService.leaveMatch(match, playerToLeave))
            .withMessage("The match has already started. Concede instead");
    }

    @Test
    @DisplayName("Should not leave match if the match has already ended")
    @Description("Test that if a player attempts to leave a match that has ended, AccessDeniedException is thrown and the player doesn't leave")
    @Owner("josbardel1(WHS7046)")
    void testLeaveMatchNegative3() {
        verify(matchRepository, never()).save(any(Match.class));
        verifyNoMoreInteractions(matchRepository);

        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime endedAt = LocalDateTime.now();
        Player playerToLeave = new Player();
        Match match = new Match();
        match.setPlayer2(playerToLeave);
        match.setStartedAt(startedAt);
        match.setEndedAt(endedAt);

        assertThatExceptionOfType(AccessDeniedException.class)
            .isThrownBy(() -> matchService.leaveMatch(match, playerToLeave))
            .withMessage("The match has already ended");
    }

    @Test
    @DisplayName("Should leave not started match if creator and player 2 becomes creator")
    @Description("Test that if the creator attempts to leave a not started match, the player leaves, player2 is set to null and both player1 and creator are set to player2")
    @Owner("josbardel1(WHS7046)")
    void testLeaveMatchPositive() {
        lenient().when(matchRepository.save(any(Match.class))).then(returnsFirstArg());

        Player playerToLeave = new Player();
        playerToLeave.setId(1);
        Player player2 = new Player();
        Match match = new Match();
        match.setCreator(playerToLeave);
        match.setPlayer1(playerToLeave);
        match.setPlayer2(player2);

        Match result = matchService.leaveMatch(match, playerToLeave);
        assertThat(result).hasPlayer2(null);
        assertThat(result).hasCreator(player2);
        assertThat(result).hasPlayer1(player2);
    }

    @Test
    @DisplayName("Should leave not started match if player 2")
    @Description("Test that if a player2 attempts to leave a not started match, the player leaves and player2 is set to null")
    @Owner("josbardel1(WHS7046)")
    void testLeaveMatchPositive2() {
        lenient().when(matchRepository.save(any(Match.class))).then(returnsFirstArg());

        Player creatorAndPlayer1 = new Player();
        creatorAndPlayer1.setId(1);
        Player playerToLeave = new Player();
        Match match = new Match();
        match.setCreator(creatorAndPlayer1);
        match.setPlayer1(creatorAndPlayer1);
        match.setPlayer2(playerToLeave); 

        assertThat(matchService.leaveMatch(match, playerToLeave)).hasPlayer2(null);
    }

    @Test
    @DisplayName("Should not start match if there are less than 2 players")
    @Description("Test that if a player attempts to start a match where player2 is null, AccessDeniedException is thrown and the match doesn't start")
    @Owner("josbardel1(WHS7046)")
    void testStartMatchNegative() {
        verify(matchRepository, never()).save(any(Match.class));
        verifyNoMoreInteractions(matchRepository);

        Player playerThatStarts = new Player();
        Match match = new Match();
        match.setCreator(playerThatStarts);

        assertThatExceptionOfType(AccessDeniedException.class)
            .isThrownBy(() -> matchService.startMatch(match))
            .withMessage("Two players are required to start the match");
    }

    @Test
    @DisplayName("Should not start match if the match has already started")
    @Description("Test that if a player attempts to start a match that has started, AccessDeniedException is thrown")
    @Owner("josbardel1(WHS7046)")
    void testStartMatchNegative2() {
        verify(matchRepository, never()).save(any(Match.class));
        verifyNoMoreInteractions(matchRepository);

        Player player2 = new Player();
        Player playerThatStarts = new Player();
        playerThatStarts.setId(1);
        LocalDateTime startedAt = LocalDateTime.now();
        Match match = new Match();
        match.setCreator(playerThatStarts);
        match.setPlayer1(playerThatStarts);
        match.setPlayer2(player2);
        match.setStartedAt(startedAt);

        assertThatExceptionOfType(AccessDeniedException.class)
            .isThrownBy(() -> matchService.startMatch(match))
            .withMessage("Unsupported operation for started match");
    }

    @Test
    @DisplayName("Should not start match if the match has already ended")
    @Description("Test that if a player attempts to start a match that has ended, AccessDeniedException is thrown and the match doesn't start")
    @Owner("josbardel1(WHS7046)")
    void testStartMatchNegative3() {
        verify(matchRepository, never()).save(any(Match.class));
        verifyNoMoreInteractions(matchRepository);

        Player player2 = new Player();
        Player playerThatStarts = new Player();
        playerThatStarts.setId(1);
        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime endedAt = LocalDateTime.now();
        Match match = new Match();
        match.setCreator(playerThatStarts);
        match.setPlayer1(playerThatStarts);
        match.setPlayer2(player2);
        match.setStartedAt(startedAt);
        match.setEndedAt(endedAt);

        assertThatExceptionOfType(AccessDeniedException.class)
            .isThrownBy(() -> matchService.startMatch(match))
            .withMessage("The match has already ended");
    }

    @Test
    @DisplayName("Should start not ended and full match if creator requests it")
    @Description("Test that if a player attempts to start a match that has not ended and has two players, the match starts")
    @Owner("josbardel1(WHS7046)")
    void testStartMatchPositive() {
        lenient().when(matchRepository.save(any(Match.class))).then(returnsFirstArg());

        Player player2 = new Player();
        Player playerThatStarts = new Player();
        playerThatStarts.setId(1);
        Match match = new Match();
        match.setCreator(playerThatStarts);
        match.setPlayer1(playerThatStarts);
        match.setPlayer2(player2);

        Match result = matchService.startMatch(match);
        org.assertj.core.api.Assertions.assertThat(result.getStartedAt()).isNotNull();
        assertThat(result).hasTurn(0);
    }

    @Test
    @DisplayName("Should not advance turn if already past last turn")
    @Description("Test that if a turn advance is attempted when the match is past the last turn, IllegalArgumentException is thrown and the turn doesn't advance")
    @Owner("josbardel1(WHS7046)")
    void testNextTurnNegative() {
        verify(matchRepository, never()).save(any(Match.class));
        verifyNoMoreInteractions(matchRepository);

        int turn = MatchDataUtil.getTurnsNum();
        Match matchToAdvanceTurn = new Match();
        matchToAdvanceTurn.setTurn(turn);
        List<PetriDish> newBoardState = new ArrayList<>();

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> matchService.nextTurn(matchToAdvanceTurn, newBoardState))
            .withMessage("No remaining turns to process");
    }

    @Test
    @DisplayName("Should not advance turn if match already ended")
    @Description("Test that if a turn advance is attempted when the match has ended, AccessDeniedException is thrown and the turn doesn't advance")
    @Owner("josbardel1(WHS7046)")
    void testNextTurnNegative2() {
        verify(matchRepository, never()).save(any(Match.class));
        verifyNoMoreInteractions(matchRepository);

        int turn = 0;
        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime endedAt = LocalDateTime.now();
        Match matchToAdvanceTurn = new Match();
        matchToAdvanceTurn.setStartedAt(startedAt);
        matchToAdvanceTurn.setEndedAt(endedAt);
        matchToAdvanceTurn.setTurn(turn);
        List<PetriDish> newBoardState = new ArrayList<>();

        assertThatExceptionOfType(AccessDeniedException.class)
            .isThrownBy(() -> matchService.nextTurn(matchToAdvanceTurn, newBoardState))
            .withMessage("The match has already ended");
    }

    @Test
    @DisplayName("Should advance turn of not ended match if valid turn")
    @Description("Test that if a turn advance is attempted when the match is in a valid turn, the turn advances")
    @Owner("josbardel1(WHS7046)")
    void testNextTurnPositive() {
        lenient().when(matchRepository.save(any(Match.class))).then(returnsFirstArg());

        int turn = 0;
        TurnType turnType = TurnType.P1_PROPAGATION;
        Match matchToAdvanceTurn = new Match();
        matchToAdvanceTurn.setTurnType(turnType);
        matchToAdvanceTurn.setTurn(turn);
        List<PetriDish> newBoardState = new ArrayList<>();

        try (MockedStatic<MatchMethodUtil> utility = Mockito.mockStatic(MatchMethodUtil.class)) {
            utility.when(() -> MatchMethodUtil.propagation(any(Match.class), any(List.class), any(int.class)))
                .then(returnsFirstArg());
            Match result = matchService.nextTurn(matchToAdvanceTurn, newBoardState);
            assertThat(result).hasTurn(turn + 1);
            assertThat(result).hasTurnType(TurnType.P2_PROPAGATION);
        }
    }

    // TODO descomentar y aprovechar tests
    /*
    private ObjectProvider<MatchHelper> matchServiceHelperProvider;

    private WebSocketMatchService webSocketService;

    @BeforeEach
    void setupMocks() {
        matchRepository = mock(MatchRepository.class);
        messagingTemplate = mock(SimpMessagingTemplate.class);
        messagingTemplateProvider = mockMessagingTemplateProvider();

        when(messagingTemplateProvider.getIfAvailable()).thenReturn(messagingTemplate);
        matchServiceHelperProvider = mockMatchServiceHelperProvider();
        when(matchServiceHelperProvider.getIfAvailable()).thenReturn(null);
        behaviourService = new MatchService(matchRepository);
        webSocketService = new WebSocketMatchService(messagingTemplateProvider, matchRepository, behaviourService);
    }

    private ObjectProvider<SimpMessagingTemplate> mockMessagingTemplateProvider() {
        return (ObjectProvider<SimpMessagingTemplate>) mock(ObjectProvider.class);
    }

    private ObjectProvider<MatchHelper> mockMatchServiceHelperProvider() {
        return (ObjectProvider<MatchHelper>) mock(ObjectProvider.class);
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
        String code = behaviourService.generateLobbyCode(true);

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
        MatchHelper helper = new MatchHelper();
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
        MatchHelper helper = new MatchHelper();
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
        MatchHelper helper = new MatchHelper();
        List<PetriDish> board = createEmptyBoard();
        for (int i = 0; i < board.size(); i++) {
            setCounts(board, i, 5, 0);
        }

        assertFalse(helper.hasPossibleMoves(board, 1));
    }

    @Test
    void hasPossibleMoves_detectsSimpleTransferOpportunity() {
        MatchHelper helper = new MatchHelper();
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
        match.setTurnType(MatchHelper.getTurnType(TURN));
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
        ObjectProvider<MatchHelper> provider = (ObjectProvider<MatchHelper>) mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(new MatchHelper());
        return new MatchService(repository);
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
        match.setTurnType(MatchHelper.getTurnType(TURN));
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
    */
}
