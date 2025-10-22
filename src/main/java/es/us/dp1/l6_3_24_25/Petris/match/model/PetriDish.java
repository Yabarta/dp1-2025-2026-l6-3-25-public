package es.us.dp1.l6_3_24_25.Petris.match.model;

import es.us.dp1.l6_3_24_25.Petris.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "petriDishes")
public class PetriDish extends BaseEntity {

    @NotNull
    private Integer index;
    private Integer player1Bacterias;
    private Integer player2Bacterias;
    @NotNull
    private List<Integer> movements;

}
