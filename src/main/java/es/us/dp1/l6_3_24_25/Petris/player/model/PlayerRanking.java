package es.us.dp1.l6_3_24_25.Petris.player.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerRanking {
    Integer rankingPosition;
    String nickname;
    Integer partidasJugadas;
    Integer partidasGanadas;
    Integer sarcinasCreadas;
    Double score;
    String profilePicture;
}
