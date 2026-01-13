package es.us.dp1.l6_3_24_25.Petris.player.controller;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.us.dp1.l6_3_24_25.Petris.player.model.Achievement;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.model.Statistics;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/v1/players")
@Tag(name = "Players", description = "API for the management of Players")
public class PlayerController {

    PlayerService playerservice;

    public PlayerController(PlayerService ps){
        this.playerservice = ps;
    }

    @PostConstruct
    public void init() {
        try {
            Path uploadsPath = Paths.get("uploads");
            if (!Files.exists(uploadsPath)) {
                Files.createDirectories(uploadsPath);
            }
        } catch (IOException e) {
            // If we can't create the uploads directory, the app will fail later when saving files.
        }
    }


    @Operation(
        summary = "Retrieve all players",
        tags = { "players", "get all" }
    )
    @ApiResponses(
        @ApiResponse(responseCode = "200", description = "Players found", content = { @Content(schema = @Schema(implementation = Player.class),
                mediaType = "application/json")})
    )
    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        return new ResponseEntity<>(playerservice.getAllPlayers(), HttpStatus.OK);
    }


    @Operation(
        summary = "Retrieve a player by ID",
        tags = { "players", "get by id" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Player found", content = { @Content(schema = @Schema(implementation = Player.class),
                mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", description = "Player not found", content = @Content(schema = @Schema()))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(playerservice.getPlayerById(id) , HttpStatus.OK);
    }


    @Operation(
        summary = "Retrieve player statistics by ID",
        tags = { "players", "get by id" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Statistics found", content = { @Content(schema = @Schema(implementation = Statistics.class),
                mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", description = "Player not found", content = @Content(schema = @Schema()))
    })
    @GetMapping("/{id}/statistics")
    public ResponseEntity<Statistics> getPlayerStatsById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(playerservice.getPlayerById(id).getStatistics(), HttpStatus.OK);
    }

    @Operation(
        summary = "Retrieve player achievements by ID",
        tags = { "players", "get by id" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Achievements found", content = { @Content(schema = @Schema(implementation = Achievement.class),
                mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", description = "Player not found", content = @Content(schema = @Schema()))
    })
    @GetMapping("/{id}/achievements")
    public ResponseEntity<List<Achievement>> getPlayerAchievementById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(playerservice.getPlayerById(id).getAchievements(), HttpStatus.OK);
    }


    @Operation(
        summary = "Retrieve a player by nickname",
        tags = { "players", "get by nickname" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Player found", content = { @Content(schema = @Schema(implementation = Player.class),
                mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", description = "Player not found", content = @Content(schema = @Schema()))
    })
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<Player> getPlayerByNickname(@PathVariable("nickname") String nickname) {
        return new ResponseEntity<>(playerservice.getPlayerByNickname(nickname), HttpStatus.OK);
    }


    @Operation(
        summary = "Retrieve a player by username",
        tags = { "players", "get by username" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Player found", content = { @Content(schema = @Schema(implementation = Player.class),
                mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", description = "Player not found", content = @Content(schema = @Schema()))
    })
    @GetMapping("/user/{username}")
    public ResponseEntity<Player> getPlayerByUsername(@PathVariable("username") String username) {
        return new ResponseEntity<>(playerservice.getPlayerByUsername(username), HttpStatus.OK);
    }


    @Operation(
        summary = "Create a new player",
        tags = { "players", "create" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Player created", content = { @Content(schema = @Schema(implementation = Player.class),
                mediaType = "application/json")}),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema()))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Player> createPlayer(@Valid @RequestBody Player player) {
        playerservice.save(player);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(player.getId())
            .toUri();

        return ResponseEntity.created(location).body(player);
    }


    @Operation(
        summary = "Update a player",
        tags = { "players", "update" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Player updated", content = { @Content(schema = @Schema(implementation = Player.class),
                mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", description = "Player not found", content = @Content(schema = @Schema()))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePlayer(@Valid @RequestBody Player player, @PathVariable("id") Integer id) {
        Player playerToUpdate = playerservice.getPlayerById(id);
        BeanUtils.copyProperties(player, playerToUpdate, "id");
        playerservice.save(playerToUpdate);

        return ResponseEntity.noContent().build();
    }


    @Operation(
        summary = "Update a player with profile picture",
        tags = { "players", "update with image" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Player updated", content = { @Content(schema = @Schema(implementation = Player.class),
                mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", description = "Player not found", content = @Content(schema = @Schema()))
    })
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Player> updatePlayerWithImage(@PathVariable("id") Integer id,
                                                        @RequestParam(value = "profilePicture", required = false) MultipartFile file,
                                                        @RequestParam(value = "nickname", required = false) String nickname,
                                                        @RequestParam(value = "email", required = false) String email) {
        Player playerToUpdate = playerservice.getPlayerById(id);

        if (nickname != null) playerToUpdate.setNickname(nickname);
        if (email != null) playerToUpdate.setEmail(email);

        if (file != null && !file.isEmpty()) {
            try {
                deleteOldProfilePicture(playerToUpdate);
                Path uploadDir = Paths.get("uploads");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = uploadDir.resolve(fileName);
                Files.copy(file.getInputStream(), filePath);
                playerToUpdate.setProfilePicture("/uploads/" + fileName);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        playerservice.save(playerToUpdate);
        return ResponseEntity.ok(playerToUpdate);
    }

    private void deleteOldProfilePicture(Player player) throws IOException {
        String oldProfilePicture = player.getProfilePicture();
        if (oldProfilePicture != null && oldProfilePicture.startsWith("/uploads/")) {
            String oldFileName = oldProfilePicture.substring("/uploads/".length());
            Path oldFilePath = Paths.get("uploads").resolve(oldFileName);
            Files.deleteIfExists(oldFilePath);
        }
    }


    @Operation(
        summary = "Delete a player",
        tags = { "players", "delete" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Player deleted", content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "404", description = "Player not found", content = @Content(schema = @Schema()))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable("id") Integer id){
        if(getPlayerById(id)!=null){
            playerservice.delete(id);
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/onlineDetection/{id}")
    public ResponseEntity<Void> detectionPresence(@PathVariable("id") Integer id) {
        playerservice.detectionCurrentPlayer(id);
        return ResponseEntity.noContent().build();
    }

}
