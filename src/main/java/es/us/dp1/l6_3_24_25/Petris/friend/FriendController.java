package es.us.dp1.l6_3_24_25.Petris.friend;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.service.MatchService;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/v1")
@RestController
public class FriendController {

    FriendService friendService;
    PlayerService playerService;
    MatchService matchService;

    @Autowired
    public FriendController(FriendService fs, PlayerService ps, MatchService ms){
        this.friendService = fs;
        this.playerService = ps;
        this.matchService = ms;
    }

    @Operation(
        summary = "Retrieve a friend by ID",
        description = "Get a friend by their unique ID",
        tags = { "friends", "get by id" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Friend found", content = { @Content(schema = @Schema(implementation = Friend.class))}),
        @ApiResponse(responseCode = "404", description = "Friend not found")
    })
    @GetMapping("/players/friends/{id}")
    public ResponseEntity<Optional<Friend>> getFriendsById(@PathVariable Integer id) {
        return new ResponseEntity<>(friendService.getFriendsById(id), HttpStatus.OK);
    }

    @Operation(
        summary = "Retrieve friends by username",
        description = "Get a list of friends for a specific player by their username",
        tags = { "friends", "get by username" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Friends found", content = { @Content(schema = @Schema(implementation = Friend.class))}),
        @ApiResponse(responseCode = "404", description = "Friends not found")
    })
    @GetMapping("/players/{username}/friends")
    public ResponseEntity<List<Friend>> getFriendsByUsername(@PathVariable String username) {
        return new ResponseEntity<>(friendService.getFriendsByUsername(username), HttpStatus.OK);
    }

    @Operation(
        summary = "Retrieve friend requests for a player",
        description = "Get a list of friend requests received by a specific player",
        tags = { "friends", "get requests" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Friend requests found", content = { @Content(schema = @Schema(implementation = Friend.class))}),
        @ApiResponse(responseCode = "404", description = "Friend requests not found")
    })
    @GetMapping("/players/{username}/requests")
    public ResponseEntity<List<Friend>> getRequest(@PathVariable String username) {
        return new ResponseEntity<>(friendService.getRequests(username), HttpStatus.OK);
    }

    @Operation(
        summary = "Retrieve sent friend requests by a player",
        description = "Get a list of friend requests sent by a specific player",
        tags = { "friends", "get sent requests" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sent friend requests found", content = { @Content(schema = @Schema(implementation = Friend.class))}),
        @ApiResponse(responseCode = "404", description = "Sent friend requests not found")
    })
    @GetMapping("/players/{username}/requester")
    public ResponseEntity<List<Friend>> getRequester(@PathVariable String username) {
        return new ResponseEntity<>(friendService.getRequester(username), HttpStatus.OK);
    }

    @Operation(
        summary = "Create a new friend request",
        description = "Send a friend request from one player to another",
        tags = { "friends", "post" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Friend request created", content = { @Content(schema = @Schema(implementation = Friend.class))}),
        @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping("/players/friends")
    public ResponseEntity<Friend> createFriend(@Valid @RequestBody Map<String, String> body) {

        String requester = (String) body.get("requester");
        String receiver = (String) body.get("receiver");
        Player req = playerService.getPlayerByUsername(requester);
        Player rec = playerService.getPlayerByUsername(receiver);
        Friend created = friendService.create(req, rec);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Accept a friend request",
        description = "Accept a pending friend request by its ID",
        tags = { "friends", "put" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Friend request accepted", content = { @Content(schema = @Schema(implementation = Friend.class))}),
        @ApiResponse(responseCode = "404", description = "Friend request not found")
    })
    @PutMapping("/players/friends/{id}")
    public ResponseEntity<Friend> acceptFriend(@PathVariable Integer id) {
        Friend friend = friendService.getFriendsById(id).orElse(null);
        if (friend == null) {
            return ResponseEntity.notFound().build();
        }
        friend.setStatus(FriendshipStatus.ACCEPTED);
        friendService.save(friend);
        return new ResponseEntity<>(friend, HttpStatus.OK);
    }

    @Operation(
        summary = "Delete a friend by ID",
        description = "Delete a friend relationship by its unique ID",
        tags = { "friends", "delete" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Friend deleted"),
        @ApiResponse(responseCode = "404", description = "Friend not found")
    })
    @DeleteMapping("/friends/{id}")
    public ResponseEntity<Void> deleteFriendById(@PathVariable Integer id) {
        if (getFriendsById(id) != null) {
            friendService.delete(id);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/friends/espectate")
    public ResponseEntity<List<Match>> getFriendMatch(@RequestParam Integer idPlayer ) {
        List<Match> matchesFriends = new java.util.ArrayList<>();
        List<Match> matches = matchService.getCurrentMatches();
        for (Match match : matches) {
        if (match != null && friendService.Player1IsFriendOfPlayer2(idPlayer , match.getPlayer1().getId()) && 
            friendService.Player1IsFriendOfPlayer2(idPlayer , match.getPlayer2().getId())) {
                matchesFriends.add(match);
        }
        }
        return ResponseEntity.ok(matchesFriends);
    }
    
}
