package es.us.dp1.l6_3_24_25.Petris.friend;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;

@RestController
public class FriendController {

    FriendService friendService;

    @Autowired
    public FriendController(FriendService fs){
        this.friendService = fs;
    }

    @GetMapping("/api/v1/players/{username}/friends")
    public ResponseEntity<List<Friend>> getFriends(@PathVariable String username) {
        return new ResponseEntity<>(friendService.getFriends(username), HttpStatus.OK);
    }

    @GetMapping("/api/v1/players/{username}/requests")
    public ResponseEntity<List<Friend>> getRequest(@PathVariable String username) {
        return new ResponseEntity<>(friendService.getRequests(username), HttpStatus.OK);
    }
}
