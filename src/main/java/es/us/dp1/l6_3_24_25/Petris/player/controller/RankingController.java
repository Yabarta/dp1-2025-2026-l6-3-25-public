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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/ranking")
@Tag(name = "Ranking", description = "Global ranking of players")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @Operation(
        summary = "Retrieve the global ranking of players",
        tags = { "ranking", "get global ranking" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ranking retrieved successfully", content = { @Content(schema = @Schema(implementation = PlayerRanking.class),
                mediaType = "application/json")})
    })
    @GetMapping
    public ResponseEntity<List<PlayerRanking>> getGlobalRanking() {
        return ResponseEntity.ok(rankingService.getGlobalRanking());
    }
}
