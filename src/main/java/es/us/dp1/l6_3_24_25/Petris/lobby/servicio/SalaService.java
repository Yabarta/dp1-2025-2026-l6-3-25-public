package es.us.dp1.l6_3_24_25.Petris.lobby.servicio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import es.us.dp1.l6_3_24_25.Petris.lobby.interfaz.Lobby;

@Service
public class SalaService {
    private final Map<String, Lobby> lobbies = new ConcurrentHashMap<>();

    public Lobby createLobby() {
        String codigo = UUID.randomUUID().toString().substring(0, 8);
        Lobby lobby = new Lobby(codigo);
        lobbies.put(codigo, lobby);
        return lobby;
    }

    public Lobby addPlayer(String codigo, String player) {
        Lobby lobby = lobbies.get(codigo);
        if (lobby != null) {
            lobby.getJugadores().add(player);
        }
        return lobby;
    }

    public List<Lobby> getAllLobbies() {
        return new ArrayList<>(lobbies.values());
    }
}