package es.us.dp1.l6_3_24_25.Petris.match.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import es.us.dp1.l6_3_24_25.Petris.user.Authorities;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import es.us.dp1.l6_3_24_25.Petris.user.UserService;

@ExtendWith(MockitoExtension.class)
class MatchControllerTest {

    @Mock
    private MatchService matchService;

    @Mock
    private UserService userService;

    @Mock
    private PlayerService playerService;

    private MatchController matchController;

    @BeforeEach
    void setup() {
        matchController = new MatchController(matchService, userService, playerService);
    }

    @AfterEach
    void tearDownRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @SuppressWarnings("null")
    @Test
    void createMatch_generatesCodeForPrivateLobbyAndMarksPlayerBusy() throws Exception {
        Player creator = buildPlayer(1, "creator", false);
        Match persisted = new Match();
        persisted.setId(77);
        persisted.setCode("ABCD");
        persisted.setPlayer1(creator);
        persisted.setCreator(creator);
        when(userService.findCurrentUser()).thenReturn(creator.getUser());
        when(playerService.getPlayerByUser(creator.getUser())).thenReturn(creator);
        when(matchService.generateLobbyCode()).thenReturn("ABCD");
        ArgumentCaptor<Match> matchCaptor = ArgumentCaptor.forClass(Match.class);
        when(matchService.createMatch(matchCaptor.capture())).thenReturn(persisted);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/matches");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseEntity<Match> response = matchController.createMatch(true);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Match body = response.getBody();
        assertNotNull(body);
        assertEquals("ABCD", body.getCode());
        assertTrue(creator.getIsCurrentlyInMatch(), "Creator should be flagged as in match");
        verify(playerService).save(creator);
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
    void createMatch_rejectsPlayerAlreadyInLobby() throws Exception {
        Player creator = buildPlayer(2, "busy", true);
        when(userService.findCurrentUser()).thenReturn(creator.getUser());
        when(playerService.getPlayerByUser(creator.getUser())).thenReturn(creator);

        assertThrows(AccessDeniedException.class, () -> matchController.createMatch(false));
        verify(matchService, never()).createMatch(any(Match.class));
    }

    @SuppressWarnings("null")
    @Test
    void joinMatch_addsSecondPlayerWhenLobbyOpen() throws Exception {
        Player player1 = buildPlayer(3, "host", true);
        Player player2 = buildPlayer(4, "guest", false);
        Match match = buildMatch(100, player1, null);
        when(matchService.getMatchById(100)).thenReturn(match);
        when(userService.findCurrentUser()).thenReturn(player2.getUser());
        when(playerService.getPlayerByUser(player2.getUser())).thenReturn(player2);
        when(matchService.joinMatch(match)).thenReturn(match);

        ResponseEntity<Match> response = matchController.joinMatch(100, Optional.empty());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(player2, match.getPlayer2());
        assertTrue(player2.getIsCurrentlyInMatch());
        verify(playerService).save(player2);
        verify(matchService).joinMatch(match);
    }

    @SuppressWarnings("null")
    @Test
    void joinMatch_returnsExistingLobbyWhenAlreadyParticipant() throws Exception {
        Player player1 = buildPlayer(5, "self", true);
        Match match = buildMatch(101, player1, null);
        when(matchService.getMatchById(101)).thenReturn(match);
        when(userService.findCurrentUser()).thenReturn(player1.getUser());
        when(playerService.getPlayerByUser(player1.getUser())).thenReturn(player1);

        ResponseEntity<Match> response = matchController.joinMatch(101, Optional.empty());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(matchService, never()).joinMatch(any(Match.class));
    }

    @SuppressWarnings("null")
    @Test
    void joinMatch_rejectsWhenCodeMismatch() throws Exception {
        Player player1 = buildPlayer(6, "lock", true);
        Match match = buildMatch(102, player1, null);
        match.setCode("ABCD");
        when(matchService.getMatchById(102)).thenReturn(match);

        assertThrows(AccessDeniedException.class, () -> matchController.joinMatch(102, Optional.of("WXYZ")));
    }

    @SuppressWarnings("null")
    @Test
    void joinMatch_rejectsFullLobby() throws Exception {
        Player player1 = buildPlayer(7, "full", true);
        Player player2 = buildPlayer(8, "taken", true);
        Player intruder = buildPlayer(28, "intruder", false);
        Match match = buildMatch(103, player1, player2);
        when(matchService.getMatchById(103)).thenReturn(match);
        when(userService.findCurrentUser()).thenReturn(intruder.getUser());
        when(playerService.getPlayerByUser(intruder.getUser())).thenReturn(intruder);

        assertThrows(AccessDeniedException.class, () -> matchController.joinMatch(103, Optional.empty()));
    }

    @SuppressWarnings("null")
    @Test
    void leaveMatch_removesPlayerAndUpdatesFlags() throws Exception {
        Player player1 = buildPlayer(9, "host", true);
        Player player2 = buildPlayer(10, "leaver", true);
        Match match = buildMatch(104, player1, player2);
        when(matchService.getMatchById(104)).thenReturn(match);
        when(userService.findCurrentUser()).thenReturn(player2.getUser());
        when(playerService.getPlayerByUser(player2.getUser())).thenReturn(player2);
        when(matchService.leaveMatch(match, player2)).thenReturn(Optional.of(match));

        ResponseEntity<Void> response = matchController.leaveMatch(104);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(player2.getIsCurrentlyInMatch());
        verify(playerService).save(player2);
        verify(matchService).leaveMatch(match, player2);
    }

    @SuppressWarnings("null")
    @Test
    void leaveMatch_rejectsPlayerOutsideLobby() throws Exception {
        Player player1 = buildPlayer(11, "host", true);
        Player outsider = buildPlayer(12, "outsider", true);
        Match match = buildMatch(105, player1, null);
        when(matchService.getMatchById(105)).thenReturn(match);
        when(userService.findCurrentUser()).thenReturn(outsider.getUser());
        when(playerService.getPlayerByUser(outsider.getUser())).thenReturn(outsider);

        assertThrows(AccessDeniedException.class, () -> matchController.leaveMatch(105));
    }

    @SuppressWarnings("null")
    @Test
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
    }

    @SuppressWarnings("null")
    @Test
    void startMatch_rejectsNonCreator() throws Exception {
        Player creator = buildPlayer(15, "creator", true);
        Player guest = buildPlayer(16, "guest", true);
        Player intruder = buildPlayer(17, "intruder", true);
        Match match = buildMatch(107, creator, guest);
        when(matchService.getMatchById(107)).thenReturn(match);
        when(userService.findCurrentUser()).thenReturn(intruder.getUser());
        when(playerService.getPlayerByUser(intruder.getUser())).thenReturn(intruder);

        assertThrows(AccessDeniedException.class, () -> matchController.startMatch(107));
    }

    @SuppressWarnings("null")
    @Test
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
