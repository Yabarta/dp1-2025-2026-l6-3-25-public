package es.us.dp1.l6_3_24_25.Petris.match.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import es.us.dp1.l6_3_24_25.Petris.exceptions.AccessDeniedException;
import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.service.MatchService;
import es.us.dp1.l6_3_24_25.Petris.match.service.WebSocketMatchService;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import es.us.dp1.l6_3_24_25.Petris.user.Authorities;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import es.us.dp1.l6_3_24_25.Petris.user.UserService;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

/* TODO descomentar y aprovechar tests
@ExtendWith(MockitoExtension.class)
@Epic("Match module")
@Feature("REST controller")
@Owner("match-rest-team")
class MatchControllerTest {

    @Mock
    private MatchService matchService;

    @Mock
    private WebSocketMatchService webSocketMatchService;

    @Mock
    private UserService userService;

    @Mock
    private PlayerService playerService;

    private MatchController matchController;

    @BeforeEach
    void setup() {
        matchController = new MatchController(matchService, webSocketMatchService, userService, playerService);
    }

    @AfterEach
    void tearDownRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Crear lobby privado asigna código y marca al creador")
    @Story("Create lobby")
    @Description("When a player creates a private lobby, a code is generated, the player state changes and notifications are broadcast.")
    @Severity(SeverityLevel.CRITICAL)
    void createMatch_generatesCodeForPrivateLobbyAndMarksPlayerBusy() throws Exception {
        Player creator = buildPlayer(1, "creator", false);
        Match persisted = new Match();
        persisted.setId(77);
        persisted.setCode("ABCD");
        persisted.setPlayer1(creator);
        persisted.setCreator(creator);
        when(userService.findCurrentUser()).thenReturn(creator.getUser());
        when(playerService.getPlayerByUser(creator.getUser())).thenReturn(creator);
        when(matchService.generateLobbyCode(true)).thenReturn("ABCD");
        ArgumentCaptor<Match> matchCaptor = ArgumentCaptor.forClass(Match.class);
        when(matchService.createMatch(creator, true)).thenReturn(persisted);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/matches");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseEntity<Match> response = matchController.createMatch(true);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Match body = response.getBody();
        assertNotNull(body);
        assertEquals("ABCD", body.getCode());
        assertTrue(creator.getIsCurrentlyInMatch(), "Creator should be flagged as in match");
        verify(playerService).save(creator);
        verify(webSocketMatchService).broadcastLobbyState(persisted);
        Match matchSentToService = matchCaptor.getValue();
        assertEquals("ABCD", matchSentToService.getCode());
        assertSame(creator, matchSentToService.getPlayer1());
        assertSame(creator, matchSentToService.getCreator());
        URI location = response.getHeaders().getLocation();
        assertNotNull(location);
        assertTrue(location.getPath().endsWith("/77"));
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Crear lobby falla si el jugador ya está ocupado")
    @Story("Create lobby")
    @Description("A player already flagged as in-match cannot start a new lobby.")
    @Severity(SeverityLevel.MINOR)
    void createMatch_rejectsPlayerAlreadyInLobby() throws Exception {
        Player creator = buildPlayer(2, "busy", true);
        when(userService.findCurrentUser()).thenReturn(creator.getUser());
        when(playerService.getPlayerByUser(creator.getUser())).thenReturn(creator);

        assertThrows(AccessDeniedException.class, () -> matchController.createMatch(false));
        verify(matchService, never()).createMatch(any(Player.class), any(Boolean.class));
        verifyNoInteractions(webSocketMatchService);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("joinMatch añade al segundo jugador si hay hueco")
    @Story("Join lobby")
    @Description("Joining an open lobby should add the guest, set flags, and broadcast the updated states.")
    @Severity(SeverityLevel.CRITICAL)
    void joinMatch_addsSecondPlayerWhenLobbyOpen() throws Exception {
        Player player1 = buildPlayer(3, "host", true);
        Player player2 = buildPlayer(4, "guest", false);
        Match match = buildMatch(100, player1, null);
        when(matchService.getMatchById(100)).thenReturn(match);
        when(userService.findCurrentUser()).thenReturn(player2.getUser());
        when(playerService.getPlayerByUser(player2.getUser())).thenReturn(player2);
        when(matchService.joinMatch(match, playerService.getPlayerByUser(player2.getUser()), "aaaa")).thenReturn(match);

        ResponseEntity<Match> response = matchController.joinMatch(100, Optional.empty());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(player2, match.getPlayer2());
        assertTrue(player2.getIsCurrentlyInMatch());
        verify(playerService).save(player2);
        verify(matchService).joinMatch(match, player2, "aaaa");
        verify(webSocketMatchService).broadcastLobbyAndMatchState(match);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("joinMatch devuelve la lobby si el usuario ya está dentro")
    @Story("Join lobby")
    @Description("If the current user already belongs to the lobby, the controller should simply return it without changes.")
    @Severity(SeverityLevel.NORMAL)
    void joinMatch_returnsExistingLobbyWhenAlreadyParticipant() throws Exception {
        Player player1 = buildPlayer(5, "self", true);
        Match match = buildMatch(101, player1, null);
        when(matchService.getMatchById(101)).thenReturn(match);
        when(userService.findCurrentUser()).thenReturn(player1.getUser());
        when(playerService.getPlayerByUser(player1.getUser())).thenReturn(player1);

        ResponseEntity<Match> response = matchController.joinMatch(101, Optional.empty());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(matchService, never()).joinMatch(any(Match.class), any(Player.class), any(String.class));
        verifyNoInteractions(webSocketMatchService);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("joinMatch rechaza códigos privados incorrectos")
    @Story("Join lobby")
    @Description("Private lobbies must validate the invitation code before allowing entry.")
    @Severity(SeverityLevel.NORMAL)
    void joinMatch_rejectsWhenCodeMismatch() throws Exception {
        Player player1 = buildPlayer(6, "lock", true);
        Match match = buildMatch(102, player1, null);
        match.setCode("ABCD");
        when(matchService.getMatchById(102)).thenReturn(match);

        assertThrows(AccessDeniedException.class, () -> matchController.joinMatch(102, Optional.of("WXYZ")));
        verifyNoInteractions(webSocketMatchService);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("joinMatch rechaza lobbies completos")
    @Story("Join lobby")
    @Description("A lobby that already has two players must reject additional participants.")
    @Severity(SeverityLevel.NORMAL)
    void joinMatch_rejectsFullLobby() throws Exception {
        Player player1 = buildPlayer(7, "full", true);
        Player player2 = buildPlayer(8, "taken", true);
        Player intruder = buildPlayer(28, "intruder", false);
        Match match = buildMatch(103, player1, player2);
        when(matchService.getMatchById(103)).thenReturn(match);
        when(userService.findCurrentUser()).thenReturn(intruder.getUser());
        when(playerService.getPlayerByUser(intruder.getUser())).thenReturn(intruder);

        assertThrows(AccessDeniedException.class, () -> matchController.joinMatch(103, Optional.empty()));
        verifyNoInteractions(webSocketMatchService);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("leaveMatch elimina invitado y actualiza flags")
    @Story("Leave lobby")
    @Description("When a guest leaves, their flag resets and remaining players/lobbies are notified.")
    @Severity(SeverityLevel.CRITICAL)
    void leaveMatch_removesPlayerAndUpdatesFlags() throws Exception {
        Player player1 = buildPlayer(9, "host", true);
        Player player2 = buildPlayer(10, "leaver", true);
        Match match = buildMatch(104, player1, player2);
        when(matchService.getMatchById(104)).thenReturn(match);
        when(userService.findCurrentUser()).thenReturn(player2.getUser());
        when(playerService.getPlayerByUser(player2.getUser())).thenReturn(player2);
        when(matchService.leaveMatch(match, player2)).thenReturn(match);

        ResponseEntity<Void> response = matchController.leaveMatch(104);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(player2.getIsCurrentlyInMatch());
        verify(playerService).save(player2);
        verify(matchService).leaveMatch(match, player2);
        verify(webSocketMatchService).broadcastLobbyState(match);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("leaveMatch cierra la lobby al irse el último jugador")
    @Story("Leave lobby")
    @Description("If the last player leaves, the lobby must be closed and closure broadcasted.")
    @Severity(SeverityLevel.CRITICAL)
    void leaveMatch_closesLobbyWhenLastPlayerLeaves() throws Exception {
        Player player1 = buildPlayer(29, "solo", true);
        Match match = buildMatch(204, player1, null);
        when(matchService.getMatchById(204)).thenReturn(match);
        when(userService.findCurrentUser()).thenReturn(player1.getUser());
        when(playerService.getPlayerByUser(player1.getUser())).thenReturn(player1);
        when(matchService.leaveMatch(match, player1)).thenReturn(null);

        ResponseEntity<Void> response = matchController.leaveMatch(204);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(player1.getIsCurrentlyInMatch());
        verify(playerService).save(player1);
        verify(matchService).leaveMatch(match, player1);
        verify(webSocketMatchService).broadcastLobbyClosed(204);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("leaveMatch rechaza peticiones de usuarios externos")
    @Story("Leave lobby")
    @Description("Only players inside the lobby can request to leave; outsiders get rejected.")
    @Severity(SeverityLevel.NORMAL)
    void leaveMatch_rejectsPlayerOutsideLobby() throws Exception {
        Player player1 = buildPlayer(11, "host", true);
        Player outsider = buildPlayer(12, "outsider", true);
        Match match = buildMatch(105, player1, null);
        when(matchService.getMatchById(105)).thenReturn(match);
        when(userService.findCurrentUser()).thenReturn(outsider.getUser());
        when(playerService.getPlayerByUser(outsider.getUser())).thenReturn(outsider);

        assertThrows(AccessDeniedException.class, () -> matchController.leaveMatch(105));
        verifyNoInteractions(webSocketMatchService);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("startMatch solo permite al creador iniciar la partida")
    @Story("Start match")
    @Description("The lobby creator is the only one allowed to start the match, broadcasting updated state.")
    @Severity(SeverityLevel.CRITICAL)
    void startMatch_onlyCreatorCanStart() throws Exception {
        Player creator = buildPlayer(13, "creator", true);
        Player guest = buildPlayer(14, "guest", true);
        Match match = buildMatch(106, creator, guest);
        when(matchService.getMatchById(106)).thenReturn(match);
        when(userService.findCurrentUser()).thenReturn(creator.getUser());
        when(playerService.getPlayerByUser(creator.getUser())).thenReturn(creator);
        Match started = buildMatch(106, creator, guest);
        started.setStartedAt(LocalDateTime.now());
        when(matchService.startMatch(match)).thenReturn(started);

        ResponseEntity<Match> response = matchController.startMatch(106);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Match body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.getStartedAt());
        verify(matchService).startMatch(match);
        verify(webSocketMatchService).broadcastLobbyAndMatchState(started);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("startMatch rechaza a usuarios que no son el creador")
    @Story("Start match")
    @Description("Non-creators attempting to start should receive an access denied error.")
    @Severity(SeverityLevel.NORMAL)
    void startMatch_rejectsNonCreator() throws Exception {
        Player creator = buildPlayer(15, "creator", true);
        Player guest = buildPlayer(16, "guest", true);
        Player intruder = buildPlayer(17, "intruder", true);
        Match match = buildMatch(107, creator, guest);
        when(matchService.getMatchById(107)).thenReturn(match);
        when(userService.findCurrentUser()).thenReturn(intruder.getUser());
        when(playerService.getPlayerByUser(intruder.getUser())).thenReturn(intruder);

        assertThrows(AccessDeniedException.class, () -> matchController.startMatch(107));
        verifyNoInteractions(webSocketMatchService);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("startMatch devuelve el estado si ya estaba iniciada")
    @Story("Start match")
    @Description("If the match already started, the controller returns the current state without re-triggering logic.")
    @Severity(SeverityLevel.NORMAL)
    void startMatch_returnsExistingWhenAlreadyStarted() throws Exception {
        Player creator = buildPlayer(18, "creator", true);
        Player guest = buildPlayer(19, "guest", true);
        Match match = buildMatch(108, creator, guest);
        match.setStartedAt(LocalDateTime.now());
        when(matchService.getMatchById(108)).thenReturn(match);

        ResponseEntity<Match> response = matchController.startMatch(108);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Match body = response.getBody();
        assertNotNull(body);
        assertSame(match, body);
        verify(matchService, never()).startMatch(any(Match.class));
        verifyNoInteractions(webSocketMatchService);
    }

    private Match buildMatch(int id, Player player1, Player player2) {
        Match match = new Match();
        match.setId(id);
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setCreator(player1);
        match.setPlayer1Score(0);
        match.setPlayer2Score(0);
        match.setBoardState(new java.util.ArrayList<>());
        return match;
    }

    private Player buildPlayer(int id, String prefix, boolean inMatch) {
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
        player.setIsCurrentlyInMatch(inMatch);
        player.setUser(user);
        return player;
    }
}
*/
