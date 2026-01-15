package es.us.dp1.l6_3_24_25.Petris.match.service;

import es.us.dp1.l6_3_24_25.Petris.match.dto.LobbyDTO;
import es.us.dp1.l6_3_24_25.Petris.match.dto.MatchDTO;
import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.repository.MatchRepository;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Epic("Match WebSocket")
@Feature("WebSocketMatchService publishing")
@ExtendWith(MockitoExtension.class)
class WebSocketMatchServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MatchService matchService; 

    private WebSocketMatchService buildServiceWithTemplate(SimpMessagingTemplate template) {
        ObjectProvider<SimpMessagingTemplate> provider = new ObjectProvider<>() {
            @Override
            public SimpMessagingTemplate getObject(Object... args) { return template; }
            @Override
            public SimpMessagingTemplate getIfAvailable() { return template; }
            @Override
            public SimpMessagingTemplate getIfUnique() { return template; }
            @Override
            public SimpMessagingTemplate getObject() { return template; }
        };
        return new WebSocketMatchService(provider, matchRepository, matchService);
    }

    private Match sampleMatch() {
        Player p1 = new Player();
        p1.setId(1);
        p1.setNickname("p1");
        User u1 = new User();
        u1.setUsername("u1");
        p1.setUser(u1);

        Player p2 = new Player();
        p2.setId(2);
        p2.setNickname("p2");
        User u2 = new User();
        u2.setUsername("u2");
        p2.setUser(u2);

        Match match = new Match();
        match.setId(9);
        match.setCreator(p1);
        match.setPlayer1(p1);
        match.setPlayer2(p2);
        match.setCreatedAt(LocalDateTime.now());
        match.setStartedAt(LocalDateTime.now());
        match.setEndedAt(LocalDateTime.now());
        match.setBoardState(List.of(PetriDish.of(1, 2)));
        return match;
    }

    @Test
    @Story("Lobby snapshot publishing")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("publishLobbySnapshot envía DTO al topic de lobby")
    @Description("Verifies that publishLobbySnapshot publishes the LobbyDTO to /topic/lobby/{id} when a template is available.")
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Feature("HU-01: Unirse a una partida (jugador)")
    void publishLobbySnapshot_sendsWhenTemplatePresent() {
        WebSocketMatchService service = buildServiceWithTemplate(messagingTemplate);
        Match match = sampleMatch();

        service.publishLobbySnapshot(match);

        ArgumentCaptor<LobbyDTO> dtoCaptor = ArgumentCaptor.forClass(LobbyDTO.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/lobby/" + match.getId()), dtoCaptor.capture());
        assertEquals(match.getId(), dtoCaptor.getValue().getId());
    }

    @Test
    @Story("Match snapshot publishing")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("publishMatchSnapshot envía DTO al topic de match")
    @Description("Verifies that publishMatchSnapshot publishes the MatchDTO to /topic/match/{id} when a template is available.")
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Feature("HU-01: Unirse a una partida (jugador)")
    void publishMatchSnapshot_sendsWhenTemplatePresent() {
        WebSocketMatchService service = buildServiceWithTemplate(messagingTemplate);
        Match match = sampleMatch();

        service.publishMatchSnapshot(match);

        ArgumentCaptor<MatchDTO> dtoCaptor = ArgumentCaptor.forClass(MatchDTO.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/match/" + match.getId()), dtoCaptor.capture());
        assertEquals(match.getId(), dtoCaptor.getValue().getId());
    }

    @Test
    @Story("Lobby list publishing")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("publishLobbyList publica listado desde el repositorio")
    @Description("Verifies that publishLobbyList sends the list of pending lobbies to /topic/lobbies.")
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Feature("HU-11: Listado de partidas en curso (administrador)")
    void publishLobbyList_sendsListFromRepo() {
        WebSocketMatchService service = buildServiceWithTemplate(messagingTemplate);
        Match match = sampleMatch();
        match.setStartedAt(null);
        when(matchRepository.findByStartedAtNull()).thenReturn(List.of(match));

        service.publishLobbyList();

        ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/lobbies"), listCaptor.capture());
        assertEquals(1, listCaptor.getValue().size());
    }

    @Test
    @Story("Lobby closed publishing")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("publishLobbyClosed ignora template o id nulos")
    @Description("Verifies that publishLobbyClosed is a no-op when the template or matchId is null.")
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Feature("HU-11: Listado de partidas en curso (administrador)")
    void publishLobbyClosed_ignoresNullTemplateOrId() {
        WebSocketMatchService withoutTemplate = buildServiceWithTemplate(null);
        withoutTemplate.publishLobbyClosed(1); // no throw

        WebSocketMatchService withTemplate = buildServiceWithTemplate(messagingTemplate);
        withTemplate.publishLobbyClosed(null);

        verifyNoInteractions(messagingTemplate);
    }

    @Test
    @Story("Broadcast helpers")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("broadcast helpers llaman a los publish correctos")
    @Description("Verifies that broadcast helper methods invoke the expected publish methods.")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void broadcastHelpers_callExpectedPublishes() {
        WebSocketMatchService service = spy(buildServiceWithTemplate(messagingTemplate));
        Match match = sampleMatch();

        service.broadcastLobbyState(match);
        verify(service).publishLobbySnapshot(match);
        verify(service).publishLobbyList();

        reset(service);
        doNothing().when(service).publishLobbySnapshot(any());
        doNothing().when(service).publishLobbyList();
        doNothing().when(service).publishMatchSnapshot(any());

        service.broadcastLobbyAndMatchState(match);
        verify(service).publishLobbySnapshot(match);
        verify(service).publishLobbyList();
        verify(service).publishMatchSnapshot(match);

        reset(service);
        doNothing().when(service).publishLobbyList();
        doNothing().when(service).publishLobbyClosed(any());

        service.broadcastLobbyClosed(match.getId());
        verify(service).publishLobbyList();
        verify(service).publishLobbyClosed(match.getId());

        reset(service);
        doNothing().when(service).publishMatchSnapshot(any());
        doNothing().when(service).publishLobbyList();

        service.broadcastMatchEnded(match);
        verify(service).publishMatchSnapshot(match);
        verify(service).publishLobbyList();
    }
}
