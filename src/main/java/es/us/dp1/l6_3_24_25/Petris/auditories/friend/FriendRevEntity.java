package es.us.dp1.l6_3_24_25.Petris.auditories.friend;


import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RevisionEntity(FriendRevisionListener.class)
public class FriendRevEntity extends DefaultRevisionEntity {
    private String requestedBy;
    private String receivedBy;
}
