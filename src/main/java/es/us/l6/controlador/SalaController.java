package es.us.l6.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import es.us.l6.interfaz.Lobby;
import es.us.l6.servicio.SalaService;

@RestController
public class SalaController {

    @Autowired
    private SalaService salaService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/api/salas")
    public ResponseEntity<Lobby> createLobby() {
        Lobby lobby = salaService.createLobby();
        return ResponseEntity.ok(lobby);
    }

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
