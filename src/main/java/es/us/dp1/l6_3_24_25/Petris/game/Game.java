package es.us.dp1.l6_3_24_25.Petris.game;

import es.us.dp1.l6_3_24_25.Petris.model.BaseEntity;
import es.us.dp1.l6_3_24_25.Petris.player.Player;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "games")
public class Game extends BaseEntity{

    @Column(name="createdAt")
    private LocalDateTime createdAt;
    @Column(name = "startedAt")
    private LocalDateTime startedAt;
    @Column(name = "endedAt")
    private LocalDateTime endedAt;
    @Column(name = "turns")
    private Integer turns;
    @Column(name = "code")
    private String code;
    @OneToOne
    @JoinColumn(name = "game_creator", nullable = false)
    private Player creator;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="petriDishes")
    private List<PetriDish> petriDish;
    @ManyToOne
    @JoinColumn(name = "player1_name", nullable = false)
    private Player player1;
    @ManyToOne
    @JoinColumn(name = "player2_name", nullable = false)
    private Player player2;

    @OneToOne(cascade = CascadeType.ALL)
    private Score score;
}
