package es.us.dp1.l6_3_24_25.Petris.match.controller;

import es.us.dp1.l6_3_24_25.Petris.exceptions.AccessDeniedException;
import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.model.TurnType;
import es.us.dp1.l6_3_24_25.Petris.match.service.MatchService;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import es.us.dp1.l6_3_24_25.Petris.user.User;
import es.us.dp1.l6_3_24_25.Petris.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/matches")
@Tag(name = "Matches", description = "API for the management of Matches")
@SecurityRequirement(name = "bearerAuth")
public class MatchController {

    private MatchService matchService;
    private UserService userService;
    private PlayerService playerService;

    @Autowired
    public MatchController(MatchService ms, UserService us, PlayerService ps) {
        this.matchService = ms;
        this.userService = us;
        this.playerService = ps;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Match> getAllMatches() {
        return matchService.getAllMatches();
    }

    @GetMapping("/current")
    @ResponseStatus(HttpStatus.OK)
    public List<Match> getCurrentMatches() {
        return matchService.getCurrentMatches();
    }

    @GetMapping("/notStarted")
    @ResponseStatus(HttpStatus.OK)
    public List<Match> getNotStartedMatches() {
        return matchService.getNotStartedMatches();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Match getMatchById(@PathVariable("id") Integer id) throws ResourceNotFoundException {
        Match match = matchService.getMatchById(id);
        if (match == null) {
            throw new ResourceNotFoundException("Match", "id", id);
        }
        return match;
    }
    
    @GetMapping("/code/{code}")
    @ResponseStatus(HttpStatus.OK)
    public Match getMatchByCode(@PathVariable("code") String code) throws ResourceNotFoundException {
        Match match = matchService.getMatchByCode(code);
        if (match == null) {
            throw new ResourceNotFoundException("Match", "code", code);
        }
        return match;
    }

    // TODO Eliminar ¿En qué situación necesitamos esta petición?
    @GetMapping("/{id}/{dishIndex}")
    @ResponseStatus(HttpStatus.OK)
    public PetriDish getPetriDish(@PathVariable("id") Integer id, @PathVariable("dishIndex") Integer index) {
        Match match = matchService.getMatchById(id);
        return match.getBoardState().get(index);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Match> createMatch(@RequestParam(value = "isPrivate", defaultValue = "false") Boolean isPrivate)
            throws AccessDeniedException {
        User currentUser = userService.findCurrentUser();
        Player currentPlayer = playerService.getPlayerByUser(currentUser);
        if (currentPlayer.getIsCurrentlyInMatch()) {
            throw new AccessDeniedException("Already in a match");
        }
        Match match = new Match();
        String code = null;
        if (isPrivate) {
            code = UUID.randomUUID().toString();
        }
        match.setCode(code);
        match.setCreator(currentPlayer);
        match.setPlayer1(currentPlayer);
        matchService.createMatch(match);
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
    public ResponseEntity<Match> joinMatch(@PathVariable("id") Integer id, @RequestParam("code") Optional<String> code)
            throws AccessDeniedException {
        Match matchToUpdate = getMatchById(id);
        if(matchToUpdate.getEndedAt() != null) {
            throw new AccessDeniedException("The match has already ended");
        }

        if(matchToUpdate.getCode() != null && !matchToUpdate.getCode().equals(code.isEmpty() ? null : code)) {
            throw new AccessDeniedException("Incorrect code for private match");
        }
        User currentUser = userService.findCurrentUser();
        Player currentPlayer = playerService.getPlayerByUser(currentUser);
        matchToUpdate.setPlayer2(currentPlayer);
        currentPlayer.setIsCurrentlyInMatch(true);
        playerService.save(currentPlayer);
            
        return new ResponseEntity<>(matchService.joinMatch(matchToUpdate), HttpStatus.OK);
    }

    @PutMapping("/{id}/nextTurn")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> nextTurn(@Valid @RequestParam Optional<List<PetriDish>> newBoardState, @PathVariable("id") Integer id)
            throws AccessDeniedException {
        Match matchToUpdate = getMatchById(id);
        if(matchToUpdate.getEndedAt() != null) {
            throw new AccessDeniedException("The match has already ended");
        }

        User currentUser = userService.findCurrentUser();
        Player currentPlayer = playerService.getPlayerByUser(currentUser);
        if (!currentPlayer.equals(matchToUpdate.getPlayer1()) && matchToUpdate.getTurnType().equals(TurnType.P1_PROPAGATION) ||
            !currentPlayer.equals(matchToUpdate.getPlayer2()) && matchToUpdate.getTurnType().equals(TurnType.P2_PROPAGATION)) {
                throw new AccessDeniedException("It's not your turn");
        }

        Match updatedMatch = matchService.nextTurn(matchToUpdate, newBoardState);
        if(updatedMatch.getEndedAt() != null) {
            Player player1 = updatedMatch.getPlayer1();
            Player player2 = updatedMatch.getPlayer2();
            player1.setIsCurrentlyInMatch(false);
            player2.setIsCurrentlyInMatch(false);
            playerService.save(player1);
            playerService.save(player2);
        }

        return new ResponseEntity<>(updatedMatch, HttpStatus.OK);
    }

    @GetMapping("/{id}/checkErrors")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getPropagationErrors(@PathVariable("id") Integer id, @Valid @RequestParam List<PetriDish> newBoardState) 
            throws AccessDeniedException{
        User currentUser = userService.findCurrentUser();
        Player currentPlayer = playerService.getPlayerByUser(currentUser);
        Match match = getMatchById(id);
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
    public ResponseEntity<Match> forceEndMatch(@PathVariable("id") Integer id)
            throws AccessDeniedException {
        User currentUser = userService.findCurrentUser();
        Player currentPlayer = playerService.getPlayerByUser(currentUser);
        Match matchToUpdate = getMatchById(id);
        if(currentPlayer.equals(matchToUpdate.getPlayer1())) {
            matchToUpdate.setWinner(2);
        } else if(currentPlayer.equals(matchToUpdate.getPlayer2())) {
            matchToUpdate.setWinner(1);
        } else {
            throw new AccessDeniedException("You're not in the game");
        }
        return new ResponseEntity<>(matchService.forceEndMatch(matchToUpdate), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteMatch(@PathVariable("id") Integer id) {
        if (getMatchById(id) != null) {
            matchService.delete(id);
        }
        return ResponseEntity.noContent().build();
    }
}
