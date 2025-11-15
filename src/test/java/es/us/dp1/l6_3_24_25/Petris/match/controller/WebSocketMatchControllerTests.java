package es.us.dp1.l6_3_24_25.Petris.match.controller;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.service.MatchService;

@ExtendWith(MockitoExtension.class)
class WebSocketMatchControllerTests {

    @Mock
    private MatchService matchService;

    private WebSocketMatchController controller;

    @BeforeEach
    void setup() {
        controller = new WebSocketMatchController(matchService);
    }

    @Test
    void watchLobby_publishesCurrentSnapshot() {
        Match match = new Match();
        when(matchService.getMatchById(55)).thenReturn(match);

        controller.watchLobby(55);

        verify(matchService).publishLobbySnapshot(match);
    }

    @Test
    void watchLobbyList_publishesLobbyCollection() {
        controller.watchLobbyList();

        verify(matchService).publishLobbyList();
    }

    @Test
    void watchMatch_publishesMatchSnapshot() {
        Match match = new Match();
        when(matchService.getMatchById(99)).thenReturn(match);

        controller.watchMatch(99);

        verify(matchService).publishMatchSnapshot(match);
    }
}
