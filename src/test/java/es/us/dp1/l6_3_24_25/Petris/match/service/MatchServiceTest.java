package es.us.dp1.l6_3_24_25.Petris.match.service;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MatchServiceTest {

    @Autowired
    MatchService matchService;

    @Test
    @DisplayName("Obtener todos las partidas")
    @Description("Método para obtener la lista de partidas")
    @Owner("FBN5868")
    void getAllMatches() {
        List<Match> matches = matchService.getAllMatches();
        assertNotNull(matches, "List of matches must not be null");
    }

    @Test
    @DisplayName("Obtener partida por ID")
    @Description("Método para obtener partida por ID")
    @Owner("FBN5868")
    void getMatchById() {
        Match match = matchService.getMatchById(1);
        assertNotNull(match, "Should return a match");
    }

    @Test
    @DisplayName("Obtener partida por ID")
    @Description("Método para obtener partida por código")
    @Owner("FBN5868")
    void getMatchByCode() {
        Match match = matchService.getMatchByCode("GBNW");
        assertNotNull(match, "Should return a match");
    }

    @Test
    @DisplayName("Obtener todos las partidas en curso")
    @Description("Método para obtener la lista de partidas en curso")
    @Owner("FBN5868")
    void getCurrentMatches() {
        List<Match> currentMatches = matchService.getCurrentMatches();
        assertNotNull(currentMatches, "List of current matches can not be null");
    }

    @Test
    void getNotStartedMatches() {
    }

    @Test
    void save() {
    }

    @Test
    void delete() {
    }
}
