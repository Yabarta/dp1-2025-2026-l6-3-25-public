package es.us.dp1.l6_3_24_25.Petris.friend;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import jakarta.validation.Valid;

@RestController
public class FriendController {

    FriendService friendService;
    PlayerService playerService;

    @Autowired
    public FriendController(FriendService fs, PlayerService ps){
        this.friendService = fs;
        this.playerService = ps;
    }

    @GetMapping("/api/v1/players/friends/{id}")
    public ResponseEntity<Optional<Friend>> getFriendsById(@PathVariable Integer id) {
        return new ResponseEntity<>(friendService.getFriendsById(id), HttpStatus.OK);
    }

    @GetMapping("/api/v1/players/{username}/friends")
    public ResponseEntity<List<Friend>> getFriendsByUsername(@PathVariable String username) {
        return new ResponseEntity<>(friendService.getFriendsByUsername(username), HttpStatus.OK);
    }

    @GetMapping("/api/v1/players/{username}/requests")
    public ResponseEntity<List<Friend>> getRequest(@PathVariable String username) {
        return new ResponseEntity<>(friendService.getRequests(username), HttpStatus.OK);
    }

    @PostMapping("/api/v1/players/friends")
    public ResponseEntity<Friend> createFriend(@Valid @RequestBody Map<String, String> body) {

        String requester = (String) body.get("requester");
        String receiver = (String) body.get("receiver");
        Player req = playerService.getPlayerByUsername(requester);
        Player rec = playerService.getPlayerByUsername(receiver);
        Friend created = friendService.create(req, rec);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/api/v1/players/friends/{id}")
    public ResponseEntity<Friend> acceptFriend(@PathVariable Integer id) {
        Friend friend = friendService.getFriendsById(id).orElse(null);
        if (friend == null) {
            return ResponseEntity.notFound().build();
        }
        friend.setStatus(FriendshipStatus.ACCEPTED);
        friendService.save(friend);
        return new ResponseEntity<>(friend, HttpStatus.OK);
    }

    @DeleteMapping("/api/v1/friends/{id}")
    public ResponseEntity<Void> deleteFriendById(@PathVariable Integer id) {
        if (getFriendsById(id) != null) {
            friendService.delete(id);
        }
        return ResponseEntity.noContent().build();
    }
}
