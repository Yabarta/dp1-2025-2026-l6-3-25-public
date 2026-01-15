package es.us.dp1.l6_3_24_25.Petris.player.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import es.us.dp1.l6_3_24_25.Petris.model.BaseEntity;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private User user;
    @NotAudited
    @Builder.Default
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Achievement> achievements = new ArrayList<>();
    @NotAudited
    @NotNull
    @OneToOne
    private Statistics statistics;

    @NotAudited
    @Builder.Default
    private Boolean isOnline = false;

    @NotAudited
    @Builder.Default
	private LocalDateTime lastLogin = LocalDateTime.now();
}
