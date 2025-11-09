package es.us.dp1.l6_3_24_25.Petris.player.model;

import es.us.dp1.l6_3_24_25.Petris.model.NamedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "statistics")
public class Statistics extends NamedEntity {

    @NotNull
    private String name;

    @NotNull
    private Integer valor;
}
