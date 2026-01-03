package es.us.dp1.l6_3_24_25.Petris.player.service;

import java.util.List;

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
    public List<Integer> getStatisticsArrayById(Integer id) {
        Statistics s = getStatisticsById(id);
        return List.of(
            s.getGamesPlayed(),
            s.getGamesWon(),
            s.getTimePlayed(),
            s.getSarcinasCreated(),
            s.getBacteriasCreated()
        );
    }

    @Transactional(readOnly = true)
    public List<Integer> getGlobalStatisticsArray() {
        List<Statistics> all = getAllStatistics();
        int totalGamesPlayed = 0;
        int totalGamesWon = 0;
        int totalTimePlayed = 0;
        int totalSarcinasCreated = 0;
        int totalBacteriasCreated = 0;

        for (Statistics s : all) {
            List<Integer> arr = getStatisticsArrayById(s.getId());
            if (arr.size() >= 5) {
                totalGamesPlayed += arr.get(0);
                totalGamesWon += arr.get(1);
                totalTimePlayed += arr.get(2);
                totalSarcinasCreated += arr.get(3);
                totalBacteriasCreated += arr.get(4);
            }
        }

        return List.of(
            totalGamesPlayed,
            totalGamesWon,
            totalTimePlayed,
            totalSarcinasCreated,
            totalBacteriasCreated
        );
    }
}
