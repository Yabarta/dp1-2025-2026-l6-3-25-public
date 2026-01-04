package es.us.dp1.l6_3_24_25.Petris.player.model;

import es.us.dp1.l6_3_24_25.Petris.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    public Double getScore() {
        if(gamesPlayed < 10) {
            return null;
        }
        double winPercent = ((double) gamesWon / (double) gamesPlayed) * 100.0;
        return winPercent + 20.0 * Math.log10((double) gamesPlayed);
    }
}
