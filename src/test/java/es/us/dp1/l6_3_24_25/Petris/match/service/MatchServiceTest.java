package es.us.dp1.l6_3_24_25.Petris.match.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;



@SpringBootTest
class MatchServiceTest {

    @Autowired
    MatchService matchService;

    @Test
    @DisplayName("Obtener todos las partidas")
    @Description("Método para obtener la lista de partidas")
    @Owner("dlozaco(FBN5868)")
    void testGetAllMatches() {
        List<Match> matches = matchService.getAllMatches();
        assertNotNull(matches, "List of matches must not be null");
        System.out.println(matches.getFirst().getCode());
    }

    @Test
    @DisplayName("Obtener partida por ID")
    @Description("Método para obtener partida por ID")
    @Owner("dlozaco(FBN5868)")
    void testGetMatchById() {
        Match match = matchService.getMatchById(1);
        assertNotNull(match, "Should return a match");
        System.out.println(match.getId());
    }

    @Test
    @DisplayName("Obtener partida por ID")
    @Description("Método para obtener partida por código")
    @Owner("dlozaco(FBN5868)")
    void testGetMatchByCode() {
        Match match = matchService.getMatchByCode("GBNW");
        assertNotNull(match, "Should return a match");
        System.out.println(match.getCode());
    }

    @Test
    @DisplayName("Obtener todos las partidas en curso")
    @Description("Método para obtener la lista de partidas en curso")
    @Owner("dlozaco(FBN5868)")
    void testGetCurrentMatches() {
        List<Match> currentMatches = matchService.getCurrentMatches();
        assertNotNull(currentMatches, "List of current matches can not be null");
        System.out.println(currentMatches.getFirst().getStartedAt());
    }

    @Test
    @DisplayName("Obtener todas las partidas sin empezar")
    @Description("Metodo para obtener todas las partidas sin empezar")
    @Owner("dlozaco(FBN5868)")
    void testGetNotStartedMatches() {
        List<Match> notStartedMatches = matchService.getNotStartedMatches();
        assertNotNull(notStartedMatches, "List of not started matches can not be null");
        System.out.println(notStartedMatches.getFirst().getCreatedAt());
    }

    @Test
    @DisplayName("Crear partida")
    @Description("Metodo para crear una partida")
    @Owner("dlozaco(FBN5868)")
    void testSave() {
        Match match = new Match();
        /*Player player1 = new Player();

        List<PetriDish> dishes = new ArrayList<>();

        for(int i = 0; i <= 6; i++){
            PetriDish petri = new PetriDish();
            petri.setMovements(List.of(1,2,3,4,5));
            dishes.add(petri);
        }
         */
        LocalDateTime fecha = LocalDateTime.now();
        match.setCode("HYMG");
        match.setTurn(4);
        match.setCreatedAt(fecha);
        /* Implementar cuando se haga la relación con Player
        match.setPetriDish(dishes);
        match.setCreator(player1);
        match.setPlayer1(player1);
        */
        Match createdMatch = matchService.save(match);

        assertEquals(createdMatch.getCode(), "HYMG", "Code doesnt match");
        assertEquals(createdMatch.getTurn(), 4, "Turn doesnt match");
        assertEquals(createdMatch.getCreatedAt(), fecha, "CreatedAt doesn't match");

    }
    /*
    @Test
    @DisplayName("Borrar partida")
    @Description("Metodo para borrar una partida")
    @Owner("dlozaco(FBN5868)")
    void testDelete() {
        matchService.delete(1);
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT, "Wrong status code");

    }
    */

}
