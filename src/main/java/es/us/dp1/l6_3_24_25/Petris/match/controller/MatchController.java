package es.us.dp1.l6_3_24_25.Petris.match.controller;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.l6_3_24_25.Petris.exceptions.AccessDeniedException;
import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.service.MatchService;
import es.us.dp1.l6_3_24_25.Petris.match.service.WebSocketMatchService;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import es.us.dp1.l6_3_24_25.Petris.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/matches")
@Tag(name = "Matches", description = "API for the management of Matches")
@SecurityRequirement(name = "bearerAuth")
public class MatchController {

    MatchService matchService;
    WebSocketMatchService webSocketMatchService;
    UserService userService;
    PlayerService playerService;

    public MatchController(MatchService ms,
                           WebSocketMatchService webSocketMatchService,
                           UserService us,
                           PlayerService ps) {
        this.matchService = ms;
        this.webSocketMatchService = webSocketMatchService;
        this.userService = us;
        this.playerService = ps;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Match>> getAllMatches() {
        return new ResponseEntity<>(matchService.getAllMatches(), HttpStatus.OK);
    }

    @GetMapping("/current")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Match>> getCurrentMatches() {
        return new ResponseEntity<>(matchService.getCurrentMatches(), HttpStatus.OK);
    }

    @GetMapping("/notStarted")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Match>> getNotStartedMatches() {
        return new ResponseEntity<>(matchService.getNotStartedMatches(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> getMatchById(@PathVariable(required = true) Integer id) throws ResourceNotFoundException {
        Match match = Objects.requireNonNull(matchService.getMatchById(id));
        return new ResponseEntity<>(match, HttpStatus.OK);
    }

    @GetMapping("/code/{code}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> getMatchByCode(@PathVariable(required = true) String code) throws ResourceNotFoundException {
        Match match = Objects.requireNonNull(matchService.getMatchByCode(code));
        return new ResponseEntity<>(match, HttpStatus.OK);
    }

    private Player getCurrentPlayer() {
        User currentUser = userService.findCurrentUser();
        Player currentPlayer = playerService.getPlayerByUser(currentUser);
        return currentPlayer;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Match> createMatch(@RequestParam(defaultValue = "false") Boolean isPrivate) throws ResponseStatusException {
        Match result;

        try {
            Player currentPlayer = getCurrentPlayer();
            result = matchService.createMatch(currentPlayer, isPrivate);
            playerService.setIsCurrentlyInMatch(currentPlayer, true);
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }

        webSocketMatchService.broadcastLobbyState(Objects.requireNonNull(result));
        
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> joinMatch(@PathVariable(required = true) Integer id,
        @RequestParam(required = false) Optional<String> code)
        throws ResponseStatusException {

        Match result;
        Match matchToUpdate = matchService.getMatchById(id);
        Player currentPlayer = getCurrentPlayer();

        if (matchToUpdate.hasPlayer(currentPlayer)) {
            // Can't join if already in the match. Result is the match without updating it
            result = matchToUpdate;
        } else {
            try {
                result = matchService.joinMatch(matchToUpdate, currentPlayer, code.orElse(null));
            } catch(AccessDeniedException e){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            playerService.setIsCurrentlyInMatch(currentPlayer, true);

            webSocketMatchService.broadcastLobbyAndMatchState(Objects.requireNonNull(result));
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/{id}/leave")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> leaveMatch(@PathVariable(required = true) Integer id) throws ResponseStatusException {
        Match matchToUpdate = matchService.getMatchById(id);
        Player currentPlayer = getCurrentPlayer();

        Match updatedMatch = matchService.leaveMatch(matchToUpdate, currentPlayer);
        currentPlayer.setIsCurrentlyInMatch(false);
        playerService.save(currentPlayer);

        Optional<Match> optionalMatch = Optional.of(updatedMatch);
        if (optionalMatch.isPresent()) {
            webSocketMatchService.broadcastLobbyState(Objects.requireNonNull(optionalMatch.get()));
        } else {
            webSocketMatchService.broadcastLobbyClosed(matchToUpdate.getId());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/start")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> startMatch(@PathVariable(required = true) Integer id) throws ResponseStatusException {
        Match result;
        Match matchToUpdate = matchService.getMatchById(id);
        Player currentPlayer = getCurrentPlayer();

        if (!matchToUpdate.hasCreator(currentPlayer)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the match creator can start the match");
        } else if (matchToUpdate.hasStarted()) {
            // Can't start if already started. Result is the match without updating it
            result = matchToUpdate;
        } else {
            result = matchService.startMatch(matchToUpdate);

            webSocketMatchService.broadcastLobbyAndMatchState(Objects.requireNonNull(result));
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/{id}/nextTurn")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> nextTurn(@PathVariable(required = true) Integer id,
            @Valid @RequestBody(required = false) Optional<List<PetriDish>> newBoardState)
            throws ResponseStatusException {

        Player currentPlayer = getCurrentPlayer();
        Match matchToUpdate = matchService.getMatchById(id);

        if (matchToUpdate.isTurnOf(currentPlayer)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "It's not your turn");
        }
        
        Match updatedMatch = matchService.nextTurn(matchToUpdate, newBoardState.orElse(null));

        if(updatedMatch.hasEnded()) {
            playerService.setIsCurrentlyInMatch(updatedMatch.getPlayer1(), false);
            playerService.setIsCurrentlyInMatch(updatedMatch.getPlayer2(), false);
            webSocketMatchService.broadcastMatchEnded(updatedMatch);
        } else {
            webSocketMatchService.publishMatchSnapshot(updatedMatch);
        }

        return new ResponseEntity<>(updatedMatch, HttpStatus.OK);
    }

    @GetMapping("/{id}/checkErrors")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<String>> checkErrors(@PathVariable(required = true) Integer id,
        @Valid @RequestBody List<PetriDish> newBoardState)
        throws ResponseStatusException {

        Player currentPlayer = getCurrentPlayer();
        Match matchToCheck = matchService.getMatchById(id);
        
        List<String> errors = matchService.checkErrors(matchToCheck, newBoardState, currentPlayer);
        return new ResponseEntity<>(errors, HttpStatus.OK);
    }

    @PutMapping("/{id}/endMatch")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> forceEndMatch(@PathVariable(required = true) Integer id) throws ResponseStatusException {
        Player currentPlayer = getCurrentPlayer();
        Match matchToUpdate = matchService.getMatchById(id);

        Match updatedMatch = matchService.forceEndMatch(matchToUpdate, currentPlayer);

        playerService.setIsCurrentlyInMatch(matchToUpdate.getPlayer1(), false);
        playerService.setIsCurrentlyInMatch(matchToUpdate.getPlayer2(), false);

        webSocketMatchService.broadcastMatchEnded(Objects.requireNonNull(updatedMatch));
        return new ResponseEntity<>(updatedMatch, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteMatch(@PathVariable(required = true) Integer id) throws ResponseStatusException {
        Match matchToDelete = matchService.getMatchById(id);
        
        matchService.delete(id);

        playerService.setIsCurrentlyInMatch(matchToDelete.getPlayer1(), false);
        playerService.setIsCurrentlyInMatch(matchToDelete.getPlayer2(), false);

        webSocketMatchService.broadcastLobbyClosed(id);
        return ResponseEntity.noContent().build();
    }
}
