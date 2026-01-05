package es.us.dp1.l6_3_24_25.Petris.lobby.controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.l6_3_24_25.Petris.lobby.interfaz.Lobby;
import es.us.dp1.l6_3_24_25.Petris.lobby.servicio.SalaService;

@RestController
public class SalaController {

    @Autowired
    private SalaService salaService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Operation(
        summary = "Create a new lobby",
        description = "Creates a new game lobby and returns its details",
        tags = { "lobbies", "create" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lobby created successfully", content = {
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Lobby.class)
            )
        }),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema))
    })
    @PostMapping("/api/salas")
    public ResponseEntity<Lobby> createLobby() {
        Lobby lobby = salaService.createLobby();
        return ResponseEntity.ok(lobby);
    }

    @Operation(
        summary = "Join a lobby",
        description = "Allows a player to join an existing game lobby using its code",
        tags = { "lobbies", "join" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Joined lobby successfully", content = {
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Lobby.class)
            )
        }),
        @ApiResponse(responseCode = "404", description = "Lobby not found", content = @Content(schema = @Schema))
    })
    @PostMapping("/api/salas/{codigo}/unirse")
    public ResponseEntity<Lobby> joinLobby(@PathVariable String codigo, @RequestBody String player) {
        Lobby lobby = salaService.addPlayer(codigo, player);
        if (lobby != null) {
            messagingTemplate.convertAndSend("/topic/sala/" + codigo, lobby.getJugadores());
            return ResponseEntity.ok(lobby);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
