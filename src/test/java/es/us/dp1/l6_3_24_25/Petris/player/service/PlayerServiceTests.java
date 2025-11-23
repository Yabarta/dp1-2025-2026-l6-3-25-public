package es.us.dp1.l6_3_24_25.Petris.player.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import es.us.dp1.l6_3_24_25.Petris.user.UserService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Epic("Player Module")
@Feature("Player Service")
@SpringBootTest
public class PlayerServiceTests {

    @Autowired
    private PlayerService playerService;

    @Autowired
	private UserService userService;

    @Test
    @Transactional
    @DisplayName("getAllPlayers Test")
    void shouldGetAllPlayers() {
        List<Player> players = this.playerService.getAllPlayers();
        assertEquals(10, players.size(), "Incorrect number of players");

    }

    @Test
    @Transactional
    @DisplayName("getPlayerById Test")
    void testGetPlayerById() {
        Player player = this.playerService.getPlayerById(1);
        assertEquals(1, player.getId(), "Incorrect id");
    }

    @Test
    @Transactional
    @DisplayName("getPlayerById Test (Negative)")
    void shouldNotGetPlayerByIncorrectId() {
        assertThrows(ResourceNotFoundException.class, () -> this.playerService.getPlayerById(50));
    }

    @Test
    @Transactional
    @DisplayName("getPlayerByNickname Test")
    void testGetPlayerByNickname() {
        Player player = this.playerService.getPlayerByNickname("player1");
        assertEquals("player1", player.getNickname(), "Incorrect nickname");
    }

    @Test
    @Transactional
    @DisplayName("getPlayerByNickname Test (Negative)")
    void shouldNotGetPlayerByIncorrectNickname() {
        assertThrows(ResourceNotFoundException.class, () -> this.playerService.getPlayerByNickname("nonExistingUsername"));
    }

    @Test
    @DisplayName("getPlayerByUser Test")
    void testGetPlayerByUser() {
        User user = this.userService.findUser(4);
        Player player = this.playerService.getPlayerByUser(user);
        assertEquals(user.getId(), player.getUser().getId(), "Incorrect user");
    }

    @Test
    @Transactional
    @DisplayName("getPlayerByUser Test (Negative)")
    void shouldNotGetPlayerWithIncorrectUser() {
        User user = this.userService.findUser(1);
        assertThrows(ResourceNotFoundException.class, () -> this.playerService.getPlayerByUser(user));;
    }


    @Test
    @Transactional
    @DisplayName("Base save Test")
    void testSave() {
        Integer count = this.playerService.getAllPlayers().size();

        Player newPlayer = new Player();
        newPlayer.setUser(userService.findUser(15));
        newPlayer.setEmail("kdr0901@gmail.com");
        newPlayer.setNickname("kdr0901");
        newPlayer.setIsCurrentlyInMatch(false);

        Player createdPlayer = this.playerService.save(newPlayer);

        assertEquals(newPlayer.getId(), createdPlayer.getId(), "Incorrect id");
        assertEquals(newPlayer.getUser(), createdPlayer.getUser(), "Incorrect user");
        assertEquals(newPlayer.getEmail(), createdPlayer.getEmail(), "Incorrect email");
        assertEquals(newPlayer.getNickname(), createdPlayer.getNickname(), "Incorrect nickname");
        assertEquals(11, this.playerService.getAllPlayers().size());

        Integer finalCount = this.playerService.getAllPlayers().size();
		assertEquals(count + 1, finalCount);
    }


    @Test
    @Transactional
    @DisplayName("Base delete Test")
    void testDelete() {
        Integer firstCount = this.playerService.getAllPlayers().size();

        Player newPlayer = new Player();
        newPlayer.setUser(userService.findUser(14));
        newPlayer.setEmail("fbn5868@gmail.com");
        newPlayer.setNickname("fbn5868");
        newPlayer.setIsCurrentlyInMatch(false);
        this.playerService.save(newPlayer);

		Integer secondCount = playerService.getAllPlayers().size();
		assertEquals(firstCount + 1, secondCount);
		playerService.delete(newPlayer.getId());
		Integer lastCount = playerService.getAllPlayers().size();
		assertEquals(firstCount, lastCount);
    }

}
