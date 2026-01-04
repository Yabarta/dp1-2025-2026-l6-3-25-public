package es.us.dp1.l6_3_24_25.Petris.player.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.player.model.Achievement;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.model.Statistics;

import java.net.URI;
import java.util.List;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;



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

    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        return new ResponseEntity<>(playerservice.getAllPlayers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(playerservice.getPlayerById(id) , HttpStatus.OK);
    }

    @GetMapping("/{id}/statistics")
    public ResponseEntity<Statistics> getPlayerStatsById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(playerservice.getPlayerById(id).getStatistics(), HttpStatus.OK);
    }

    @GetMapping("/{id}/achievements")
    public ResponseEntity<List<Achievement>> getPlayerAchievementById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(playerservice.getPlayerById(id).getAchievements(), HttpStatus.OK);
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<Player> getPlayerByNickname(@PathVariable("nickname") String nickname) {
        return new ResponseEntity<>(playerservice.getPlayerByNickname(nickname), HttpStatus.OK);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<Player> getPlayerByUsername(@PathVariable("username") String username) {
        return new ResponseEntity<>(playerservice.getPlayerByUsername(username), HttpStatus.OK);
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePlayer(@Valid @RequestBody Player player, @PathVariable("id") Integer id) {
        Player playerToUpdate = playerservice.getPlayerById(id);
        BeanUtils.copyProperties(player, playerToUpdate, "id");
        playerservice.save(playerToUpdate);

        return ResponseEntity.noContent().build();
    }

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

    @PutMapping("/{id}/statistics/{statId}")
    @ResponseStatus(HttpStatus.OK)
    public void updatePlayerStat(@PathVariable("id") Integer id, @PathVariable("statId") Integer statId, @Valid @RequestBody Statistics stat) {
        Player player = playerservice.getPlayerById(id);
        Statistics statToUpdate = player.getStatistics();
        BeanUtils.copyProperties(stat, statToUpdate, "id");
        playerservice.save(player);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable("id") Integer id){
        if(getPlayerById(id)!=null){
            playerservice.delete(id);
        }
        return ResponseEntity.noContent().build();
    }

}
