package es.us.dp1.l6_3_24_25.Petris.friend;

import es.us.dp1.l6_3_24_25.Petris.model.BaseEntity;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Audited
@Setter
@Getter
@Entity
public class Friend extends BaseEntity {
    @ManyToOne
    @NotNull
    private Player requester;
    @ManyToOne
    @NotNull
    private Player receiver;
    private FriendshipStatus status;
}
