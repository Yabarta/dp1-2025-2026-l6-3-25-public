package es.us.dp1.l6_3_24_25.Petris.player.model;

import java.util.ArrayList;
import java.util.List;

import es.us.dp1.l6_3_24_25.Petris.model.BaseEntity;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "players")
public class Player extends BaseEntity{

    private String nickname;
    private String email;
    private String profilePicture;
    @NotNull
    private Boolean isCurrentlyInMatch;
    @NotNull
    @OneToOne
    private User user;
    @ManyToMany
    private List<Achievement> achievements = new ArrayList<>();
    @NotNull
    @OneToOne
    private Statistics statistics;

}
