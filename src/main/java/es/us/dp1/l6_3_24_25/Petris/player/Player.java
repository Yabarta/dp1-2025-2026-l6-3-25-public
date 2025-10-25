package es.us.dp1.l6_3_24_25.Petris.player;


import es.us.dp1.l6_3_24_25.Petris.game.Game;
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

    @Column(unique = true, name = "nickname")
    private String nickname;
    @Column(unique = true, name = "email")
    private String email;
    @OneToOne(cascade = CascadeType.ALL)
    private User user;
    @OneToMany
    private Set<Game> game;

}
