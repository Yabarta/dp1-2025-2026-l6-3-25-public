package es.us.dp1.l6_3_24_25.Petris.match.service;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

import es.us.dp1.l6_3_24_25.Petris.exceptions.AccessDeniedException;
import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.repository.MatchRepository;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;

@Epic("Game")
@Feature("Match management")
@Owner("josbardel1")
@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    protected MatchService matchService;

    @BeforeEach
    void setup() {
        matchService = new MatchService(matchRepository);
    }
    
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
        assertEquals(1, match.getId());
        System.out.println(match.getId());
    }

    @Test
    @DisplayName("Obtener partida por ID incorrecto")
    @Description("Método para obtener partida por ID incorrecto")
    @Owner("dlozaco(FBN5868)")
    void testGetMatchByWrongId() {
        Exception ex = assertThrows(ResourceNotFoundException.class, () -> matchService.getMatchById(100));
        assertEquals("Match not found with Id: '100'", ex.getMessage());
    }

    @Test
    @DisplayName("Obtener partida por code incorrecto")
    @Description("Método para obtener partida por código no existente")
    @Owner("dlozaco(FBN5868)")
    void testGetMatchByWrongCode() {
        Exception ex = assertThrows(ResourceNotFoundException.class, ()-> matchService.getMatchByCode("GBNW"));
        assertEquals("Match not found with Code: 'GBNW'", ex.getMessage());
    }

    @Test
    @DisplayName("Obtener partida por code")
    @Description("Método para obtener partida por código no existente")
    @Owner("dlozaco(FBN5868)")
    void testGetMatchByCode() {
        Match match = matchService.getMatchByCode("TRJU");
        assertEquals("TRJU", match.getCode());
        System.out.println(match.getCode());
    }

    @Test
    @DisplayName("Obtener todos las partidas en curso")
    @Description("Método para obtener la lista de partidas en curso")
    @Owner("dlozaco(FBN5868)")
    void testGetCurrentMatches() {
        List<Match> currentMatches = matchService.getCurrentMatches();
        assertNotNull(currentMatches, "List of current matches can not be null");
    }

    @Test
    @DisplayName("Obtener todas las partidas sin empezar")
    @Description("Metodo para obtener todas las partidas sin empezar")
    @Owner("dlozaco(FBN5868)")
    void testGetNotStartedMatches() {
        List<Match> notStartedMatches = matchService.getNotStartedMatches();
        assertNotNull(notStartedMatches, "List of not started matches can not be null");
    }

    @Test
    @DisplayName("Should not create match with creator already in a match")
    @Description("Test that if a player that is currently in a match attempts to create a match, AccessDeniedException is thrown and the match is not created")
    @Owner("josbardel1(WHS7046)")
    void testCreateMatchNegative() {
        verify(matchRepository, never()).save(any(Match.class));
        verifyNoMoreInteractions(matchRepository);

        Boolean isPrivate = false;
        Player creator = new Player();
        creator.setIsCurrentlyInMatch(true);

        assertThatExceptionOfType(AccessDeniedException.class).isThrownBy(() -> matchService.createMatch(creator, isPrivate))
            .withMessage("Already in a match");
    }

    @Test
    @DisplayName("Should create match with creator not already in a match")
    @Description("Test that if a player that is not currently in a match attempts to create a match, the match is created with that player as creator")
    @Owner("josbardel1(WHS7046)")
    void testCreateMatchPositive() {
        lenient().when(matchRepository.save(any(Match.class))).then(returnsFirstArg());

        Boolean isPrivate = false;
        Player creator = new Player();
        creator.setIsCurrentlyInMatch(false);

        assertThat(matchService.createMatch(creator, isPrivate));
    }
}
