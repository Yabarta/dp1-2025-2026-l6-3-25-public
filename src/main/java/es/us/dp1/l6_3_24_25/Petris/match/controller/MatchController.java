package es.us.dp1.l6_3_24_25.Petris.match.controller;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    public MatchController(
        MatchService ms,
        WebSocketMatchService webSocketMatchService,
        UserService us,
        PlayerService ps) {

        this.matchService = ms;
        this.webSocketMatchService = webSocketMatchService;
        this.userService = us;
        this.playerService = ps;
    }

    @Operation(
        summary = "Retrieve all matches",
        description = "Get a list of all matches",
        tags = { "matches", "get all" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Matches found", content = { @Content(schema =  @Schema(implementation = Match.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", description = "No matches found", content = @Content(schema = @Schema()))
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Match>> getAllMatches() {
        return new ResponseEntity<>(matchService.getAllMatches(), HttpStatus.OK);
    }

    @Operation(
        summary = "Retrieve current matches",
        description = "Get a list of matches that are currently ongoing",
        tags = { "matches", "get current" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Current matches found", content = { @Content(schema =  @Schema(implementation = Match.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", description = "No current matches found", content = @Content(schema = @Schema()))
    })
    @GetMapping("/current")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Match>> getCurrentMatches() {
        return new ResponseEntity<>(matchService.getCurrentMatches(), HttpStatus.OK);
    }

    @Operation(
        summary = "Retrieve not started matches",
        description = "Get a list of matches that have not started yet",
        tags = { "matches", "get not started" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Not started matches found", content = { @Content(schema =  @Schema(implementation = Match.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", description = "No not started matches found", content = @Content(schema = @Schema()))
    })
    @GetMapping("/notStarted")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Match>> getNotStartedMatches() {
        return new ResponseEntity<>(matchService.getNotStartedMatches(), HttpStatus.OK);
    }

    @Operation(
        summary = "Retrieve match by id",
        description = "Get a match by its unique id",
        tags = { "matches", "get by id" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Match found", content = { @Content(schema = @Schema(implementation = Match.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", description = "Match not found", content = @Content(schema = @Schema()))
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> getMatchById(@PathVariable(required = true) Integer id) throws ResponseStatusException {
        try {
            Match result = matchService.getMatchById(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(
        summary = "Retrieve match by code",
        description = "Get a match by its unique code",
        tags = { "matches", "get by code" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Match found", content = { @Content(schema = @Schema(implementation = Match.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", description = "Match not found", content = @Content(schema = @Schema()))
    })
    @GetMapping("/code/{code}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> getMatchByCode(@PathVariable(required = true) String code) throws ResponseStatusException {
        try {
            Match result = matchService.getMatchByCode(code);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private Player getCurrentPlayer() {
        User currentUser = userService.findCurrentUser();
        Player currentPlayer = playerService.getPlayerByUser(currentUser);
        return currentPlayer;
    }

    @Operation(
        summary = "Create a new match",
        description = "Create a new match, optionally private",
        tags = { "matches", "post" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Match created", content = { @Content(schema = @Schema(implementation = Match.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema()))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Match> createMatch(@RequestParam(defaultValue = "false") Boolean isPrivate) throws ResponseStatusException {
        try {
            Player currentPlayer = getCurrentPlayer();
            Match createdMatch = matchService.createMatch(currentPlayer, isPrivate);
            playerService.setIsCurrentlyInMatch(currentPlayer, true);

            webSocketMatchService.broadcastLobbyState(createdMatch);

            return new ResponseEntity<>(createdMatch, HttpStatus.CREATED);
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @Operation(
        summary = "Join an existing match",
        description = "Join an existing match by its id, providing the code if it's private",
        tags = { "matches", "put" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Match joined", content = { @Content(schema = @Schema(implementation = Match.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema()))
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> joinMatch(@PathVariable(required = true) Integer id,
            @RequestParam(defaultValue = "") String code)
            throws ResponseStatusException {

        try {
            Match result;
            Match matchToUpdate = matchService.getMatchById(id);
            Player currentPlayer = getCurrentPlayer();
            if (matchToUpdate.hasPlayer(currentPlayer)) {
                // Can't join if already in the match. Result is the match without updating it
                result = matchToUpdate;
            } else {
                result = matchService.joinMatch(matchToUpdate, currentPlayer, code);
                playerService.setIsCurrentlyInMatch(currentPlayer, true);

                webSocketMatchService.broadcastLobbyAndMatchState(result);
            }

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @Operation(
        summary = "Leave a match",
        description = "Leave a match by its id",
        tags = { "matches", "put" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Match left", content = @Content()),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema()))
    })
    @PutMapping("/{id}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> leaveMatch(@PathVariable(required = true) Integer id) throws ResponseStatusException {
        try {
            Match matchToUpdate = matchService.getMatchById(id);
            Player currentPlayer = getCurrentPlayer();

            Match updatedMatch = null;
            if (matchToUpdate.isFull()) {
                updatedMatch = matchService.leaveMatch(matchToUpdate, currentPlayer);

                playerService.setIsCurrentlyInMatch(currentPlayer, false);
            } else {
                matchService.delete(id);

                if (matchToUpdate.getPlayer1() != null) {
                    playerService.setIsCurrentlyInMatch(matchToUpdate.getPlayer1(), false);
                }
                if (matchToUpdate.getPlayer2() != null) {
                    playerService.setIsCurrentlyInMatch(matchToUpdate.getPlayer2(), false);
                }
            }

            if (updatedMatch != null) {
                webSocketMatchService.broadcastLobbyState(Objects.requireNonNull(updatedMatch));
            } else {
                webSocketMatchService.broadcastLobbyClosed(matchToUpdate.getId());
            }

            return ResponseEntity.noContent().build();
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @Operation(
        summary = "Start a match",
        description = "Start a match by its id (only the creator can start it)",
        tags = { "matches", "put" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Match started", content = { @Content(schema = @Schema(implementation = Match.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema()))
    })
    @PutMapping("/{id}/start")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> startMatch(@PathVariable(required = true) Integer id) throws ResponseStatusException {
        try {
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
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @Operation(
        summary = "Advance to the next turn",
        description = "Advance the match to the next turn, providing the new board state",
        tags = { "matches", "put" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Turn advanced", content = { @Content(schema = @Schema(implementation = Match.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema()))
    })
    @PutMapping("/{id}/nextTurn")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> nextTurn(@PathVariable(required = true) Integer id,
            @Valid @RequestBody(required = false) Optional<List<PetriDish>> newBoardState)
            throws ResponseStatusException {

        try {
            Player currentPlayer = getCurrentPlayer();
            Match matchToUpdate = matchService.getMatchById(id);

            if (!matchToUpdate.isTurnOf(currentPlayer) && !matchToUpdate.isFissionOrContaminationTurn(currentPlayer)) {
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
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @Operation(
        summary = "Check for errors in the proposed board state",
        description = "Check for errors in the proposed board state for the current turn",
        tags = { "matches", "post" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Errors checked", content = { @Content(schema = @Schema(implementation = String.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema()))
    })
    @GetMapping("/{id}/checkErrors")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<String>> checkErrors(@PathVariable(required = true) Integer id,
            @Valid @RequestBody List<PetriDish> newBoardState)
            throws ResponseStatusException {

        try {
            Player currentPlayer = getCurrentPlayer();
            Match matchToCheck = matchService.getMatchById(id);

            List<String> errors = matchService.checkErrors(matchToCheck, newBoardState, currentPlayer);

            return new ResponseEntity<>(errors, HttpStatus.OK);
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @Operation(
        summary = "Concede the match",
        description = "Concede the match, declaring the opponent as the winner",
        tags = { "matches", "put" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Match conceded", content = { @Content(schema = @Schema(implementation = Match.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema()))
    })
    @PutMapping("/{id}/endMatch")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Match> concedeMatch(@PathVariable(required = true) Integer id) throws ResponseStatusException {
        try {
            Player currentPlayer = getCurrentPlayer();
            Match matchToUpdate = matchService.getMatchById(id);

            Match updatedMatch = matchService.concedeMatch(matchToUpdate, currentPlayer);

            playerService.setIsCurrentlyInMatch(matchToUpdate.getPlayer1(), false);
            playerService.setIsCurrentlyInMatch(matchToUpdate.getPlayer2(), false);

            webSocketMatchService.broadcastMatchEnded(Objects.requireNonNull(updatedMatch));

            return new ResponseEntity<>(updatedMatch, HttpStatus.OK);
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @Operation(
        summary = "Delete a match",
        description = "Delete a match by its id",
        tags = { "matches", "delete" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Match deleted", content = @Content()),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema()))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteMatch(@PathVariable(required = true) Integer id) throws ResponseStatusException {
        try {
            Match matchToDelete = matchService.getMatchById(id);

            matchService.delete(id);

            if (matchToDelete.getPlayer1() != null) {
                playerService.setIsCurrentlyInMatch(matchToDelete.getPlayer1(), false);
            }
            if (matchToDelete.getPlayer2() != null) {
                playerService.setIsCurrentlyInMatch(matchToDelete.getPlayer2(), false);
            }

            webSocketMatchService.broadcastLobbyClosed(id);

            return ResponseEntity.noContent().build();
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
}
