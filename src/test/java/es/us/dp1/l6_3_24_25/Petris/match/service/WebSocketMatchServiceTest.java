package es.us.dp1.l6_3_24_25.Petris.match.service;

import es.us.dp1.l6_3_24_25.Petris.match.dto.LobbyDTO;
import es.us.dp1.l6_3_24_25.Petris.match.dto.MatchDTO;
import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.repository.MatchRepository;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import io.qameta.allure.Owner;
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
import static org.mockito.Mockito.*;

@Owner("DiegoVicenteCamara(RXW1249)")
@ExtendWith(MockitoExtension.class)
class WebSocketMatchServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MatchService matchService; // unused in service but required by ctor

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
    void publishLobbySnapshot_sendsWhenTemplatePresent() {
        WebSocketMatchService service = buildServiceWithTemplate(messagingTemplate);
        Match match = sampleMatch();

        service.publishLobbySnapshot(match);

        ArgumentCaptor<LobbyDTO> dtoCaptor = ArgumentCaptor.forClass(LobbyDTO.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/lobby/" + match.getId()), dtoCaptor.capture());
        assertEquals(match.getId(), dtoCaptor.getValue().getId());
    }

    @Test
    void publishMatchSnapshot_sendsWhenTemplatePresent() {
        WebSocketMatchService service = buildServiceWithTemplate(messagingTemplate);
        Match match = sampleMatch();

        service.publishMatchSnapshot(match);

        ArgumentCaptor<MatchDTO> dtoCaptor = ArgumentCaptor.forClass(MatchDTO.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/match/" + match.getId()), dtoCaptor.capture());
        assertEquals(match.getId(), dtoCaptor.getValue().getId());
    }

    @Test
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
    void publishLobbyClosed_ignoresNullTemplateOrId() {
        WebSocketMatchService withoutTemplate = buildServiceWithTemplate(null);
        withoutTemplate.publishLobbyClosed(1); // no throw

        WebSocketMatchService withTemplate = buildServiceWithTemplate(messagingTemplate);
        withTemplate.publishLobbyClosed(null);

        verifyNoInteractions(messagingTemplate);
    }

    @Test
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
