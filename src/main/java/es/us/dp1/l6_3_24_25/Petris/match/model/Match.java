package es.us.dp1.l6_3_24_25.Petris.match.model;

import es.us.dp1.l6_3_24_25.Petris.model.BaseEntity;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "matches")
public class Match extends BaseEntity{
    @NotNull
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String code;
    @Min(0)
    @Max(9)
    private Integer player1Score;
    @Min(0)
    @Max(9)
    private Integer player2Score;
    @Min(1)
    @Max(2)
    private Integer winner;
    private Integer turn;
    private TurnType turnType;

    @NotNull
    @OneToOne(cascade = CascadeType.PERSIST)
    private Player creator;

    @OneToMany(cascade = CascadeType.ALL)
    private List<PetriDish> boardState;

    @ManyToOne()
    private Player player1;

    @ManyToOne()
    private Player player2;

}
