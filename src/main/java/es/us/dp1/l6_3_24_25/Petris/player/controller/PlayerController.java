package es.us.dp1.l6_3_24_25.Petris.player.controller;

import io.micrometer.core.instrument.Statistic;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.player.model.Achievement;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.model.Statistics;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.HttpStatus;





@RestController
@RequestMapping("/api/v1/players")
@Tag(name = "Players", description = "API for the management of Players")
public class PlayerController {

    PlayerService playerservice;

    @Autowired
    public PlayerController(PlayerService ps){
        this.playerservice = ps;
    }

    @GetMapping
    public List<Player> getAllPlayers() {
        return playerservice.getAllPlayers();
    }

    @GetMapping("/{id}")
    public Player getPlayerById(@PathVariable("id") Integer id) {
        return playerservice.getPlayerById(id);
    }

    @GetMapping("/{id}/statistics")
    public List<Statistics> getPlayerStatsById(@PathVariable("id") Integer id) {
        return playerservice.getPlayerById(id).getStatistics();
    }

    @GetMapping("/{id}/statistics/{statId}")
    public Statistics getPlayerSpecificStatById(@PathVariable("id") Integer id, @PathVariable("statId") Integer statId) {
        Player player = playerservice.getPlayerById(id);
        return player.getStatistics().stream()
                .filter(stat -> stat.getId().equals(statId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Statistic not found"));
    }

    @GetMapping("/{id}/achievement")
    public List<Achievement> getPlayerAchievementById(@PathVariable("id") Integer id) {
        return playerservice.getPlayerById(id).getAchievements();
    }

    @GetMapping("/{id}/game")
    public List<Match> getPlayerGameById(@PathVariable("id") Integer id) {
        return playerservice.getPlayerById(id).getGame();
    }

    @GetMapping("/{username}")
    public Player getPlayerByNickname(@PathVariable("username") String username) {
        return playerservice.getPlayerByNickname(username);
    }

    @PostMapping
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
        Player playerToUpdate = getPlayerById(id);
        BeanUtils.copyProperties(player, playerToUpdate, "id");
        playerservice.save(playerToUpdate);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/statistics/{statId}")
    @ResponseStatus(HttpStatus.OK)
    public void updatePlayerStat(@PathVariable("id") Integer id, @PathVariable("statId") Integer statId, @Valid @RequestBody Statistics stat) {
        Player player = playerservice.getPlayerById(id);
        Statistics statToUpdate = player.getStatistics().stream()
                .filter(s -> s.getId().equals(statId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Statistic not found"));
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
