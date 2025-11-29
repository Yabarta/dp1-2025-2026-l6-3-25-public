package es.us.dp1.l6_3_24_25.Petris.match.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.model.TurnType;
import es.us.dp1.l6_3_24_25.Petris.match.repository.MatchRepository;
import es.us.dp1.l6_3_24_25.Petris.match.util.MatchDataUtil;
import es.us.dp1.l6_3_24_25.Petris.match.util.MatchMethodUtil;
import es.us.dp1.l6_3_24_25.Petris.player.batchProcessing.MatchStatsBatchOrchestrator;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

import static generatedAssertions.org.assertj.Assertions.assertThat;

@Epic("Game module")
@Feature("Create, play and delete matches")
@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;
    @Mock
    private MatchStatsBatchOrchestrator matchStatsBatchOrchestrator;
    protected MatchService matchService;
    
    /*
    private WebSocketMatchService webSocketService;
    private SimpMessagingTemplate messagingTemplate;
    private ObjectProvider<SimpMessagingTemplate> messagingTemplateProvider;
    */

    @BeforeEach
    void setup() {
        matchService = new MatchService(matchRepository, matchStatsBatchOrchestrator);
        /*
        messagingTemplate = mock(SimpMessagingTemplate.class);
        webSocketService = new WebSocketMatchService(messagingTemplateProvider, matchRepository, matchService);
        */
    }

    @Test
    @DisplayName("Obtener todos las partidas")
    @Description("Método para obtener la lista de partidas")
    @Owner("dlozaco(FBN5868)")
    @Story("Retrieve matches")
    @Severity(SeverityLevel.NORMAL)
    void testGetAllMatches() {
        List<Match> matches = matchService.getAllMatches();
        assertNotNull(matches, "List of matches must not be null");
    }

    @Test
    @DisplayName("Obtener partida por ID")
    @Description("Método para obtener partida por ID")
    @Owner("dlozaco(FBN5868)")
    @Story("Retrieve matches")
    @Severity(SeverityLevel.NORMAL)
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
    @Story("Retrieve matches")
    @Severity(SeverityLevel.MINOR)
    void testGetMatchByWrongId() {
        Exception ex = assertThrows(ResourceNotFoundException.class, () -> matchService.getMatchById(100));
        assertEquals("Match not found with Id: '100'", ex.getMessage());
    }

    @Test
    @DisplayName("Obtener partida por code incorrecto")
    @Description("Método para obtener partida por código no existente")
    @Owner("dlozaco(FBN5868)")
    @Story("Retrieve matches")
    @Severity(SeverityLevel.MINOR)
    void testGetMatchByWrongCode() {
        Exception ex = assertThrows(ResourceNotFoundException.class, () -> matchService.getMatchByCode("GBNW"));
        assertEquals("Match not found with Code: 'GBNW'", ex.getMessage());
    }

    @Test
    @DisplayName("Obtener partida por code")
    @Description("Método para obtener partida por código no existente")
    @Owner("dlozaco(FBN5868)")
    @Story("Retrieve matches")
    @Severity(SeverityLevel.NORMAL)
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
    @Story("Retrieve matches")
    @Severity(SeverityLevel.NORMAL)
    void testGetCurrentMatches() {
        List<Match> currentMatches = matchService.getCurrentMatches();
        assertNotNull(currentMatches, "List of current matches can not be null");
    }

    @Test
    @DisplayName("Obtener todas las partidas sin empezar")
    @Description("Metodo para obtener todas las partidas sin empezar")
    @Owner("dlozaco(FBN5868)")
    @Story("Retrieve matches")
    @Severity(SeverityLevel.NORMAL)
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
    @DisplayName("Should not advance turn if match already ended")
    @Description("Test that if a turn advance is attempted when the match has ended, AccessDeniedException is thrown and the turn doesn't advance")
    @Owner("josbardel1(WHS7046)")
    void testNextTurnNegative3() {
        verify(matchRepository, never()).save(any(Match.class));
        verifyNoMoreInteractions(matchRepository);

        int turn = 0;
        Match matchToAdvanceTurn = new Match();
        matchToAdvanceTurn.setTurn(turn);
        List<PetriDish> newBoardState = new ArrayList<>();

        assertThatExceptionOfType(AccessDeniedException.class)
            .isThrownBy(() -> matchService.nextTurn(matchToAdvanceTurn, newBoardState))
            .withMessage("The match has not yet started");
    }

    @Test
    @DisplayName("Should advance turn of not ended match if valid turn")
    @Description("Test that if a turn advance is attempted when the match is in a valid turn, the turn advances")
    @Owner("josbardel1(WHS7046)")
    void testNextTurnPositive() {
        lenient().when(matchRepository.save(any(Match.class))).then(returnsFirstArg());

        int turn = 0;
        TurnType turnType = MatchDataUtil.getTurnType(turn);
        LocalDateTime startedAt = LocalDateTime.now();
        Match matchToAdvanceTurn = new Match();
        matchToAdvanceTurn.setStartedAt(startedAt);
        matchToAdvanceTurn.setTurnType(turnType);
        matchToAdvanceTurn.setTurn(turn);
        List<PetriDish> newBoardState = new ArrayList<>();

        try (MockedStatic<MatchMethodUtil> utility = Mockito.mockStatic(MatchMethodUtil.class)) {
            utility.when(() -> MatchMethodUtil.propagation(any(Match.class), anyList(), any(int.class)))
                .then(returnsFirstArg());
                
            Match result = matchService.nextTurn(matchToAdvanceTurn, newBoardState);
            assertThat(result).hasTurn(turn + 1);
            assertThat(result).hasTurnType(MatchDataUtil.getTurnType(turn + 1));
        }
    }

    @Test
    @DisplayName("Should end match if finishing turn")
    @Description("Test that if a turn advance is attempted when the match is in a finishing turn, the match ends")
    @Owner("josbardel1(WHS7046)")
    void testNextTurnPositive2() {
        lenient().when(matchRepository.save(any(Match.class))).then(returnsFirstArg());

        int turn = MatchDataUtil.getTurnsNum() - 1;
        TurnType turnType = MatchDataUtil.getTurnType(turn);
        LocalDateTime startedAt = LocalDateTime.now();
        Match matchToAdvanceTurn = new Match();
        matchToAdvanceTurn.setStartedAt(startedAt);
        matchToAdvanceTurn.setTurnType(turnType);
        matchToAdvanceTurn.setTurn(turn);
        List<PetriDish> newBoardState = new ArrayList<>();

        try (MockedStatic<MatchMethodUtil> utility = Mockito.mockStatic(MatchMethodUtil.class)) {
            utility.when(() -> MatchMethodUtil.contamination(any(Match.class))).thenAnswer(i -> {
                Match match = i.getArgument(0, Match.class);
                match.setWinner(1);
                return match;
            });
            
            Match result = matchService.nextTurn(matchToAdvanceTurn, newBoardState);
            assertThat(result).hasTurn(turn + 1);
            assertThat(result).hasTurnType(null);
            org.assertj.core.api.Assertions.assertThat(result.getEndedAt()).isNotNull();
        }
    }

    @Test
    @DisplayName("Should not check for errors if player who requested is not in the match")
    @Description("Test that if a player who is not in the match attempts to check for propagation errors, AccessDeniedException is thrown")
    @Owner("josbardel1(WHS7046)")
    void testCheckErrorsNegative() {
        try (MockedStatic<MatchMethodUtil> utility = Mockito.mockStatic(MatchMethodUtil.class)) {
            utility.verify(() -> MatchMethodUtil.getPropagationErrors(anyList(), anyList(), any(int.class)), never());

            Match matchToCheck = new Match();
            Player playerNotInTheMatch = new Player();
            List<PetriDish> newBoardState = new ArrayList<>();

            assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> matchService.checkErrors(matchToCheck, newBoardState, playerNotInTheMatch))
                .withMessage("Not in this match");
        }
    }

    @ParameterizedTest
    @DisplayName("Should not check for errors if not the turn of requesting player")
    @Description("Test that if a player attempts to check for propagation errors in a turn not their own, AccessDeniedException is thrown")
    @Owner("josbardel1(WHS7046)")
    @EnumSource(value = TurnType.class, names = {"P2_PROPAGATION", "BINARY_FISSION", "CONTAMINATION"})
    void testCheckErrorsNegative2WithEnumSource(TurnType turnType) {
        try (MockedStatic<MatchMethodUtil> utility = Mockito.mockStatic(MatchMethodUtil.class)) {
            utility.verify(() -> MatchMethodUtil.getPropagationErrors(anyList(), anyList(), any(int.class)), never());

            Player player1 = new Player();
            Match matchToCheck = new Match();
            matchToCheck.setPlayer1(player1);
            matchToCheck.setTurnType(turnType);
            List<PetriDish> newBoardState = new ArrayList<>();

            assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> matchService.checkErrors(matchToCheck, newBoardState, player1))
                .withMessage("Can only check for errors in your propagation turns");
        }
    }

    @Test
    @DisplayName("Should not check for errors if match already ended")
    @Description("Test that if a player attempts to check for propagation errors in an ended match, AccessDeniedException is thrown")
    @Owner("josbardel1(WHS7046)")
    void testCheckErrorsNegative3() {
        try (MockedStatic<MatchMethodUtil> utility = Mockito.mockStatic(MatchMethodUtil.class)) {
            utility.verify(() -> MatchMethodUtil.getPropagationErrors(anyList(), anyList(), any(int.class)), never());

            Player player1 = new Player();
            LocalDateTime endedAt = LocalDateTime.now();
            Match matchToCheck = new Match();
            matchToCheck.setPlayer1(player1);
            matchToCheck.setEndedAt(endedAt);
            matchToCheck.setTurnType(TurnType.P1_PROPAGATION);
            List<PetriDish> newBoardState = new ArrayList<>();

            assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> matchService.checkErrors(matchToCheck, newBoardState, player1))
                .withMessage("The match has already ended");
        }
    }

    @Test
    @DisplayName("Should check for errors of the current turn player in not ended match")
    @Description("Test that a player can check for propagation errors during their turn in a not ended match")
    @Owner("josbardel1(WHS7046)")
    void testCheckErrorsPositive() {
        try (MockedStatic<MatchMethodUtil> utility = Mockito.mockStatic(MatchMethodUtil.class)) {
            utility.when(() -> MatchMethodUtil.getPropagationErrors(anyList(), anyList(), any(int.class)))
                .thenReturn(new ArrayList<>());

            Player player1 = new Player();
            Match matchToCheck = new Match();
            matchToCheck.setPlayer1(player1);
            matchToCheck.setTurnType(TurnType.P1_PROPAGATION);
            List<PetriDish> newBoardState = new ArrayList<>();

            org.assertj.core.api.Assertions.assertThat(matchService.checkErrors(matchToCheck, newBoardState, player1)).isNotNull();
        }
    }

    @Test
    @DisplayName("Should not concede match if player who requested is not in the match")
    @Description("Test that if a player who is not in the match attempts to concede, AccessDeniedException is thrown and the match doesn't end")
    @Owner("josbardel1(WHS7046)")
    void testConcedeMatchNegative() {
        verify(matchRepository, never()).save(any(Match.class));
        verifyNoMoreInteractions(matchRepository);

        Match matchToConcede = new Match();
        Player playerNotInTheMatch = new Player();

        assertThatExceptionOfType(AccessDeniedException.class)
            .isThrownBy(() -> matchService.concedeMatch(matchToConcede, playerNotInTheMatch))
            .withMessage("Not in this match");
    }

    @ParameterizedTest
    @DisplayName("Should not concede match if not in propagation")
    @Description("Test that if a player attempts to concede outside of a propagation turn, AccessDeniedException is thrown and the match doesn't end")
    @Owner("josbardel1(WHS7046)")
    @EnumSource(value = TurnType.class, names = {"BINARY_FISSION", "CONTAMINATION"})
    void testConcedeMatchNegative2WithEnumSource(TurnType turnType) {
        verify(matchRepository, never()).save(any(Match.class));
        verifyNoMoreInteractions(matchRepository);

        Player player1 = new Player();
        Match matchToConcede = new Match();
        matchToConcede.setPlayer1(player1);
        matchToConcede.setTurnType(turnType);

        assertThatExceptionOfType(AccessDeniedException.class)
            .isThrownBy(() -> matchService.concedeMatch(matchToConcede, player1))
            .withMessage("Can only concede in propagation turns");
    }

    @Test
    @DisplayName("Should not concede if match already ended")
    @Description("Test that if a player attempts to concede an ended match, AccessDeniedException is thrown and the match doesn't end")
    @Owner("josbardel1(WHS7046)")
    void testConcedeMatchNegative3() {
        verify(matchRepository, never()).save(any(Match.class));
        verifyNoMoreInteractions(matchRepository);

        Player player1 = new Player();
        LocalDateTime endedAt = LocalDateTime.now();
        Match matchToConcede = new Match();
        matchToConcede.setPlayer1(player1);
        matchToConcede.setEndedAt(endedAt);
        matchToConcede.setTurnType(TurnType.P2_PROPAGATION);

        assertThatExceptionOfType(AccessDeniedException.class)
            .isThrownBy(() -> matchService.concedeMatch(matchToConcede, player1))
            .withMessage("The match has already ended");
    }

    @ParameterizedTest
    @DisplayName("Should not concede if match already ended")
    @Description("Test that if a player attempts to concede an ended match, AccessDeniedException is thrown and the match doesn't end")
    @Owner("josbardel1(WHS7046)")
    @EnumSource(value = TurnType.class, names = {"P1_PROPAGATION", "P2_PROPAGATION"})
    void testConcedeMatchPositiveWithEnumSource(TurnType turnType) {
        lenient().when(matchRepository.save(any(Match.class))).then(returnsFirstArg());

        Player player1 = new Player();
        Match matchToConcede = new Match();
        matchToConcede.setPlayer1(player1);
        matchToConcede.setTurnType(turnType);

        Match result = matchService.concedeMatch(matchToConcede, player1);
        org.assertj.core.api.Assertions.assertThat(result.getEndedAt()).isNotNull();
        assertThat(result).hasWinner(2);
    }

    // TODO descomentar y aprovechar tests
    /*
    @Test
    void broadcastLobbyAndMatchState_publishesLobbySnapshotListAndMatchSnapshot() {
        Player creator = buildPlayer(44, "creator", true);
        Player guest = buildPlayer(55, "guest", true);
        Match match = buildMatch(18, creator, guest);
        match.setBoardState(List.of(new PetriDish()));
        LobbyDTO expectedLobby = Objects.requireNonNull(LobbyDTO.toLobbyDTO(match));
        MatchDTO expectedMatch = Objects.requireNonNull(MatchDTO.toMatchDTO(match));
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
    @DisplayName("broadcastLobbyClosed notifica refresco y cierre")
    @Story("WebSocket broadcasting")
    @Description("broadcastLobbyClosed should notify lobby list refresh plus closure event.")
    @Severity(SeverityLevel.NORMAL)
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
    void toMatchDTO_includesBoardAndPlayers() {
        Integer TURN = 7;
        Player player1 = buildPlayer(41, "alpha", true);
        Player player2 = buildPlayer(42, "beta", true);
        Match match = buildMatch(21, player1, player2);
        match.setPlayer1Score(3);
        match.setPlayer2Score(5);
        match.setTurn(TURN);
        match.setTurnType(MatchDataUtil.getTurnType(TURN));
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

        MatchDTO dto = MatchDTO.toMatchDTO(match);

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
    @DisplayName("toLobbyDTO marca privacidad y jugadores")
    @Story("DTO mapping")
    @Description("toLobbyDTO should mark privacy via code and list all players.")
    @Severity(SeverityLevel.NORMAL)
    void toLobbyDTO_marksPrivacyAndPlayerList() {
        Player creator = buildPlayer(51, "creator", true);
        Player guest = buildPlayer(52, "guest", true);
        Match match = buildMatch(31, creator, guest);
        match.setCode("ZXCV");

        LobbyDTO dto = LobbyDTO.toLobbyDTO(match);

        assertEquals(31, dto.getId());
        assertTrue(dto.isPrivate(), "Code should mark the lobby as private");
        assertEquals("ZXCV", dto.getCode());
        assertEquals(creator.getId(), dto.getCreatorId());
        assertEquals(2, dto.getPlayers().size());
        assertEquals("creator_user", dto.getPlayers().get(0).getUsername());
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
        match.setTurnType(MatchDataUtil.getTurnType(TURN));
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
    */
}
