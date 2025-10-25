package es.us.dp1.l6_3_24_25.Petris.match.controller;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.service.MatchService;
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
@RequestMapping("/api/v1/matches")
@Tag(name = "Matches", description = "API for the management of Matches")
@SecurityRequirement(name = "bearerAuth")
public class MatchController {

    MatchService matchService;
    @Autowired
    public MatchController(MatchService ms){
        this.matchService = ms;
    }

    @GetMapping
    public List<Match> getAllGames(){
        return matchService.getAllMatches();
    }

    @GetMapping("/current")
    public List<Match> getCurrentMatches(){
        return matchService.getCurrentMatches();
    }

    @GetMapping("/notStarted")
    public List<Match> getNotStartedMatches(){
        return matchService.getNotStartedMatches();
    }

    @GetMapping("/{id}")
    public Match getMatchById(@PathVariable("id")Integer id){
        Match match = matchService.getMatchById(id);
        if(match == null){
            throw new ResourceNotFoundException("Match", "id", id);
        }
        return match;
    }

    @GetMapping("/{code}")
    public Match getMatchByCode(@PathVariable("code")String code){
        Match match = matchService.getMatchByCode(code);
        if(match == null){
            throw new ResourceNotFoundException("Match", "code", code);
        }
        return match;
    }

    @GetMapping("/{id}/{dishIndex}")
    public PetriDish getPetriDish(@PathVariable("id")Integer id,
                                    @PathVariable("dishIndex")Integer index){
        Match match = matchService.getMatchById(id);
        return match.getPetriDish().get(index);
    }

    @PostMapping
    public ResponseEntity<Match> createMatch(@Valid @RequestBody Match match){
        matchService.save(match);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(match.getId())
            .toUri();
        return ResponseEntity.created(location).body(match);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMatch(@Valid @RequestBody Match match, @PathVariable("id") Integer id){
        Match matchToUpdate = getMatchById(id);
        BeanUtils.copyProperties(match, matchToUpdate, "id");
        matchService.save(matchToUpdate);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable("id")Integer id){
        if(getMatchById(id)!=null){
            matchService.delete(id);
        }
        return ResponseEntity.noContent().build();
    }
}
