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

@Epic("Player Module")
@SpringBootTest
public class PlayerServiceTests {

    @Autowired
    private PlayerService playerService;

    @Autowired
	private UserService userService;

    @Test
    @Transactional
    @Feature("Get All Players")
    @DisplayName("getAllPlayers Test")
    void shouldGetAllPlayers() {
        List<Player> players = this.playerService.getAllPlayers();
        assertEquals(10, players.size(), "Incorrect number of players");

    }



    @Test
    @Feature("Get Player by Id")
    @Transactional
    @DisplayName("getPlayerById Test")
    void shouldGetPlayerById() {
        Player player = this.playerService.getPlayerById(1);
        assertEquals(1, player.getId(), "Incorrect id");
    }

    @Test
    @Feature("Get Player by Id")
    @Transactional
    @DisplayName("getPlayerById Test (Negative)")
    void shouldNotGetPlayerByIncorrectId() {
        assertThrows(ResourceNotFoundException.class, () -> this.playerService.getPlayerById(1000));
    }

    @Test
    @Feature("Get Player by Nickname")
    @Transactional
    @DisplayName("getPlayerByNickname Test")
    void shouldGetPlayerByNickname() {
        Player player = this.playerService.getPlayerByNickname("player1");
        assertEquals("player1", player.getNickname(), "Incorrect nickname");
    }

    @Test
    @Feature("Get Player by Nickname")
    @Transactional
    @DisplayName("getPlayerByNickname Test (Negative)")
    void shouldNotGetPlayerByIncorrectNickname() {
        assertThrows(ResourceNotFoundException.class, () -> this.playerService.getPlayerByNickname("nonExistentNickname"));
    }

    @Test
    @Feature("Get Player by Username")
    @Transactional
    @DisplayName("getPlayerByUsername Test")
    void shouldGetPlayerByUsername() {
        Player correctPlayer = this.playerService.getPlayerById(1);
        Player player = this.playerService.getPlayerByUsername("player1");
        assertEquals(correctPlayer.getId(), player.getId(), "Incorrect player");
    }

    @Test
    @Feature("Get Player by Username")
    @Transactional
    @DisplayName("getPlayerByUsername Test (Negative)")
    void shouldNotGetPlayerByIncorrectUsername() {
        assertThrows(ResourceNotFoundException.class, () -> this.playerService.getPlayerByUsername("nonExistentUsername"));
    }

    @Test
    @Feature("Get Player by User")
    @Transactional
    @DisplayName("getPlayerByUser Test")
    void shouldGetPlayerByUser() {
        User user = this.userService.findUser(4);
        Player player = this.playerService.getPlayerByUser(user);
        assertEquals(user.getId(), player.getUser().getId(), "Incorrect user");
    }

    @Test
    @Feature("Get Player by User")
    @Transactional
    @DisplayName("getPlayerByUser Test (Negative)")
    void shouldNotGetPlayerWithIncorrectUser() {
        User user = this.userService.findUser(1);
        assertThrows(ResourceNotFoundException.class, () -> this.playerService.getPlayerByUser(user));;
    }


    @Test
    @Feature("Save Player")
    @Transactional
    @DisplayName("Save Test")
    void shouldSave() {
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
    @Feature("Delete Player")
    @Transactional
    @DisplayName("Delete Test")
    void shouldDelete() {
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
