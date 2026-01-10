package es.us.dp1.l6_3_24_25.Petris.player.controller;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.player.model.Achievement;
import es.us.dp1.l6_3_24_25.Petris.player.service.AchievementService;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/achievements")
@Tag(name = "Achievements", description = "API for the management of Achievements")
public class AchievementController {

    AchievementService achievementService;
    PlayerService playerService;

    public AchievementController(AchievementService as, PlayerService ps) {
        this.achievementService = as;
        this.playerService = ps;
    }

    @Operation(
        summary = "Retrieve all achievements",
        tags = { "achievements", "get all" }
    )
    @ApiResponses(
        @ApiResponse(responseCode = "200", description = "Achievements found", content = { @Content(schema = @Schema(implementation = Achievement.class),
                mediaType = "application/json")})
    )
    @GetMapping
    public List<Achievement> getAllAchievements() {
        return achievementService.getAllAchievements();
    }

    @Operation(
        summary = "Retrieve an achievement by ID",
        tags = { "achievements", "get by id" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Achievement found", content = { @Content(schema = @Schema(implementation = Achievement.class),
                mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", description = "Achievement not found", content = @Content(schema = @Schema()))
    })
    @GetMapping("/{id}")
    public Achievement getAchievementById(@PathVariable("id") Integer id) {
        Achievement achievement = achievementService.getAchievementById(id);
        if (achievement == null) {
            throw new ResourceNotFoundException("Achievement", "id", id);
        }
        return achievement;
    }

    // TODO: Add GET method by name

    @Operation(
        summary = "Create a new achievement",
        tags = { "achievements", "create" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Achievement created", content = { @Content(schema = @Schema(implementation = Achievement.class),
                mediaType = "application/json")}),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema()))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Achievement> createAchievement(@Valid @RequestBody Achievement achievement) {
    Achievement saved = achievementService.saveAchievement(achievement);
    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(saved.getId())
        .toUri();
    return ResponseEntity.created(location).body(saved);
    }

    @Operation(
        summary = "Create a new achievement with image",
        tags = { "achievements", "create with image" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Achievement created", content = { @Content(schema = @Schema(implementation = Achievement.class),
                mediaType = "application/json")}),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema()))
    })
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Achievement> createAchievementWithImage(
                                                                  @RequestParam(value = "name") String name,
                                                                  @RequestParam(value = "description") String description,
                                                                  @RequestParam(value = "valor") Integer valor,
                                                                  @RequestParam(value = "statisticName") String statisticName,
                                                                  @RequestParam(value = "image", required = false) MultipartFile file) {
        Achievement saved = achievementService.createAchievementWithImage(name, description, valor, statisticName, file);
        playerService.getAllPlayers().stream().forEach(player -> {
            if (player.getStatistics().getStatisticByName(statisticName) >= valor) {
                player.getAchievements().add(saved);
                playerService.save(player);
            }
        });
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(saved.getId())
            .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @Operation(
        summary = "Update an existing achievement",
        tags = { "achievements", "update" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Achievement updated", content = { @Content(schema = @Schema(implementation = Achievement.class),
                mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", description = "Achievement not found", content = @Content(schema = @Schema()))
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Achievement updateAchievement(@PathVariable("id") Integer id, @Valid @RequestBody Achievement newAchievement) {
        Achievement achievement = achievementService.getAchievementById(id);
        if (achievement == null) {
            throw new ResourceNotFoundException("Achievement", "id", id);
        }
        achievement.setName(newAchievement.getName());
        achievement.setDescription(newAchievement.getDescription());
        achievement.setValor(newAchievement.getValor());
        achievement.setStatisticName(newAchievement.getStatisticName());
        achievement.setImage(newAchievement.getImage());
        return achievementService.saveAchievement(achievement);
    }

    @Operation(
        summary = "Update an achievement with image",
        tags = { "achievements", "update with image" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Achievement updated", content = { @Content(schema = @Schema(implementation = Achievement.class),
                mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", description = "Achievement not found", content = @Content(schema = @Schema()))
    })
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Achievement> updateAchievementWithImage(@PathVariable("id") Integer id,
                                                                  @RequestParam(value = "image", required = false) MultipartFile file,
                                                                  @RequestParam(value = "name", required = false) String name,
                                                                  @RequestParam(value = "description", required = false) String description,
                                                                  @RequestParam(value = "valor", required = false) Integer valor,
                                                                  @RequestParam(value = "statisticName", required = false) String statisticName) {
        Achievement achievement = achievementService.updateAchievementWithImage(id, file, name, description, valor, statisticName);
        return ResponseEntity.ok(achievement);
    }

    @Operation(
        summary = "Delete an achievement",
        tags = { "achievements", "delete" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Achievement deleted", content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "404", description = "Achievement not found", content = @Content(schema = @Schema()))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAchievement(@PathVariable("id") Integer id) {
        Achievement achievement = achievementService.getAchievementById(id);
        if (achievement == null) {
            throw new ResourceNotFoundException("Achievement", "id", id);
        }
        achievementService.deleteAchievement(id);
    }

}
