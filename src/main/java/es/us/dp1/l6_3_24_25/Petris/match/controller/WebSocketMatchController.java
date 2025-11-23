package es.us.dp1.l6_3_24_25.Petris.match.controller;

import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.service.MatchService;
import es.us.dp1.l6_3_24_25.Petris.match.service.WebSocketMatchService;

@Controller
public class WebSocketMatchController {

    private final MatchService matchService;
    private final WebSocketMatchService webSocketMatchService;

    public WebSocketMatchController(final MatchService matchService,
                                    final WebSocketMatchService webSocketMatchService) {
        this.matchService = matchService;
        this.webSocketMatchService = webSocketMatchService;
    }

    @MessageMapping("/lobbies/watch")
    public void watchLobby(@Payload @NonNull Integer matchId) {
        Match match = Objects.requireNonNull(matchService.getMatchById(matchId));
        webSocketMatchService.publishLobbySnapshot(match);
    }

    @MessageMapping("/lobbies/list")
    public void watchLobbyList() {
        webSocketMatchService.publishLobbyList();
    }

    @MessageMapping("/matches/watch/{matchId}")
    public void watchMatch(@DestinationVariable @NonNull Integer matchId) {
        Match match = Objects.requireNonNull(matchService.getMatchById(matchId));
        webSocketMatchService.publishMatchSnapshot(match);
    }
}
