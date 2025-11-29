package es.us.dp1.l6_3_24_25.Petris.friend;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.l6_3_24_25.Petris.player.model.Player;


@Service
public class FriendService {

    @Autowired
    private FriendshipRepository friendRepository;

    @Transactional(readOnly = true)
    public List<Friend> getFriendsByUsername(String username) {
        return friendRepository.findAcceptedFriendships(username);
    }

    @Transactional(readOnly = true)
    public Optional<Friend> getFriendsById(Integer id) {
        return friendRepository.findFriendById(id);
    }

    @Transactional(readOnly = true)
    public List<Friend> getRequests(String username) {
        return friendRepository.findRequestByPlayer(username);
    }

    @Transactional(readOnly = true)
    public List<Friend> getRequester(String username) {
        return friendRepository.findRequesterByPlayer(username);
    }

    @Transactional
    public Friend create(Player requester, Player receiver) {
        Friend newFriend = new Friend();
        newFriend.setRequester(requester);
        newFriend.setReceiver(receiver);
        newFriend.setStatus(FriendshipStatus.PENDING); // O "WAITING"

        return friendRepository.save(newFriend);
    }

    public void save(Friend friend) {
        friendRepository.save(friend);
    }

    @Transactional
    public void delete(Integer id){
        friendRepository.deleteById(id);
    }
}
