package es.us.dp1.l6_3_24_25.Petris.match.controller;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.us.dp1.l6_3_24_25.Petris.exceptions.AccessDeniedException;
import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.model.TurnType;
import es.us.dp1.l6_3_24_25.Petris.match.service.MatchService;
import es.us.dp1.l6_3_24_25.Petris.match.service.WebSocketMatchService;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import es.us.dp1.l6_3_24_25.Petris.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/matches")
@Tag(name = "Matches", description = "API for the management of Matches")
@SecurityRequirement(name = "bearerAuth")
public class MatchController {

    MatchService matchService;
    WebSocketMatchService webSocketMatchService;
    UserService userService;
    PlayerService playerService;

    @Autowired
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
    public ResponseEntity<Match> getMatchById(@PathVariable("id") @NonNull Integer id) throws ResourceNotFoundException {
        Match match = Objects.requireNonNull(matchService.getMatchById(id));
        return new ResponseEntity<>(match, HttpStatus.OK);
    }

    @GetMapping("/code/{code}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> getMatchByCode(@PathVariable("code") @NonNull String code) throws ResourceNotFoundException {
        Match match = Objects.requireNonNull(matchService.getMatchByCode(code));
        return new ResponseEntity<>(match, HttpStatus.OK);
    }

    // TODO Eliminar ¿En qué situación necesitamos esta petición?
    @GetMapping("/{id}/{dishIndex}")
    public PetriDish getPetriDish(@PathVariable("id") @NonNull Integer id, @PathVariable("dishIndex") @NonNull Integer index) {
        Match match = Objects.requireNonNull(matchService.getMatchById(id));
        return match.getBoardState().get(index);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Match> createMatch(@RequestParam(value = "isPrivate", defaultValue = "false") Boolean isPrivate)
            throws AccessDeniedException {
        User currentUser = userService.findCurrentUser();
        Player currentPlayer = playerService.getPlayerByUser(currentUser);
        if (Boolean.TRUE.equals(currentPlayer.getIsCurrentlyInMatch())) {
            throw new AccessDeniedException("Already in a match");
        }
        Match match = new Match();
        String code = null;
        if (isPrivate) {
            code = matchService.generateLobbyCode();
        }
        match.setCode(code);
        match.setCreator(currentPlayer);
        match.setPlayer1(currentPlayer);
        match = matchService.createMatch(match);
        webSocketMatchService.broadcastLobbyState(Objects.requireNonNull(match));
        currentPlayer.setIsCurrentlyInMatch(true);
        playerService.save(currentPlayer);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(match.getId())
            .toUri();
        return ResponseEntity.created(location).body(match);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> joinMatch(@PathVariable("id") @NonNull Integer id,
                                           @RequestParam(value = "code", required = false) Optional<String> code)
            throws AccessDeniedException {
        Match matchToUpdate = Objects.requireNonNull(matchService.getMatchById(id));
        if(matchToUpdate.getEndedAt() != null) {
            throw new AccessDeniedException("The match has already ended");
        }

        if(matchToUpdate.getStartedAt() != null) {
            throw new AccessDeniedException("The match has already started");
        }

        String providedCode = code.orElse(null);
        if(matchToUpdate.getCode() != null && !matchToUpdate.getCode().equalsIgnoreCase(providedCode)) {
            throw new AccessDeniedException("Incorrect code for private match");
        }
        User currentUser = userService.findCurrentUser();
        Player currentPlayer = playerService.getPlayerByUser(currentUser);
        if (currentPlayer.equals(matchToUpdate.getPlayer1()) || currentPlayer.equals(matchToUpdate.getPlayer2())) {
            return new ResponseEntity<>(matchToUpdate, HttpStatus.OK);
        }
        if (Boolean.TRUE.equals(currentPlayer.getIsCurrentlyInMatch())) {
            throw new AccessDeniedException("Already in a match");
        }
        if (matchToUpdate.getPlayer2() != null) {
            throw new AccessDeniedException("The match is already full");
        }
        matchToUpdate.setPlayer2(currentPlayer);
        currentPlayer.setIsCurrentlyInMatch(true);
        playerService.save(currentPlayer);

        Match joined = matchService.joinMatch(matchToUpdate);
        webSocketMatchService.broadcastLobbyAndMatchState(Objects.requireNonNull(joined));
        return new ResponseEntity<>(joined, HttpStatus.OK);
    }

    @PutMapping("/{id}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> leaveMatch(@PathVariable("id") @NonNull Integer id) throws AccessDeniedException {
        Match match = Objects.requireNonNull(matchService.getMatchById(id));
        if (match.getEndedAt() != null) {
            throw new AccessDeniedException("The match has already ended");
        }
        if (match.getStartedAt() != null) {
            throw new AccessDeniedException("The match has already started");
        }
        User currentUser = userService.findCurrentUser();
        Player currentPlayer = playerService.getPlayerByUser(currentUser);
        Player p1 = matchService.getMatchById(id).getPlayer1();
        Player p2 = matchService.getMatchById(id).getPlayer2();
        p1.setIsCurrentlyInMatch(false);
        if (p2 != null) {
            p2.setIsCurrentlyInMatch(false);
        }
        if (!currentPlayer.equals(match.getPlayer1()) && !currentPlayer.equals(match.getPlayer2())) {
            throw new AccessDeniedException("You're not part of this lobby");
        }
        Optional<Match> optionalMatch = matchService.leaveMatch(match, currentPlayer);
        currentPlayer.setIsCurrentlyInMatch(false);
        playerService.save(currentPlayer);
        if (optionalMatch.isPresent()) {
            webSocketMatchService.broadcastLobbyState(Objects.requireNonNull(optionalMatch.get()));
        } else {
            webSocketMatchService.broadcastLobbyClosed(match.getId());
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/start")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> startMatch(@PathVariable("id") @NonNull Integer id) throws AccessDeniedException {
        Match match = Objects.requireNonNull(matchService.getMatchById(id));
        if (match.getEndedAt() != null) {
            throw new AccessDeniedException("The match has already ended");
        }
        if (match.getStartedAt() != null) {
            return new ResponseEntity<>(match, HttpStatus.OK);
        }
        if (match.getPlayer1() == null || match.getPlayer2() == null) {
            throw new AccessDeniedException("Two players are required to start the match");
        }
        User currentUser = userService.findCurrentUser();
        Player currentPlayer = playerService.getPlayerByUser(currentUser);
        if (!currentPlayer.equals(match.getCreator())) {
            throw new AccessDeniedException("Only the lobby creator can start the match");
        }
        Match started = matchService.startMatch(match);
        webSocketMatchService.broadcastLobbyAndMatchState(Objects.requireNonNull(started));
        return new ResponseEntity<>(started, HttpStatus.OK);
    }

    @PutMapping("/{id}/nextTurn")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> nextTurn(@Valid @RequestBody(required = false) List<PetriDish> newBoardState, @PathVariable("id") @NonNull Integer id)
            throws AccessDeniedException {
        Match matchToUpdate = Objects.requireNonNull(matchService.getMatchById(id));
        if(matchToUpdate.getEndedAt() != null) {
            throw new AccessDeniedException("The match has already ended");
        }

        User currentUser = userService.findCurrentUser();
        Player currentPlayer = playerService.getPlayerByUser(currentUser);
        if (!currentPlayer.equals(matchToUpdate.getPlayer1()) && matchToUpdate.getTurnType().equals(TurnType.P1_PROPAGATION) ||
            !currentPlayer.equals(matchToUpdate.getPlayer2()) && matchToUpdate.getTurnType().equals(TurnType.P2_PROPAGATION)) {
                throw new AccessDeniedException("It's not your turn");
        }
        
        Match updatedMatch = matchService.nextTurn(matchToUpdate, Optional.ofNullable(newBoardState));
        if(updatedMatch.getEndedAt() != null) {
            Player player1 = updatedMatch.getPlayer1();
            Player player2 = updatedMatch.getPlayer2();
            player1.setIsCurrentlyInMatch(false);
            player2.setIsCurrentlyInMatch(false);
            playerService.save(player1);
            playerService.save(player2);
            webSocketMatchService.broadcastMatchEnded(updatedMatch);
        } else {
            webSocketMatchService.publishMatchSnapshot(updatedMatch);
        }

        return new ResponseEntity<>(updatedMatch, HttpStatus.OK);
    }

    @GetMapping("/{id}/checkErrors")
    public List<String> getPropagationErrors(@PathVariable("id") @NonNull Integer id, @Valid @RequestParam List<PetriDish> newBoardState)
            throws AccessDeniedException{
        User currentUser = userService.findCurrentUser();
        Player currentPlayer = playerService.getPlayerByUser(currentUser);
        Match match = Objects.requireNonNull(matchService.getMatchById(id));
        int player;
        if(currentPlayer.equals(match.getPlayer1())) {
            player = 1;
        } else if(currentPlayer.equals(match.getPlayer2())) {
            player = 2;
        } else {
            throw new AccessDeniedException();
        }
        return matchService.getPropagationErrors(match.getBoardState(), newBoardState, player);
    }

    @PutMapping("/{id}/endMatch")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> forceEndMatch(@PathVariable("id") @NonNull Integer id)
            throws AccessDeniedException {
        User currentUser = userService.findCurrentUser();
        Player currentPlayer = playerService.getPlayerByUser(currentUser);
        Player p1 = matchService.getMatchById(id).getPlayer1();
        Player p2 = matchService.getMatchById(id).getPlayer2();
        p1.setIsCurrentlyInMatch(false);
        p2.setIsCurrentlyInMatch(false);
        Match matchToUpdate = Objects.requireNonNull(matchService.getMatchById(id));
        if(currentPlayer.equals(matchToUpdate.getPlayer1())) {
            matchToUpdate.setWinner(2);
        } else if(currentPlayer.equals(matchToUpdate.getPlayer2())) {
            matchToUpdate.setWinner(1);
        } else {
            throw new AccessDeniedException("You're not in the game");
        }
        Match ended = matchService.forceEndMatch(matchToUpdate);
        webSocketMatchService.broadcastMatchEnded(Objects.requireNonNull(ended));
        return new ResponseEntity<>(ended, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteMatch(@PathVariable("id") @NonNull Integer id) {
        matchService.delete(id);
        webSocketMatchService.broadcastLobbyClosed(id);
        return ResponseEntity.noContent().build();
    }
}
