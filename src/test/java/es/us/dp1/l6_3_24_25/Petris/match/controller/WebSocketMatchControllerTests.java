package es.us.dp1.l6_3_24_25.Petris.match.controller;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.service.MatchService;
import es.us.dp1.l6_3_24_25.Petris.match.service.WebSocketMatchService;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@ExtendWith(MockitoExtension.class)
@Epic("Match module")
@Feature("WebSocket controller")
@Owner("match-realtime-team")
class WebSocketMatchControllerTests {

    @Mock
    private MatchService matchService;

    @Mock
    private WebSocketMatchService webSocketMatchService;

    private WebSocketMatchController controller;

    @BeforeEach
    void setup() {
        controller = new WebSocketMatchController(matchService, webSocketMatchService);
    }

    @Test
    @DisplayName("watchLobby obtiene la partida y publica el snapshot")
    @Story("Subscribe single lobby feed")
    @Description("Ensures watchLobby loads the match and publishes a lobby snapshot to subscribers.")
    @Severity(SeverityLevel.CRITICAL)
    void watchLobby_publishesCurrentSnapshot() {
        Match match = new Match();
        when(matchService.getMatchById(55)).thenReturn(match);

        controller.watchLobby(55);

        verify(webSocketMatchService).publishLobbySnapshot(match);
    }

    @Test
    @DisplayName("watchLobbyList publica la colección de lobbies")
    @Story("Subscribe lobby list feed")
    @Description("Validates that the lobby list endpoint pushes the aggregated lobby collection.")
    @Severity(SeverityLevel.NORMAL)
    void watchLobbyList_publishesLobbyCollection() {
        controller.watchLobbyList();

        verify(webSocketMatchService).publishLobbyList();
    }

    @Test
    @DisplayName("watchMatch publica el snapshot completo de la partida")
    @Story("Subscribe ongoing match feed")
    @Description("Checks that watchMatch retrieves the match and relays the full board snapshot via WebSocket.")
    @Severity(SeverityLevel.CRITICAL)
    void watchMatch_publishesMatchSnapshot() {
        Match match = new Match();
        when(matchService.getMatchById(99)).thenReturn(match);

        controller.watchMatch(99);

        verify(webSocketMatchService).publishMatchSnapshot(match);
    }
}
