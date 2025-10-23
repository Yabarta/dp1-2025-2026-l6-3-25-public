package es.us.dp1.l6_3_24_25.Petris.match.model;

import es.us.dp1.l6_3_24_25.Petris.model.BaseEntity;
import es.us.dp1.l6_3_24_25.Petris.player.Player;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.Cascade;

@Getter
@Setter
@Entity
@Table(name = "matches")
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


    /* Implementar cuando se haga la relación con Player
    @OneToOne(cascade = CascadeType.PERSIST)
    private Player creator;
    @NotNull
    @OneToMany(cascade = CascadeType.ALL)
    private List<PetriDish> petriDish;

    @ManyToOne(fetch = FetchType.LAZY)
    private Player player1;

    @ManyToOne(fetch = FetchType.LAZY)
    private Player player2;
    */
}
