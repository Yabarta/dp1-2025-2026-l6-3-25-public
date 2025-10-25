package es.us.dp1.l6_3_24_25.Petris.game;

import es.us.dp1.l6_3_24_25.Petris.model.BaseEntity;
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
