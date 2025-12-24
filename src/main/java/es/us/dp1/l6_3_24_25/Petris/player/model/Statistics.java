package es.us.dp1.l6_3_24_25.Petris.player.model;

import es.us.dp1.l6_3_24_25.Petris.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "statistics")
public class Statistics extends BaseEntity {

    @NotNull
    private Integer gamesPlayed ;

    @NotNull
    private Integer gamesWon ;

    @NotNull
    private Integer timePlayed ;

    @NotNull
    private Integer sarcinasCreated ;

    @NotNull
    private Integer bacteriasCreated ;
}
