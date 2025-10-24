package es.us.dp1.l6_3_24_25.Petris.player.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "achievement")
public class Achievement {

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private Object value;

    @NotNull
    private String statisticName;

    @NotNull
    private String image;

}