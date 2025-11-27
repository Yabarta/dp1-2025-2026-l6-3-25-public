package es.us.dp1.l6_3_24_25.Petris.friend;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;

public class FriendController {

    FriendService friendService;

    @Autowired
    public FriendController(FriendService fs){
        this.friendService = fs;
    }

    @GetMapping("/api/v1/players/friends")
    public ResponseEntity<List<Friend>> getAllPlayers() {
        return new ResponseEntity<>(friendService.getAllFriendships(), HttpStatus.OK);
    }
}
