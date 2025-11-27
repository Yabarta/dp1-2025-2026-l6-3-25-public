package es.us.dp1.l6_3_24_25.Petris.player.controller;

import es.us.dp1.l6_3_24_25.Petris.exceptions.AccessDeniedException;
import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.player.model.Achievement;
import es.us.dp1.l6_3_24_25.Petris.player.service.AchievementService;
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
@RequestMapping("/api/v1/achievements")
@Tag(name = "Achievements", description = "API for the management of Achievements")
public class AchievementController {

    AchievementService achievementService;

    @Autowired
    public AchievementController(AchievementService as) {
        this.achievementService = as;
    }

    @GetMapping
    public List<Achievement> getAllAchievements() {
        return achievementService.getAllAchievements();
    }

    @GetMapping("/{id}")
    public Achievement getAchievementById(@PathVariable("id") Integer id) {
        Achievement achievement = achievementService.getAchievementById(id);
        if (achievement == null) {
            throw new ResourceNotFoundException("Achievement", "id", id);
        }
        return achievement;
    }

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
