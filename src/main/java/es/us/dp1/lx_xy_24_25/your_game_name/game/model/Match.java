package es.us.dp1.lx_xy_24_25.your_game_name.game.model;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "games")
public class Match extends BaseEntity{

    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String code;
    private Integer finalP1Score;
    private Integer finalP2Score;
    private Integer winner;
    private Integer turn;
    private TurnType turnType;

    @NotNull
    @OneToOne
    private Player creator;
    @NotNull
    @OneToMany(cascade = CascadeType.ALL)
    private List<PetriDish> petriDish;
    @NotNull
    @ManyToOne
    private Player player1;
    @ManyToOne
    private Player player2;

}
