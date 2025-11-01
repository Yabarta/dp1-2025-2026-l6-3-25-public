package es.us.dp1.l6_3_24_25.Petris.player.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.user.UserService;

@SpringBootTest
public class PlayerServiceTests {

    @Autowired
    private PlayerService playerService;

    @Autowired
	private UserService userService;

    @Test
    void testGetAllPlayers() {
        List<Player> players = this.playerService.getAllPlayers();
        assertEquals(2, players.size(), "Incorrect number of players");
    }

    @Test
    void testGetPlayerById() {
        Player player = this.playerService.getPlayerById(1);
        assertEquals(1, player.getId(), "Incorrect id");
    }

    @Test
    void testGetPlayerByNickname() {
        Player player = this.playerService.getPlayerByNickname("player1");
        assertEquals("player1", player.getNickname(), "Incorrect nickname");
    }

    @Test
    void testSave() {
        Integer count = this.playerService.getAllPlayers().size();

        Player newPlayer = new Player();
        newPlayer.setUser(userService.findUser(6));
        newPlayer.setEmail("email3@gmail.com");
        newPlayer.setNickname("player3");

        Player createdPlayer = this.playerService.save(newPlayer);

        assertEquals(newPlayer.getId(), createdPlayer.getId(), "Incorrect id");
        assertEquals(newPlayer.getUser(), createdPlayer.getUser(), "Incorrect user");
        assertEquals(newPlayer.getEmail(), createdPlayer.getEmail(), "Incorrect email");
        assertEquals(newPlayer.getNickname(), createdPlayer.getNickname(), "Incorrect nickname");
        assertEquals(3, this.playerService.getAllPlayers().size());

        Integer finalCount = this.playerService.getAllPlayers().size();
		assertEquals(count + 1, finalCount);
    }

    
    @Test
    void testDelete() {
        Integer firstCount = this.playerService.getAllPlayers().size();

        Player newPlayer = new Player();
        newPlayer.setUser(userService.findUser(7));
        newPlayer.setEmail("email4@gmail.com");
        newPlayer.setNickname("player4");
        this.playerService.save(newPlayer);

		Integer secondCount = playerService.getAllPlayers().size();
		assertEquals(firstCount + 1, secondCount);
		playerService.delete(newPlayer.getId());
		Integer lastCount = playerService.getAllPlayers().size();
		assertEquals(firstCount, lastCount);
    }

}
