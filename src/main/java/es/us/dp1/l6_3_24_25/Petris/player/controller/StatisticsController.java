package es.us.dp1.l6_3_24_25.Petris.player.controller;

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

@RestController
@RequestMapping("/api/v1/statistics")
@Tag(name = "Statistics", description = "API for the management of Statistics")
public class StatisticsController {

	StatisticsService statisticsService;

    public StatisticsController(StatisticsService ss){
        this.statisticsService = ss;
    }

	@GetMapping
	public ResponseEntity<List<Statistics>> getAllStatistics() {
        return ResponseEntity.ok(statisticsService.getAllStatistics());
    }

    @GetMapping
    public ResponseEntity<List<Integer>> getGlobalStatisticsArray() {
        List<Integer> globalStats = statisticsService.getGlobalStatisticsArray();
        return ResponseEntity.ok(globalStats);
    }

	@GetMapping("/{id}")
	public ResponseEntity<Statistics> getStatisticsById(@PathVariable Integer id) {
        Statistics statistics = statisticsService.getStatisticsById(id);
        return ResponseEntity.ok(statistics);
    }

	@GetMapping("/{id}/array")
	public ResponseEntity<List<Integer>> getStatisticsArrayById(@PathVariable Integer id) {
        List<Integer> statisticsArray = statisticsService.getStatisticsArrayById(id);
        return ResponseEntity.ok(statisticsArray);
    }

}
