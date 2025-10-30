package es.us.dp1.l6_3_24_25.Petris.match.controller;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.service.MatchService;
import es.us.dp1.l6_3_24_25.Petris.match.service.PetriDishService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/matches/{matchId}/petriDishes")
@Tag(name = "Petri Dishes", description = "API for the management of Petri Dishes")
@SecurityRequirement(name = "bearerAuth")
public class PetriDishController {

    private final PetriDishService petriDishService;
    private final MatchService matchService;

    @Autowired
    public PetriDishController(PetriDishService petriDishService, MatchService matchService) {
        this.petriDishService = petriDishService;
        this.matchService = matchService;
    }

    @GetMapping
    public List<PetriDish> getAllPetriDishesByMatchId(@PathVariable("matchId") Integer matchId) {
        Match match = matchService.getMatchById(matchId);
        if (match == null) {
            throw new ResourceNotFoundException("Match", "id", matchId);
        }
        return match.getPetriDish();
    }

    @GetMapping("/{petriDishIndex}")
    public PetriDish getPetriDishByIndex(@PathVariable("matchId") Integer matchId, @PathVariable("petriDishIndex") Integer petriDishIndex) {
        Match match = matchService.getMatchById(matchId);
        if (match == null) {
            throw new ResourceNotFoundException("Match", "id", matchId);
        }
        PetriDish petriDish = petriDishService.getPetriDishByIndex(petriDishIndex);
        if (petriDish == null || !match.getPetriDish().contains(petriDish)) {
            throw new ResourceNotFoundException("PetriDish", "index", petriDishIndex);
        }
        return petriDish;
    }

    @PostMapping
    public ResponseEntity<PetriDish> createPetriDish(@PathVariable("matchId") Integer matchId, @Valid @RequestBody PetriDish petriDish) {
        Match match = matchService.getMatchById(matchId);
        if (match == null) {
            throw new ResourceNotFoundException("Match", "id", matchId);
        }
        PetriDish newPetriDish = petriDishService.save(petriDish);
        match.getPetriDish().add(newPetriDish);
        matchService.save(match);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{petriDishId}")
                .buildAndExpand(newPetriDish.getId())
                .toUri();
        return ResponseEntity.created(location).body(newPetriDish);
    }

    @PutMapping("/{petriDishId}")
    public ResponseEntity<Void> updatePetriDish(@PathVariable("matchId") Integer matchId, @PathVariable("petriDishId") Integer petriDishId, @Valid @RequestBody PetriDish petriDish) {
        Match match = matchService.getMatchById(matchId);
        if (match == null) {
            throw new ResourceNotFoundException("Match", "id", matchId);
        }
        PetriDish petriDishToUpdate = petriDishService.getPetriDishById(petriDishId);
        if (petriDishToUpdate == null || !match.getPetriDish().contains(petriDishToUpdate)) {
            throw new ResourceNotFoundException("PetriDish", "id", petriDishId);
        }
        BeanUtils.copyProperties(petriDish, petriDishToUpdate, "id");
        petriDishService.save(petriDishToUpdate);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{petriDishId}")
    public ResponseEntity<Void> deletePetriDish(@PathVariable("matchId") Integer matchId, @PathVariable("petriDishId") Integer petriDishId) {
        Match match = matchService.getMatchById(matchId);
        if (match == null) {
            throw new ResourceNotFoundException("Match", "id", matchId);
        }
        PetriDish petriDishToDelete = petriDishService.getPetriDishById(petriDishId);
        if (petriDishToDelete == null || !match.getPetriDish().contains(petriDishToDelete)) {
            throw new ResourceNotFoundException("PetriDish", "id", petriDishId);
        }
        match.getPetriDish().remove(petriDishToDelete);
        matchService.save(match);
        petriDishService.delete(petriDishId);
        return ResponseEntity.noContent().build();
    }
}