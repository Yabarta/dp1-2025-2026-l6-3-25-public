package es.us.dp1.l6_3_24_25.Petris.player.model;


import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.model.BaseEntity;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "players")
public class Player extends BaseEntity{

    private String nickname;
    private String email;
    @OneToOne(cascade = CascadeType.ALL)
    private User user;
    @OneToMany
    private Set<Match> game;
    @ManyToMany
    private Set<Achievement> achievements;
    @OneToMany(cascade = CascadeType.ALL)
    private Set<Statistics> statistics;

}
