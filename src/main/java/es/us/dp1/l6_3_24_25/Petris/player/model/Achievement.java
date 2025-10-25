package es.us.dp1.l6_3_24_25.Petris.player.model;

import es.us.dp1.l6_3_24_25.Petris.model.NamedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "achievements")
public class Achievement extends NamedEntity {
    @NotNull
    private String description;

    @NotNull
    private Integer valor;

    @NotNull
    private String statisticName;

    @NotNull
    private String image;

}
