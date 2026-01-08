package es.us.dp1.l6_3_24_25.Petris.match.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.l6_3_24_25.Petris.configuration.SecurityConfiguration;
import es.us.dp1.l6_3_24_25.Petris.exceptions.AccessDeniedException;
import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.model.TurnType;
import es.us.dp1.l6_3_24_25.Petris.match.service.MatchService;
import es.us.dp1.l6_3_24_25.Petris.match.service.WebSocketMatchService;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import es.us.dp1.l6_3_24_25.Petris.user.UserService;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;

@Epic("Game module")
@Feature("REST controller for matches")
@WebMvcTest(value = {MatchController.class},
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class),
    excludeAutoConfiguration = SecurityConfiguration.class)
class MatchControllerTest {
    
    @MockitoBean
    MatchService matchService;
    @MockitoBean
    WebSocketMatchService webSocketMatchService;
    @MockitoBean
    UserService userService;
    @MockitoBean
    PlayerService playerService;

    @Autowired
    MockMvc mvc;

    private static final String BASE_URL = "/api/v1/matches";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        reset(matchService);
        reset(webSocketMatchService);
        reset(userService);
        reset(playerService);
    }

    private void verifyGetCurrentPlayer() {
        verify(userService, times(1)).findCurrentUser();
        verify(playerService, times(1)).getPlayerByUser(any());
    }

    private void stubCurrentPlayer(Player player) {
        when(userService.findCurrentUser()).thenReturn(new User());
        when(playerService.getPlayerByUser(any())).thenReturn(player);
    }

    @Test
    @DisplayName("Should return the match with the provided id")
    @Description("Test that if the match with the id as parameter is requested and exists, OK is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Create a new game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testGetMatchByIdPositive() throws Exception {
        int id = 1;
        Match match = new Match();
        match.setTurnType(TurnType.BINARY_FISSION);

        when(matchService.getMatchById(id)).thenReturn(match);

        mvc.perform(get(BASE_URL + "/{id}", id)
            .with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return the not ended match with the provided code")
    @Description("Test that if a not started match with the code as parameter is requested and exists, OK is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Create a new game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testGetMatchByCodePositive() throws Exception {
        String code = "AAAA";
        Match match = new Match();
        match.setTurnType(TurnType.BINARY_FISSION);

        when(matchService.getMatchByCode(code)).thenReturn(match);

        mvc.perform(get(BASE_URL + "/code/{code}", code)
            .with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should not create match if exception thrown")
    @Description("Test that if an exception is thrown when creating a match, FORBIDDEN is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Create a new game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testCreateMatchNegative() throws Exception {
        Boolean isPrivate = true;

        when(matchService.createMatch(any(), anyBoolean())).thenThrow(new AccessDeniedException());

        mvc.perform(post(BASE_URL)
            .with(csrf())
            .param("isPrivate", objectMapper.writeValueAsString(isPrivate)))
            .andExpect(status().isForbidden());

        verifyGetCurrentPlayer();
        verify(playerService, never()).setIsCurrentlyInMatch(any(), any());
        verify(webSocketMatchService, never()).broadcastLobbyState(any());
    }

    @Test
    @DisplayName("Should create match and set player to currently in a match")
    @Description("Test that if a player creates a match, CREATED is returned and isCurrentlyInMatch is set to true for the creator")
    @Owner("josbardel1(WHS7046)")
    @Story("Create a new game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testCreateMatchPositive() throws Exception {
        Match match = new Match();
        match.setTurnType(TurnType.BINARY_FISSION);
        Player creator = new Player();
        Boolean isPrivate = true;

        stubCurrentPlayer(creator);
        when(matchService.createMatch(creator, isPrivate)).thenReturn(match);

        mvc.perform(post(BASE_URL)
            .with(csrf())
            .param("isPrivate", objectMapper.writeValueAsString(isPrivate)))
            .andExpect(status().isCreated());

        verify(playerService, times(1)).setIsCurrentlyInMatch(creator, true);
        verify(webSocketMatchService, times(1)).broadcastLobbyState(match);
    }

    @Test
    @DisplayName("Should not join match if exception thrown")
    @Description("Test that if an exception is thrown when joining a match, FORBIDDEN is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testJoinMatchNegative() throws Exception {
        int id = 1;

        when(matchService.joinMatch(any(), any(), any())).thenThrow(new AccessDeniedException());

        mvc.perform(put(BASE_URL + "/{id}", id)
            .with(csrf()))
            .andExpect(status().isForbidden());

        verify(matchService, times(1)).getMatchById(id);
        verifyGetCurrentPlayer();
        verify(playerService, never()).setIsCurrentlyInMatch(any(), any());
        verify(webSocketMatchService, never()).broadcastLobbyAndMatchState(any());
    }

    @Test
    @DisplayName("Should join match and set player to currently in a match")
    @Description("Test that if a player joins a match, OK is returned and isCurrentlyInMatch is set to true for that player")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testJoinMatchPositive() throws Exception {
        int id = 1;
        String code = "AAAA";
        Match match = new Match();
        match.setTurnType(TurnType.BINARY_FISSION);
        Player playerToJoin = new Player();

        when(matchService.getMatchById(id)).thenReturn(match);
        stubCurrentPlayer(playerToJoin);
        when(matchService.joinMatch(match, playerToJoin, code)).thenReturn(match);

        mvc.perform(put(BASE_URL + "/{id}", id)
            .with(csrf())
            .param("code", code))
            .andExpect(status().isOk());

        verify(playerService, times(1)).setIsCurrentlyInMatch(playerToJoin, true);
        verify(webSocketMatchService, times(1)).broadcastLobbyAndMatchState(match);
    }

    @Test
    @DisplayName("Should return match if already in the match")
    @Description("Test that if a player already in a match attempts to join that match, OK is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testJoinMatchPositive2() throws Exception {
        int id = 1;
        Match match = new Match();
        match.setTurnType(TurnType.BINARY_FISSION);
        Player playerToJoin = new Player();
        match.setPlayer1(playerToJoin);

        when(matchService.getMatchById(id)).thenReturn(match);
        stubCurrentPlayer(playerToJoin);

        mvc.perform(put(BASE_URL + "/{id}", id)
            .with(csrf()))
            .andExpect(status().isOk());

        verify(matchService, never()).joinMatch(any(), any(), any());
        verify(playerService, never()).setIsCurrentlyInMatch(any(), anyBoolean());
        verify(webSocketMatchService, never()).broadcastLobbyAndMatchState(any());
    }

    @Test
    @DisplayName("Should not leave match if exception thrown")
    @Description("Test that if an exception is thrown when leaving a match, FORBIDDEN is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testLeaveMatchNegative() throws Exception {
        int id = 1;
        Player player1 = new Player();
        Player player2 = new Player();
        Match match = new Match();
        match.setTurnType(TurnType.BINARY_FISSION);
        match.setPlayer1(player1);
        match.setPlayer2(player2);

        when(matchService.getMatchById(id)).thenReturn(match);
        when(matchService.leaveMatch(any(), any())).thenThrow(new AccessDeniedException());

        mvc.perform(put(BASE_URL + "/{id}/leave", id)
            .with(csrf()))
            .andExpect(status().isForbidden());

        verifyGetCurrentPlayer();
        verify(playerService, never()).setIsCurrentlyInMatch(any(), any());
        verify(webSocketMatchService, never()).broadcastLobbyState(any());
    }

    @Test
    @DisplayName("Should leave match and set player to not currently in a match")
    @Description("Test that if a player leaves a match, NO_CONTENT is returned and isCurrentlyInMatch is set to false for that player")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testLeaveMatchPositive() throws Exception {
        int id = 1;
        Player player1 = new Player();
        Player player2 = new Player();
        Match match = new Match();
        match.setTurnType(TurnType.BINARY_FISSION);
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        Player playerToLeave = player2;

        when(matchService.getMatchById(id)).thenReturn(match);
        stubCurrentPlayer(playerToLeave);
        when(matchService.leaveMatch(match, playerToLeave)).thenReturn(match);

        mvc.perform(put(BASE_URL + "/{id}/leave", id)
            .with(csrf()))
            .andExpect(status().isNoContent());

        verify(playerService, times(1)).setIsCurrentlyInMatch(playerToLeave, false);
        verify(webSocketMatchService, times(1)).broadcastLobbyState(match);
    }

    @Test
    @DisplayName("Should leave match and set player to not currently in a match")
    @Description("Test that if a player leaves a match, NO_CONTENT is returned and isCurrentlyInMatch is set to false for that player")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testLeaveMatchPositive2() throws Exception {
        int id = 1;
        Match match = new Match();
        match.setTurnType(TurnType.BINARY_FISSION);
        Player playerToLeave = new Player();

        when(matchService.getMatchById(id)).thenReturn(match);
        stubCurrentPlayer(playerToLeave);

        mvc.perform(put(BASE_URL + "/{id}/leave", id)
            .with(csrf()))
            .andExpect(status().isNoContent());
        
        verify(matchService, times(1)).leaveMatch(match, playerToLeave);
        verify(playerService, times(1)).setIsCurrentlyInMatch(playerToLeave, false);
        verify(webSocketMatchService, times(1)).broadcastLobbyClosed(id);
    }

    @Test
    @DisplayName("Should not start match if exception thrown")
    @Description("Test that if an exception is thrown when leaving a match, FORBIDDEN is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testStartMatchNegative() throws Exception {
        int id = 1;
        Player creator = new Player();
        Match match = new Match();
        match.setTurnType(TurnType.BINARY_FISSION);
        match.setCreator(creator);

        when(matchService.getMatchById(id)).thenReturn(match);
        stubCurrentPlayer(creator);
        when(matchService.startMatch(any())).thenThrow(new AccessDeniedException());

        mvc.perform(put(BASE_URL + "/{id}/start", id)
            .with(csrf()))
            .andExpect(status().isForbidden());

        verify(webSocketMatchService, never()).broadcastLobbyAndMatchState(any());
    }

    @Test
    @DisplayName("Should not start match only if non-creator requested")
    @Description("Test that if a non-creator attempts to start the match, FORBIDDEN is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testStartMatchNegative2() throws Exception {
        int id = 1;
        Player notCreator = new Player();
        Match match = new Match();
        match.setTurnType(TurnType.BINARY_FISSION);

        when(matchService.getMatchById(id)).thenReturn(match);
        stubCurrentPlayer(notCreator);

        mvc.perform(put(BASE_URL + "/{id}/start", id)
            .with(csrf()))
            .andExpect(status().isForbidden());
        
        verify(matchService, never()).startMatch(any());
        verify(webSocketMatchService, never()).broadcastLobbyAndMatchState(any());
    }

    @Test
    @DisplayName("Should start match if creator requested")
    @Description("Test that if the creator attempts to start the match, the match starts and OK is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testStartMatchPositive() throws Exception {
        int id = 1;
        Player creator = new Player();
        Match match = new Match();
        match.setTurnType(TurnType.BINARY_FISSION);
        match.setCreator(creator);

        when(matchService.getMatchById(id)).thenReturn(match);
        stubCurrentPlayer(creator);
        when(matchService.startMatch(match)).thenReturn(match);

        mvc.perform(put(BASE_URL + "/{id}/start", id)
            .with(csrf()))
            .andExpect(status().isOk());
        
        verify(webSocketMatchService, times(1)).broadcastLobbyAndMatchState(match);
    }

    @Test
    @DisplayName("Should return match if creator requested starting when already started")
    @Description("Test that if the creator attempts to start an already started match, OK is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testStartMatchPositive2() throws Exception {
        int id = 1;
        Player creator = new Player();
        Match match = new Match();
        match.setTurnType(TurnType.BINARY_FISSION);
        match.setCreator(creator);
        match.setStartedAt(LocalDateTime.now());

        when(matchService.getMatchById(id)).thenReturn(match);
        stubCurrentPlayer(creator);

        mvc.perform(put(BASE_URL + "/{id}/start", id)
            .with(csrf()))
            .andExpect(status().isOk());
        
        verify(matchService, never()).startMatch(any());
        verify(webSocketMatchService, never()).broadcastLobbyAndMatchState(any());
    }

    @Test
    @DisplayName("Should not advance turn if not the player's propagation turn")
    @Description("Test that if a player attempts to advance the turn of a match with turnType propagation of the other player, FORBIDDEN is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testNextTurnNegative() throws Exception {
        int id = 1;
        Player player2 = new Player();
        Match match = new Match();
        match.setTurnType(TurnType.P1_PROPAGATION);
        match.setPlayer2(player2);

        when(matchService.getMatchById(id)).thenReturn(match);
        stubCurrentPlayer(player2);

        mvc.perform(put(BASE_URL + "/{id}/nextTurn", id)
            .with(csrf()))
            .andExpect(status().isForbidden());

        verify(matchService, never()).nextTurn(any(), any());
        verify(playerService, never()).setIsCurrentlyInMatch(any(), anyBoolean());
        verify(webSocketMatchService, never()).broadcastMatchEnded(any());
        verify(webSocketMatchService, never()).publishMatchSnapshot(any());
    }

    @Test
    @DisplayName("Should not advance turn if exception thrown")
    @Description("Test that if an exception is thrown when advancing turn, FORBIDDEN is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testNextTurnNegative2() throws Exception {
        int id = 1;
        Player player1 = new Player();
        Match match = new Match();
        match.setTurnType(TurnType.P1_PROPAGATION);
        match.setPlayer2(player1);

        when(matchService.getMatchById(id)).thenReturn(match);
        stubCurrentPlayer(player1);
        when(matchService.nextTurn(match, null)).thenThrow(new AccessDeniedException());

        mvc.perform(put(BASE_URL + "/{id}/nextTurn", id)
            .with(csrf()))
            .andExpect(status().isForbidden());

        verify(playerService, never()).setIsCurrentlyInMatch(any(), anyBoolean());
        verify(webSocketMatchService, never()).broadcastMatchEnded(any());
        verify(webSocketMatchService, never()).publishMatchSnapshot(any());
    }

    @Test
    @DisplayName("Should advance turn if appropriate player")
    @Description("Test that if turnType is the propagation of the requesting player, the turn advances and OK is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testNextTurnPositive() throws Exception {
        int id = 1;
        Player player1 = new Player();
        Player player2 = new Player();
        Match match = new Match();
        match.setTurnType(TurnType.P1_PROPAGATION);
        match.setPlayer1(player1);
        match.setPlayer2(player2);

        when(matchService.getMatchById(id)).thenReturn(match);
        stubCurrentPlayer(player1);
        when(matchService.nextTurn(match, null)).thenReturn(match);

        mvc.perform(put(BASE_URL + "/{id}/nextTurn", id)
            .with(csrf()))
            .andExpect(status().isOk());

        verify(playerService, never()).setIsCurrentlyInMatch(any(), anyBoolean());
        verify(webSocketMatchService, never()).broadcastMatchEnded(any());
        verify(webSocketMatchService, times(1)).publishMatchSnapshot(match);
    }

    @Test
    @DisplayName("Should set players not in a match after it ends")
    @Description("Test that if a valid turn advance results in the end of the match, the turn advances, isCurrentlyInMatch is set to false for both players and OK is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testNextTurnPositive2() throws Exception {
        int id = 1;
        Player player1 = new Player();
        Player player2 = new Player();
        Match match = new Match();
        match.setTurnType(TurnType.CONTAMINATION);
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        Match endedMatch = new Match();
        endedMatch.setTurnType(TurnType.CONTAMINATION);
        endedMatch.setEndedAt(LocalDateTime.now());

        when(matchService.getMatchById(id)).thenReturn(match);
        stubCurrentPlayer(player1);
        match.setEndedAt(LocalDateTime.now());
        when(matchService.nextTurn(match, null)).thenReturn(endedMatch);

        mvc.perform(put(BASE_URL + "/{id}/nextTurn", id)
            .with(csrf()))
            .andExpect(status().isOk());

        verify(playerService, times(2)).setIsCurrentlyInMatch(any(), eq(false));
        verify(webSocketMatchService, times(1)).broadcastMatchEnded(endedMatch);
        verify(webSocketMatchService, never()).publishMatchSnapshot(any());
    }

    @Test
    @DisplayName("Should return propagation errors")
    @Description("Test that when propagation errors are requested, OK is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testCheckErrorsPositive() throws Exception {
        int id = 1;
        Player player = new Player();
        Match match = new Match();
        match.setTurnType(TurnType.P1_PROPAGATION);
        List<PetriDish> newBoardState = new ArrayList<>();

        when(matchService.getMatchById(id)).thenReturn(match);
        stubCurrentPlayer(player);
        when(matchService.checkErrors(match, newBoardState, player)).thenReturn(new ArrayList<>());

        mvc.perform(get(BASE_URL + "/{id}/checkErrors", id)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newBoardState)))
            .andExpect(status().isOk());
        
        verify(matchService, times(1)).checkErrors(match, newBoardState, player);
    }

    @Test
    @DisplayName("Should concede match for requesting player")
    @Description("Test that when a player requests to concede a match, it ends and OK is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void testConcedeMatchPositive() throws Exception {
        int id = 1;
        Player player = new Player();
        Match match = new Match();
        match.setTurnType(TurnType.P1_PROPAGATION);

        when(matchService.getMatchById(id)).thenReturn(match);
        stubCurrentPlayer(player);
        when(matchService.concedeMatch(match, player)).thenReturn(match);

        mvc.perform(put(BASE_URL + "/{id}/endMatch", id)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        
        verify(playerService, times(2)).setIsCurrentlyInMatch(any(), eq(false));
        verify(webSocketMatchService, times(1)).broadcastMatchEnded(match);
    }

    @Test
    @DisplayName("Should delete match for requesting admin")
    @Description("Test that when an administrator requests to delete a match, it is deleted and NO_CONTENT is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @WithMockUser(username = "admin", authorities = "ADMIN")
    public void testDeleteMatchPositive() throws Exception {
        int id = 1;
        Match match = new Match();
        match.setTurnType(TurnType.P1_PROPAGATION);

        when(matchService.getMatchById(id)).thenReturn(match);

        mvc.perform(delete(BASE_URL + "/delete/{id}", id)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
        
        verify(matchService, times(1)).delete(id);
        verify(playerService, times(2)).setIsCurrentlyInMatch(any(), eq(false));
        verify(webSocketMatchService, times(1)).broadcastLobbyClosed(id);
    }
}
