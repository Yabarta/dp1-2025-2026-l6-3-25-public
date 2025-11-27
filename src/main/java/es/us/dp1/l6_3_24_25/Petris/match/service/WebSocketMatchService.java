package es.us.dp1.l6_3_24_25.Petris.match.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import es.us.dp1.l6_3_24_25.Petris.match.dto.LobbyDTO;
import es.us.dp1.l6_3_24_25.Petris.match.dto.MatchDTO;
import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.repository.MatchRepository;

@Service
public class WebSocketMatchService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MatchRepository matchRepository;

    public WebSocketMatchService(final ObjectProvider<SimpMessagingTemplate> messagingTemplateProvider,
                                 final MatchRepository matchRepository,
                                 final MatchService matchService) {
        this.messagingTemplate = messagingTemplateProvider.getIfAvailable();
        this.matchRepository = matchRepository;
    }

    public void publishLobbySnapshot(@NonNull Match match) {
        if (messagingTemplate == null) {
            return;
        }
        Match ensuredMatch = Objects.requireNonNull(match, "match");
        LobbyDTO lobbySnapshot = Objects.requireNonNull(LobbyDTO.toLobbyDTO(ensuredMatch));
        messagingTemplate.convertAndSend("/topic/lobby/" + ensuredMatch.getId(), lobbySnapshot);
    }

    public void publishLobbyList() {
        if (messagingTemplate == null) {
            return;
        }
        List<LobbyDTO> lobbies = matchRepository.findByStartedAtNull().stream()
            .map(LobbyDTO::toLobbyDTO)
            .toList();
        messagingTemplate.convertAndSend("/topic/lobbies", Objects.requireNonNull(List.copyOf(lobbies)));
    }

    public void publishMatchSnapshot(@NonNull Match match) {
        if (messagingTemplate == null) {
            return;
        }
        Match ensuredMatch = Objects.requireNonNull(match, "match");
        MatchDTO matchSnapshot = Objects.requireNonNull(MatchDTO.toMatchDTO(ensuredMatch));
        messagingTemplate.convertAndSend("/topic/match/" + ensuredMatch.getId(), matchSnapshot);
    }

    public void publishLobbyClosed(@Nullable Integer matchId) {
        if (messagingTemplate == null || matchId == null) {
            return;
        }
        messagingTemplate.convertAndSend("/topic/lobby/" + matchId, "LOBBY_CLOSED");
    }

    public void broadcastLobbyState(@NonNull Match match) {
        publishLobbySnapshot(match);
        publishLobbyList();
    }

    public void broadcastLobbyAndMatchState(@NonNull Match match) {
        publishLobbySnapshot(match);
        publishLobbyList();
        publishMatchSnapshot(match);
    }

    public void broadcastLobbyClosed(@Nullable Integer matchId) {
        publishLobbyList();
        publishLobbyClosed(matchId);
    }

    public void broadcastMatchEnded(@NonNull Match match) {
        publishMatchSnapshot(match);
        publishLobbyList();
    }
}
