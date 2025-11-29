package es.us.dp1.l6_3_24_25.Petris.match.model;

import es.us.dp1.l6_3_24_25.Petris.match.util.MatchDataUtil;
import es.us.dp1.l6_3_24_25.Petris.model.BaseEntity;
import io.micrometer.common.lang.NonNull;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "petriDishes")
public class PetriDish extends BaseEntity {
    @NonNull
    @Max(MatchDataUtil.MAX_BACTERIA_PER_PETRI_DISH)
    @Min(0)
    private Integer player1Bacteria = 0;
    @NonNull
    @Max(MatchDataUtil.MAX_BACTERIA_PER_PETRI_DISH)
    @Min(0)
    private Integer player2Bacteria = 0;

    public static PetriDish of(Integer player1Bacteria, Integer player2Bacteria) {
        PetriDish result = new PetriDish();
        result.setPlayer1Bacteria(player1Bacteria);
        result.setPlayer2Bacteria(player2Bacteria);
        return result;
    }

}
