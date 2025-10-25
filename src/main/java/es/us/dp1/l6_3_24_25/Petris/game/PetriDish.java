package es.us.dp1.lx_xy_24_25.your_game_name.game;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "petriDishes")
public class PetriDish extends BaseEntity {

    @Column(name = "player1Bacterias")
    private Integer player1Bacterias;
    @Column(name = "player2Bacterias")
    private Integer player2Bacterias;

}
