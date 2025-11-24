package es.us.dp1.l6_3_24_25.Petris.match.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import es.us.dp1.l6_3_24_25.Petris.match.model.TurnType;
import lombok.Data;

@Data
public class MatchDTO {
    private Integer id;
    private String code;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Integer turn;
    private TurnType turnType;
    private Integer player1Score;
    private Integer player2Score;
    private Integer winner;
    private PlayerSummaryDTO player1;
    private PlayerSummaryDTO player2;
    private List<PetriDishDTO> board = new ArrayList<>();
}
