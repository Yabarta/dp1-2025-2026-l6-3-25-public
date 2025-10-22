package es.us.dp1.l6_3_24_25.Petris.match.model;

import es.us.dp1.l6_3_24_25.Petris.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "matches")
public class Match extends BaseEntity{

    private String createdAt;
    private String startedAt;
    private String endedAt;
    private String code;
    private Integer finalP1Score;
    private Integer finalP2Score;
    private Integer winner;
    private Integer turn;
    private TurnType turnType;

    /*
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
    */
}
