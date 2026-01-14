package es.us.dp1.l6_3_24_25.Petris.friend;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.l6_3_24_25.Petris.match.service.MatchService;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Epic("Friend Controller Module")
@WithMockUser(username = "player", roles = {"PLAYER"})
@WebMvcTest(FriendController.class)
public class FriendControllerTests {

    private static final String BASE_URL = "/api/v1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FriendService friendService;

    @MockBean
    private PlayerService playerService;

    @MockBean
    private MatchService matchService;

    private Player player1;
    private Player player2;
    private Friend friendship;

    @BeforeEach
    void setUp() {
        player1 = new Player();
        player1.setId(1);
        player1.setNickname("player1");

        player2 = new Player();
        player2.setId(2);
        player2.setNickname("player2");

        friendship = new Friend();
        friendship.setId(1);
        friendship.setRequester(player1);
        friendship.setReceiver(player2);
        friendship.setStatus(FriendshipStatus.PENDING);
    }

    @Test
    @Feature("Get Friend by ID")
    @DisplayName("Get Friend by ID (Successfully)")
    void shouldGetFriendById() throws Exception {
        when(friendService.getFriendsById(1)).thenReturn(Optional.of(friendship));

        mockMvc.perform(get(BASE_URL + "/players/friends/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @Feature("Get Friends by Username")
    @DisplayName("Get Friends by Username (Successfully)")
    void shouldGetFriendsByUsername() throws Exception {
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        when(friendService.getFriendsByUsername("player1")).thenReturn(List.of(friendship));

        mockMvc.perform(get(BASE_URL + "/players/player1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACCEPTED"));
    }

    @Test
    @Feature("Create Friend Request")
    @DisplayName("Create Friend Request (Successfully)")
    void shouldCreateFriendRequest() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("requester", "player1");
        body.put("receiver", "player2");

        when(playerService.getPlayerByUsername("player1")).thenReturn(player1);
        when(playerService.getPlayerByUsername("player2")).thenReturn(player2);
        when(friendService.create(any(Player.class), any(Player.class))).thenReturn(friendship);

        mockMvc.perform(post(BASE_URL + "/players/friends")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.requester.nickname").value("player1"))
                .andExpect(jsonPath("$.receiver.nickname").value("player2"));
    }

    @Test
    @Feature("Accept Friend Request")
    @DisplayName("Accept Friend Request (Successfully)")
    void shouldAcceptFriendRequest() throws Exception {
        when(friendService.getFriendsById(1)).thenReturn(Optional.of(friendship));

        mockMvc.perform(put(BASE_URL + "/players/friends/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        verify(friendService, times(1)).save(any(Friend.class));
    }

    @Test
    @Feature("Accept Friend Request")
    @DisplayName("Accept Friend Request (Not Found)")
    void shouldReturn404WhenAcceptingNonExistentFriend() throws Exception {
        when(friendService.getFriendsById(99)).thenReturn(Optional.empty());

        mockMvc.perform(put(BASE_URL + "/players/friends/99")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @Feature("Delete Friend")
    @DisplayName("Delete Friend (Successfully)")
    void shouldDeleteFriend() throws Exception {
        // En tu controlador, getFriendsById(id) devuelve ResponseEntity, aquí simulamos que existe
        when(friendService.getFriendsById(1)).thenReturn(Optional.of(friendship));

        mockMvc.perform(delete(BASE_URL + "/friends/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(friendService, times(1)).delete(1);
    }

    /* 
    @Test
    @Feature("Spectate Friends")
    @DisplayName("Get matches of friends to spectate")
    void shouldGetFriendMatches() throws Exception {
        Match match = new Match();
        match.setId(10);
        match.setPlayer1(player1);
        match.setPlayer2(player2);

        when(matchService.getCurrentMatches()).thenReturn(List.of(match));
        when(friendService.Player1IsFriendOfPlayer2(1, player1.getId())).thenReturn(true);
        when(friendService.Player1IsFriendOfPlayer2(1, player2.getId())).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/friends/espectate")
                .param("idPlayer", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(10));
    }*/

    @Test
    @Feature("Get Received Friend Requests")
    @DisplayName("Retrieve friend requests received by a player")
    void shouldGetRequests() throws Exception {
        when(friendService.getRequests("player1")).thenReturn(List.of(friendship));

        mockMvc.perform(get(BASE_URL + "/players/player1/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].requester.nickname").value("player1"));
    }

    @Test
    @Feature("Get Sent Friend Requests")
    @DisplayName("Retrieve friend requests sent by a player")
    void shouldGetRequester() throws Exception {
        when(friendService.getRequester("player1")).thenReturn(List.of(friendship));

        mockMvc.perform(get(BASE_URL + "/players/player1/requester"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].requester.nickname").value("player1"));
    }
}