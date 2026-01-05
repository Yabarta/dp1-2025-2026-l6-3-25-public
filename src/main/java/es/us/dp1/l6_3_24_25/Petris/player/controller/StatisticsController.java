package es.us.dp1.l6_3_24_25.Petris.player.controller;

import es.us.dp1.l6_3_24_25.Petris.player.model.GlobalStatistic;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.player.model.Statistics;
import es.us.dp1.l6_3_24_25.Petris.player.service.StatisticsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import java.util.List;

import io.micrometer.core.ipc.http.HttpSender.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/statistics")
@Tag(name = "Statistics", description = "API for the management of Statistics")
public class StatisticsController {

	StatisticsService statisticsService;

    public StatisticsController(StatisticsService ss){
        this.statisticsService = ss;
    }

    @Operation(
        summary = "Retrieve all statistics",
        tags = { "statistics", "get all" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Statistics found", content = { @Content(schema = @Schema(implementation = Statistics.class),
                mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", description = "Statistics not found", content = @Content(schema = @Schema()))
    })
	@GetMapping
	public ResponseEntity<List<Statistics>> getAllStatistics() {
        return ResponseEntity.ok(statisticsService.getAllStatistics());
    }


    @Operation(
        summary = "Retrieve global statistics",
        tags = { "statistics", "get global statistics" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Global statistics found", content = { @Content(schema = @Schema(implementation = GlobalStatistic.class),
                mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", description = "Global statistics not found", content = @Content(schema = @Schema()))
    })
    @GetMapping("/global")
    public ResponseEntity<GlobalStatistic> getGlobalStatisticsArray() {
        GlobalStatistic globalStats = statisticsService.getGlobalStatistics();
        return ResponseEntity.ok(globalStats);
    }

    @Operation(
        summary = "Retrieve a statistics entry by ID",
        tags = { "statistics", "get by id" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Statistics entry found", content = { @Content(schema = @Schema(implementation = Statistics.class),
                mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", description = "Statistics entry not found", content = @Content(schema = @Schema()))
    })
	@GetMapping("/{id}")
	public ResponseEntity<Statistics> getStatisticsById(@PathVariable Integer id) {
        Statistics statistics = statisticsService.getStatisticsById(id);
        return ResponseEntity.ok(statistics);
    }

    @Operation(
        summary = "Retrieve box plot statistics distribution for a specific field",
        tags = { "statistics", "get box plot distribution" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Box plot statistics distribution retrieved successfully", content = { @Content(schema = @Schema(implementation = Double.class),
                mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", description = "Field not found", content = @Content(schema = @Schema()))
    })
    @GetMapping("/distribution/{fieldName}")
    public ResponseEntity<List<Double>> getStatisticsDistribution(@PathVariable String fieldName) {
        List<Double> distribution = statisticsService.getBoxPlotStatsForField(fieldName);
        return ResponseEntity.ok(distribution);
    }



}
