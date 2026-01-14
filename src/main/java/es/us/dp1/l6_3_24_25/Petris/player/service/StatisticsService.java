package es.us.dp1.l6_3_24_25.Petris.player.service;

import java.util.List;

import es.us.dp1.l6_3_24_25.Petris.player.model.GlobalStatistic;
import es.us.dp1.l6_3_24_25.Petris.player.model.StatsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.player.model.Statistics;
import es.us.dp1.l6_3_24_25.Petris.player.repository.StatisticsRepository;

@Service

public class StatisticsService {

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Transactional(readOnly = true)
    public List<Statistics> getAllStatistics() {
        return statisticsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Statistics getStatisticsById(Integer id) {
        return statisticsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Statistics", "id", id));
    }

    @Transactional
    public Statistics saveStatistics(Statistics statistics) {
        return statisticsRepository.save(statistics);
    }

    @Transactional(readOnly = true)
    public GlobalStatistic getGlobalStatistics() {
        List<Statistics> all = getAllStatistics();

        Integer totalGamesPlayed = all.stream().map(Statistics::getGamesPlayed).reduce(0, Integer::sum) / 2; // Each game is counted twice, once for each player
        Integer totalTimePlayed = all.stream().map(Statistics::getTimePlayed).reduce(0, Integer::sum) / 2 / 60 / 60; // Each game is counted twice, once for each player, and convert to minutes
        Integer totalSarcinasCreated = all.stream().map(Statistics::getSarcinasCreated).reduce(0, Integer::sum);
        Integer totalPlayers = all.size();

        return new GlobalStatistic(totalGamesPlayed, totalTimePlayed, totalSarcinasCreated, totalPlayers);
    }

    @Transactional(readOnly = true)
    public List<Double> getBoxPlotStatsForField(String fieldName) {
        List<Statistics> allStats = getAllStatistics();
        List<Integer> fieldValues;

        switch (fieldName) {
            case "gamesPlayed":
                fieldValues = allStats.stream().map(Statistics::getGamesPlayed).toList();
                break;
            case "gamesWon":
                fieldValues = allStats.stream().map(Statistics::getGamesWon).toList();
                break;
            case "timePlayed":
                fieldValues = allStats.stream().map(Statistics::getTimePlayed).toList();
                break;
            case "sarcinasCreated":
                fieldValues = allStats.stream().map(Statistics::getSarcinasCreated).toList();
                break;
            case "bacteriasCreated":
                fieldValues = allStats.stream().map(Statistics::getBacteriasCreated).toList();
                break;
            default:
                throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }

        return StatsUtil.calculateBoxPlotStats(fieldValues);
    }
}
