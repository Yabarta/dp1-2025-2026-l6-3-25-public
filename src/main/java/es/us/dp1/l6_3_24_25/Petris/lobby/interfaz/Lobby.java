package es.us.dp1.l6_3_24_25.Petris.lobby.interfaz;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Lobby {
    private String id;
    private String codigoDeUnion;
    private List<String> jugadores;

    public Lobby(String codigoDeUnion) {
        this.id = codigoDeUnion;
        this.codigoDeUnion = codigoDeUnion;
        this.jugadores = new ArrayList<>();
    }
}
