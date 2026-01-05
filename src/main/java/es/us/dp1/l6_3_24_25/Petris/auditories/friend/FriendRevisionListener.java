package es.us.dp1.l6_3_24_25.Petris.auditories.friend;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class FriendRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        FriendRevEntity friendRevEntity = (FriendRevEntity) revisionEntity;
        String modifiedBy = "";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        if(principal != null && principal instanceof UserDetails)
            modifiedBy = ((UserDetails) principal).getUsername();
        else if(principal != null)
            modifiedBy = principal.toString();
        friendRevEntity.setModifiedBy(modifiedBy);
    }
}
