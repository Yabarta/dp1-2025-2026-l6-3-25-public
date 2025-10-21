package es.us.dp1.lx_xy_24_25.your_game_name.player;


import es.us.dp1.lx_xy_24_25.your_game_name.game.model.Match;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
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
    private Set<Match> game;

}
