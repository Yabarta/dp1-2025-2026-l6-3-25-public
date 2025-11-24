package es.us.dp1.l6_3_24_25.Petris.player.model;


import java.util.List;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.model.BaseEntity;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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
    @OneToOne()
    private User user;
    @OneToMany
    private List<Match> game;
    @ManyToMany
    private List<Achievement> achievements;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Statistics> statistics;

}
