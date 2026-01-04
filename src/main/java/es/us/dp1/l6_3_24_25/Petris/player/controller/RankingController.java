package es.us.dp1.l6_3_24_25.Petris.player.controller;

import java.util.List;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;

import es.us.dp1.l6_3_24_25.Petris.player.model.PlayerRanking;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.l6_3_24_25.Petris.player.service.RankingService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/ranking")
@Tag(name = "Ranking", description = "Global ranking of players")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping
    public ResponseEntity<List<PlayerRanking>> getGlobalRanking() {
        return ResponseEntity.ok(rankingService.getGlobalRanking());
    }
}
