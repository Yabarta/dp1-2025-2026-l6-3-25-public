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
import io.qameta.allure.Owner;

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
    @Feature("HU-23: Listado de usuarios (administrador)")
    @DisplayName("getAllPlayers Test")
    @Owner("luggzz(KDR0901)")
    void shouldGetAllPlayers() {
        List<Player> players = this.playerService.getAllPlayers();
        assertEquals(10, players.size(), "Incorrect number of players");

    }



    @Test
    @Feature("HU-28: Ver perfil de otro jugador (jugador)")
    @Transactional
    @DisplayName("getPlayerById Test")
    @Owner("luggzz(KDR0901)")
    void shouldGetPlayerById() {
        Player player = this.playerService.getPlayerById(1);
        assertEquals(1, player.getId(), "Incorrect id");
    }

    @Test
    @Feature("HU-28: Ver perfil de otro jugador (jugador)")
    @Transactional
    @DisplayName("getPlayerById Test (Negative)")
    @Owner("luggzz(KDR0901)")
    void shouldNotGetPlayerByIncorrectId() {
        assertThrows(ResourceNotFoundException.class, () -> this.playerService.getPlayerById(1000));
    }

    @Test
    @Feature("HU-28: Ver perfil de otro jugador (jugador)")
    @Transactional
    @DisplayName("getPlayerByNickname Test")
    @Owner("luggzz(KDR0901)")
    void shouldGetPlayerByNickname() {
        Player player = this.playerService.getPlayerByNickname("player1");
        assertEquals("player1", player.getNickname(), "Incorrect nickname");
    }

    @Test
    @Feature("HU-28: Ver perfil de otro jugador (jugador)")
    @Transactional
    @DisplayName("getPlayerByNickname Test (Negative)")
    @Owner("luggzz(KDR0901)")
    void shouldNotGetPlayerByIncorrectNickname() {
        assertThrows(ResourceNotFoundException.class, () -> this.playerService.getPlayerByNickname("nonExistentNickname"));
    }

    @Test
    @Feature("HU-28: Ver perfil de otro jugador (jugador)")
    @Transactional
    @DisplayName("getPlayerByUsername Test")
    @Owner("luggzz(KDR0901)")
    void shouldGetPlayerByUsername() {
        Player correctPlayer = this.playerService.getPlayerById(1);
        Player player = this.playerService.getPlayerByUsername("player1");
        assertEquals(correctPlayer.getId(), player.getId(), "Incorrect player");
    }

    @Test
    @Feature("HU-28: Ver perfil de otro jugador (jugador)")
    @Transactional
    @DisplayName("getPlayerByUsername Test (Negative)")
    @Owner("luggzz(KDR0901)")
    void shouldNotGetPlayerByIncorrectUsername() {
        assertThrows(ResourceNotFoundException.class, () -> this.playerService.getPlayerByUsername("nonExistentUsername"));
    }

    @Test
    @Feature("HU-28: Ver perfil de otro jugador (jugador)")
    @Transactional
    @DisplayName("getPlayerByUser Test")
    @Owner("luggzz(KDR0901)")
    void shouldGetPlayerByUser() {
        User user = this.userService.findUser(4);
        Player player = this.playerService.getPlayerByUser(user);
        assertEquals(user.getId(), player.getUser().getId(), "Incorrect user");
    }

    @Test
    @Feature("HU-28: Ver perfil de otro jugador (jugador)")
    @Transactional
    @DisplayName("getPlayerByUser Test (Negative)")
    @Owner("luggzz(KDR0901)")
    void shouldNotGetPlayerWithIncorrectUser() {
        User user = this.userService.findUser(1);
        assertThrows(ResourceNotFoundException.class, () -> this.playerService.getPlayerByUser(user));
    }


    @Test
    @Feature("HU-17: Registro de usuario (usuario)")
    @Transactional
    @DisplayName("Save Test")
    @Owner("luggzz(KDR0901)")
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
    @Feature("HU-25: Eliminar usuario (administrador)")
    @Transactional
    @DisplayName("Delete Test")
    @Owner("luggzz(KDR0901)")
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


    @Test
    @Feature("HU-20: Editar perfil (jugador)")
    @Transactional
    @DisplayName("Update match status Test")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void shouldUpdateIsCurrentlyInMatchFlag() {
        Player player = this.playerService.getPlayerById(1);
        Boolean initial = player.getIsCurrentlyInMatch();

        this.playerService.setIsCurrentlyInMatch(player, !initial);

        Player updated = this.playerService.getPlayerById(1);
        assertEquals(!initial, updated.getIsCurrentlyInMatch(), "Match flag should be toggled");

        this.playerService.setIsCurrentlyInMatch(updated, initial);
    }



}
