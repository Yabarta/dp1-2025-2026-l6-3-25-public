package es.us.dp1.l6_3_24_25.Petris.player.model;

import es.us.dp1.l6_3_24_25.Petris.model.BaseEntity;
import jakarta.persistence.Column;
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
    @Column(name = "games_played")
    private Integer gamesPlayed = 0;

    @NotNull
    @Column(name = "games_won")
    private Integer gamesWon = 0;

    @NotNull
    @Column(name = "sarcines_created")
    private Integer sarcinesCreated = 0;
}
