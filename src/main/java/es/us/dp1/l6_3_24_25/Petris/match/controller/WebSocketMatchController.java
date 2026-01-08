package es.us.dp1.l6_3_24_25.Petris.match.controller;

import java.util.Objects;

import es.us.dp1.l6_3_24_25.Petris.lobby.interfaz.Lobby;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
        summary = "Watch a lobby for updates",
        description = "Subscribe to updates for a specific lobby by providing its match ID.",
        tags = { "lobbies", "watch" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully subscribed to lobby updates", content = @Content(schema = @Schema(implementation = Lobby.class))),
        @ApiResponse(responseCode = "404", description = "Lobby not found", content = @Content(schema = @Schema()))
    })
    @MessageMapping("/lobbies/watch")
    public void watchLobby(@Payload @NonNull Integer matchId) {
        Match match = Objects.requireNonNull(matchService.getMatchById(matchId));
        webSocketMatchService.publishLobbySnapshot(match);
    }

    @Operation(
        summary = "Watch the list of lobbies for updates",
        description = "Subscribe to updates for the list of all lobbies.",
        tags = { "lobbies", "list", "watch" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully subscribed to lobby list updates", content = @Content(schema = @Schema(implementation = Lobby.class))),
        @ApiResponse(responseCode = "404", description = "No lobbies found", content = @Content(schema = @Schema()))
    })
    @MessageMapping("/lobbies/list")
    public void watchLobbyList() {
        webSocketMatchService.publishLobbyList();
    }

    @Operation(
        summary = "Watch a match for updates",
        description = "Subscribe to updates for a specific match by providing its match ID.",
        tags = { "matches", "watch" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully subscribed to match updates", content = @Content(schema = @Schema(implementation = Match.class))),
        @ApiResponse(responseCode = "404", description = "Match not found", content = @Content(schema = @Schema()))
    })
    @MessageMapping("/matches/watch/{matchId}")
    public void watchMatch(@DestinationVariable @NonNull Integer matchId) {
        Match match = Objects.requireNonNull(matchService.getMatchById(matchId));
        webSocketMatchService.publishMatchSnapshot(match);
    }
}
