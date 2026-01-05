package es.us.dp1.l6_3_24_25.Petris.player.controller;

import java.io.InputStream;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.player.model.Achievement;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.model.Statistics;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import es.us.dp1.l6_3_24_25.Petris.user.Authorities;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.Mockito.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@Epic("Player Controller Module")
@WithMockUser(username = "player", roles = {"PLAYER"})
@WebMvcTest(PlayerController.class)
public class PlayerControllerTests {

    private static final String BASE_URL = "/api/v1/players";
    private Player player;
    private User user;
    private Authorities auth;
    private Statistics statistics;
    private List<Achievement> achievements;
    private Match match1, match2;

	private static final int TEST_USER_ID = 1;
	private static final int TEST_AUTH_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
	private ObjectMapper objectMapper;

    @MockBean
    private PlayerService playerService;
    
    @Autowired
    private PlayerController playerController;
    /* 
    @BeforeEach
    void setUp() {
        playerController = new PlayerController(playerService);
        auth = new Authorities();
		auth.setId(TEST_AUTH_ID);
		auth.setAuthority("PLAYER");

		user = new User();
		user.setId(TEST_USER_ID);
		user.setUsername("user");
		user.setPassword("password");
		user.setAuthority(auth);

        statistics = new Statistics();
        statistics.setId(1);
        statistics.setGamesPlayed(10);
        statistics.setGamesWon(4);
        statistics.setSarcinesCreated(7);

        Achievement ach1 = new Achievement();
        ach1.setId(1);
        ach1.setName("ach1");
        ach1.setDescription("desc1");
        ach1.setValor(10);
        ach1.setStatisticName("stat1");
        ach1.setImage("img1");
        Achievement ach2 = new Achievement();
        ach2.setId(2);
        ach2.setName("ach2");
        ach2.setDescription("desc2");
        ach2.setValor(20);
        ach2.setStatisticName("stat2");
        ach2.setImage("img2");
        achievements = List.of(ach1, ach2);

        player = new Player();
        player.setId(1);
        player.setNickname("player1");
        player.setIsCurrentlyInMatch(false);
        player.setUser(user);
        player.setStatistics(statistics);
        player.setAchievements(achievements);

        match1 = new Match();
        match1.setId(1);
        match1.setCreatedAt(LocalDateTime.now());
        match1.setCreator(player);
        match1.setPlayer1(player);

        match2 = new Match();
        match2.setId(2);
        match2.setCreatedAt(LocalDateTime.now());
        match2.setCreator(player);
        match2.setPlayer2(player);
    }

    @Test
    @Feature("Get All Players")
    @DisplayName("Get All Players (Successfully)")
    void shouldGetAllPlayers() throws Exception {
        User user2 = new User();
        user2.setId(2);
        user2.setUsername("user2");
        user2.setPassword("password2");
        user2.setAuthority(auth);

        Player player2 = new Player();
        player2.setId(2);
        player2.setNickname("player2");
        player2.setIsCurrentlyInMatch(false);
        player2.setUser(user2);

        when(playerService.getAllPlayers()).thenReturn(List.of(player, player2));
        mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$[?(@.id == 1)].nickname").value("player1"))
            .andExpect(jsonPath("$[?(@.id == 2)].nickname").value("player2"));
    }

    @Test
    @Feature("Get Player by Id")
    @DisplayName("Get Player by Id (Successfully)")
    void testGetPlayerById() throws Exception {
        when(playerService.getPlayerById(1)).thenReturn(player);
        mockMvc.perform(get(BASE_URL + "/1")).andExpect(status().isOk())
            .andExpect(jsonPath("$.nickname").value("player1"));
    }

    @Test
    @Feature("Get Player by Id")
    @DisplayName("Get Player by Id (Id not found)")
    void testGetPlayerById_NotFound() throws Exception {
        when(playerService.getPlayerById(1000)).thenThrow(new ResourceNotFoundException("Player", "id", 1000));
        mockMvc.perform(get(BASE_URL + "/1000")).andExpect(status().isNotFound());
    }

    @Test
    @Feature("Get Player Statistics by Id")
    @DisplayName("Get Player Statistics by Id (Successfully)")
    void testGetPlayerStatsById() throws Exception {
        when(playerService.getPlayerById(1)).thenReturn(player);
        mockMvc.perform(get(BASE_URL + "/1/statistics")).andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.gamesPlayed").value(10))
            .andExpect(jsonPath("$.gamesWon").value(4))
            .andExpect(jsonPath("$.sarcinesCreated").value(7));
    }


    @Test
    @Feature("Get Player Specific Statistic by Id")
    @DisplayName("Get Player Specific Statistic by Id (Successfully)")
    void testGetPlayerSpecificStatById() throws Exception {
        when(playerService.getPlayerById(1)).thenReturn(player);
        mockMvc.perform(get(BASE_URL + "/1/statistics/1")).andExpect(status().isOk())
            .andExpect(jsonPath("$.gamesPlayed").value(10));
    }

    @Test
    @Feature("Get Player Specific Statistic by Id")
    @DisplayName("Get Player Specific Statistic by Id (Stat not found)")
    void testGetPlayerSpecificStatById_NotFound() throws Exception {
        when(playerService.getPlayerById(1)).thenReturn(player);
        mockMvc.perform(get(BASE_URL + "/1/statistics/1000")).andExpect(status().isNotFound());
    }

    @Test
    @Feature("Get Player Achievements by Id")
    @DisplayName("Get Player Achievements by Id (Successfully)")
    void testGetPlayerAchievementById() throws Exception {
        when(playerService.getPlayerById(1)).thenReturn(player);
        mockMvc.perform(get(BASE_URL + "/1/achievements")).andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$[?(@.id == 1)].name").value("ach1"))
            .andExpect(jsonPath("$[?(@.id == 2)].name").value("ach2"));
    }

    @Test
    @Feature("Get Player Achievements by Id")
    @DisplayName("Get Player Achievements by Id (No Achievements)")
    void testGetPlayerAchievementById_NoAchievements() throws Exception {
        Player playerNoAch = new Player();
        playerNoAch.setId(2);
        playerNoAch.setNickname("player2");
        playerNoAch.setIsCurrentlyInMatch(false);
        playerNoAch.setUser(user);
        Statistics auxStatistics = new Statistics();
        auxStatistics.setId(2);
        playerNoAch.setStatistics(auxStatistics);

        when(playerService.getPlayerById(2)).thenReturn(playerNoAch);
        mockMvc.perform(get(BASE_URL + "/2/achievements")).andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(0))
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Feature("Get Player by Nickname")
    @DisplayName("Get Player by Nickname (Successfully)")
    void testGetPlayerByNickname() throws Exception {
        when(playerService.getPlayerByNickname("player1")).thenReturn(player);
        mockMvc.perform(get(BASE_URL + "/nickname/player1")).andExpect(status().isOk())
            .andExpect(jsonPath("$.nickname").value("player1"));
    }

    @Test
    @Feature("Get Player by Nickname")
    @DisplayName("Get Player by Nickname (Not Found)")
    void testGetPlayerByNickname_NotFound() throws Exception {
        when(playerService.getPlayerByNickname("unknown")).thenThrow(new ResourceNotFoundException("Player", "nickname", "unknown"));
        mockMvc.perform(get(BASE_URL + "/nickname/unknown")).andExpect(status().isNotFound());
    }

    @Test
    @Feature("Get Player by Username")
    @DisplayName("Get Player by Username (Successfully)")
    void testGetPlayerByUsername() throws Exception {
        when(playerService.getPlayerByUsername("user")).thenReturn(player);
        mockMvc.perform(get(BASE_URL + "/user/user")).andExpect(status().isOk())
            .andExpect(jsonPath("$.nickname").value("player1"));
    }

    @Test
    @Feature("Get Player by Username")
    @DisplayName("Get Player by Username (Not Found)")
    void testGetPlayerByUsername_NotFound() throws Exception {
        when(playerService.getPlayerByUsername("unknown")).thenThrow(new ResourceNotFoundException("User", "username", "unknown"));
        mockMvc.perform(get(BASE_URL + "/user/unknown")).andExpect(status().isNotFound());
    }

    @Test
    @Feature("Create Player")
    @DisplayName("Create Player (Successfully)")
    void testCreatePlayer() throws Exception {

        User user2 = new User();
        user2.setId(2);
        user2.setUsername("user2");
        user2.setPassword("password2");
        user2.setAuthority(auth);

        Player aux = new Player();
        aux.setId(2);
        aux.setNickname("player2");
        aux.setIsCurrentlyInMatch(false);
        aux.setUser(user2);

        mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(aux))).andExpect(status().isCreated());
    }

    @Test
    @Feature("Update Player")
    @DisplayName("Update Player (Successfully)")
    void testUpdatePlayer() throws Exception {
        player.setNickname("UPDATED");
        player.setEmail("email@gmail.com");

		when(this.playerService.getPlayerById(1)).thenReturn(player);
		when(this.playerService.save(any(Player.class))).thenReturn(player);

        mockMvc.perform(put(BASE_URL + "/1").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(player))).andExpect(status().isNoContent());

    }

    @Test
    @Feature("Update Player")
    @DisplayName("Update Player (Not Found)")
    void testUpdatePlayer_NotFound() throws Exception {
        player.setNickname("UPDATED");
        player.setEmail("email@gmail.com");

        when(this.playerService.getPlayerById(100)).thenThrow(ResourceNotFoundException.class);
        when(this.playerService.save(any(Player.class))).thenReturn(player);
        
        mockMvc.perform(put(BASE_URL + "/100").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(player))).andExpect(status().isNotFound());
    }

    @Test
    @Feature("Update Player Statistic")
    @DisplayName("Update Player Statistic (Successfully)")
    void testUpdatePlayerStat() throws Exception {
        Statistics statToUpdate = player.getStatistics();

        statToUpdate.setGamesPlayed(50);
        statToUpdate.setGamesWon(25);
        statToUpdate.setSarcinesCreated(30);

        when(this.playerService.getPlayerById(1)).thenReturn(player);

        mockMvc.perform(put(BASE_URL + "/1/statistics/1") 
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(statToUpdate))) 
            .andExpect(status().isOk());
    }

    @Test
    @Feature("Update Player Statistic")
    @DisplayName("Update Player Statistic (Not Found)")
    void testUpdatePlayerStat_NotFound() throws Exception {
        Statistics statToUpdate = player.getStatistics();

        statToUpdate.setGamesPlayed(50);
        statToUpdate.setGamesWon(25);
        statToUpdate.setSarcinesCreated(30);

        when(this.playerService.getPlayerById(1)).thenReturn(player);

        mockMvc.perform(put(BASE_URL + "/1/statistics/1000") 
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(statToUpdate))) 
            .andExpect(status().isNotFound());
    }

    @Test
    @Feature("Delete Player")
    @DisplayName("Delete Player (Successfully)")
    void testDeletePlayer() throws Exception {
        when(this.playerService.getPlayerById(1)).thenReturn(player);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(BASE_URL + "/1")
            .with(csrf()))
            .andExpect(status().isNoContent());

        verify(playerService, times(1)).delete(1);
    }

    @Test
    @Feature("Update Player with Image Upload")
    @DisplayName("Update Player with Image Upload (Successfully)")
    void testUpdatePlayerWithImage() throws Exception {
        final int playerId = 1;
        final String newNickname = "PhotoPlayer";
        final String originalFileName = "profile.png";
        final UUID fakeUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");

        Player originalPlayer = new Player();
        originalPlayer.setId(playerId);
        originalPlayer.setNickname(player.getNickname());
        originalPlayer.setProfilePicture("/uploads/old_pic.jpg");
        originalPlayer.setUser(user);

        when(playerService.getPlayerById(playerId)).thenReturn(originalPlayer);

        MockMultipartFile mockFile = new MockMultipartFile(
                "profilePicture",
                originalFileName,
                MediaType.IMAGE_PNG_VALUE,
                "file content".getBytes()
        );

        try (MockedStatic<UUID> uuidMock = mockStatic(UUID.class);
             MockedStatic<Files> filesMock = mockStatic(Files.class)) {

            uuidMock.when(UUID::randomUUID).thenReturn(fakeUuid);
            filesMock.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            filesMock.when(() -> Files.copy(any(InputStream.class), any(Path.class))).thenReturn(100L);            
            filesMock.when(() -> Files.deleteIfExists(any(Path.class))).thenReturn(true);

            mockMvc.perform(multipart(BASE_URL + "/{id}", playerId)
                    .file(mockFile)
                    .param("nickname", newNickname)
                    .with(csrf())
                    .with(request -> { 
                        request.setMethod("PUT"); 
                        return request;
                    }))
                    .andExpect(status().isOk());

            filesMock.verify(() -> Files.copy(any(InputStream.class), any(Path.class)), times(1));
            ArgumentCaptor<Player> playerCaptor = ArgumentCaptor.forClass(Player.class);
            verify(playerService, times(1)).save(playerCaptor.capture());
            
            Player savedPlayer = playerCaptor.getValue();
            String expectedUrl = "/uploads/" + fakeUuid.toString() + "_" + originalFileName;

            assertEquals(newNickname, savedPlayer.getNickname());
            assertEquals(expectedUrl, savedPlayer.getProfilePicture());
        }
    } */
}
