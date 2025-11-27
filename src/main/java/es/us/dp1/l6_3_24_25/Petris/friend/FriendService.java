package es.us.dp1.l6_3_24_25.Petris.friend;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FriendService {

    @Autowired
    private FriendshipRepository friendRepository;

    @Transactional(readOnly = true)
    public List<Friend> getFriends(String username) {
        return friendRepository.findAcceptedFriendships(username);
    }
    @Transactional(readOnly = true)
    public List<Friend> getRequests(String username) {
        return friendRepository.findRequestByPlayer(username);
    }
}
