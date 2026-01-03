package es.us.dp1.l6_3_24_25.Petris.player.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerRanking {
    Integer rankingPosition;
    String nickname;
    Integer partidasJugadas;
    Integer partidasGanadas;
    Integer sarcinasCreadas;
    Double score;
}
