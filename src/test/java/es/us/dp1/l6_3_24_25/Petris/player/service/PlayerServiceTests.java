package es.us.dp1.l6_3_24_25.Petris.player.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import es.us.dp1.l6_3_24_25.Petris.user.UserService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("Player Service Module")
@SpringBootTest
public class PlayerServiceTests {

    @Autowired
    private PlayerService playerService;

    @Autowired
	private UserService userService;

    @Autowired
    private StatisticsService statisticsService;

    @Test
    @Transactional
    @Feature("Player Retrieval")
    @DisplayName("getAllPlayers Test")
    void shouldGetAllPlayers() {
        List<Player> players = this.playerService.getAllPlayers();
        assertEquals(10, players.size(), "Incorrect number of players");

    }



    @Test
    @Feature("Player Retrieval")
    @Transactional
    @DisplayName("getPlayerById Test")
    void shouldGetPlayerById() {
        Player player = this.playerService.getPlayerById(1);
        assertEquals(1, player.getId(), "Incorrect id");
    }

    @Test
    @Feature("Player Retrieval")
    @Transactional
    @DisplayName("getPlayerById Test (Negative)")
    void shouldNotGetPlayerByIncorrectId() {
        assertThrows(ResourceNotFoundException.class, () -> this.playerService.getPlayerById(1000));
    }

    @Test
    @Feature("Player Retrieval")
    @Transactional
    @DisplayName("getPlayerByNickname Test")
    void shouldGetPlayerByNickname() {
        Player player = this.playerService.getPlayerByNickname("player1");
        assertEquals("player1", player.getNickname(), "Incorrect nickname");
    }

    @Test
    @Feature("Player Retrieval")
    @Transactional
    @DisplayName("getPlayerByNickname Test (Negative)")
    void shouldNotGetPlayerByIncorrectNickname() {
        assertThrows(ResourceNotFoundException.class, () -> this.playerService.getPlayerByNickname("nonExistentNickname"));
    }

    @Test
    @Feature("Player Retrieval")
    @Transactional
    @DisplayName("getPlayerByUsername Test")
    void shouldGetPlayerByUsername() {
        Player correctPlayer = this.playerService.getPlayerById(1);
        Player player = this.playerService.getPlayerByUsername("player1");
        assertEquals(correctPlayer.getId(), player.getId(), "Incorrect player");
    }

    @Test
    @Feature("Player Retrieval")
    @Transactional
    @DisplayName("getPlayerByUsername Test (Negative)")
    void shouldNotGetPlayerByIncorrectUsername() {
        assertThrows(ResourceNotFoundException.class, () -> this.playerService.getPlayerByUsername("nonExistentUsername"));
    }

    @Test
    @Feature("Player Retrieval")
    @Transactional
    @DisplayName("getPlayerByUser Test")
    void shouldGetPlayerByUser() {
        User user = this.userService.findUser(4);
        Player player = this.playerService.getPlayerByUser(user);
        assertEquals(user.getId(), player.getUser().getId(), "Incorrect user");
    }

    @Test
    @Feature("Player Retrieval")
    @Transactional
    @DisplayName("getPlayerByUser Test (Negative)")
    void shouldNotGetPlayerWithIncorrectUser() {
        User user = this.userService.findUser(1);
        assertThrows(ResourceNotFoundException.class, () -> this.playerService.getPlayerByUser(user));
    }


    @Test
    @Feature("Player Management")
    @Transactional
    @DisplayName("Save Test")
    void shouldSave() {
        Integer count = this.playerService.getAllPlayers().size();

        Player newPlayer = new Player();
        newPlayer.setUser(userService.findUser(15));
        newPlayer.setEmail("kdr0901@gmail.com");
        newPlayer.setNickname("kdr0901");
        newPlayer.setIsCurrentlyInMatch(false);
        newPlayer.setStatistics(statisticsService.getStatisticsById(11));

        Player createdPlayer = this.playerService.save(newPlayer);

        assertEquals(newPlayer.getId(), createdPlayer.getId(), "Incorrect id");
        assertEquals(newPlayer.getUser(), createdPlayer.getUser(), "Incorrect user");
        assertEquals(newPlayer.getEmail(), createdPlayer.getEmail(), "Incorrect email");
        assertEquals(newPlayer.getNickname(), createdPlayer.getNickname(), "Incorrect nickname");

        Integer finalCount = this.playerService.getAllPlayers().size();
		assertEquals(count + 1, finalCount);
    }


    @Test
    @Feature("Player Management")
    @Transactional
    @DisplayName("Delete Test")
    void shouldDelete() {
        Integer firstCount = this.playerService.getAllPlayers().size();

        Player newPlayer = new Player();
        newPlayer.setUser(userService.findUser(14));
        newPlayer.setEmail("fbn5868@gmail.com");
        newPlayer.setNickname("fbn5868");
        newPlayer.setIsCurrentlyInMatch(false);
        newPlayer.setStatistics(statisticsService.getStatisticsById(11));
        this.playerService.save(newPlayer);

		Integer secondCount = playerService.getAllPlayers().size();
		assertEquals(firstCount + 1, secondCount);
		playerService.delete(newPlayer.getId());
		Integer lastCount = playerService.getAllPlayers().size();
		assertEquals(firstCount, lastCount);
    }



}
