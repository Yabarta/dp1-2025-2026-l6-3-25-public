package es.us.dp1.l6_3_24_25.Petris.player.model;


import java.util.ArrayList;
import java.util.List;

import es.us.dp1.l6_3_24_25.Petris.friend.Friend;
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
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Getter
@Setter
@Entity
@Table(name = "players")
@Audited
public class Player extends BaseEntity{

    private String nickname;
    @NotAudited
    private String email;
    @NotAudited
    private String profilePicture;
    @NotAudited
    @NotNull
    private Boolean isCurrentlyInMatch;
    @NotAudited
    @NotNull
    @OneToOne()
    private User user;
    @NotAudited
    @ManyToMany
    private List<Achievement> achievements = new ArrayList<>();
    @NotAudited
    @NotNull
    @OneToMany(cascade = CascadeType.ALL)
    private List<Statistics> statistics = new ArrayList<>();

}
