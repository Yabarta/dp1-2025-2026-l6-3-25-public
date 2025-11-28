package es.us.dp1.l6_3_24_25.Petris.friend;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FriendController {

    FriendService friendService;

    @Autowired
    public FriendController(FriendService fs){
        this.friendService = fs;
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

    @DeleteMapping("/api/v1/friends/{id}")
    public ResponseEntity<Void> deleteFriendById(@PathVariable Integer id) {
        if (getFriendsById(id) != null) {
            friendService.delete(id);
        }
        return ResponseEntity.noContent().build();
    }
}
