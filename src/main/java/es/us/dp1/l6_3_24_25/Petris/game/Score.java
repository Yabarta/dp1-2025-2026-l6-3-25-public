package es.us.dp1.l6_3_24_25.Petris.game;

import es.us.dp1.l6_3_24_25.Petris.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "scores")
public class Score extends BaseEntity {

    @Column(name = "score")
    private Integer points;

}
