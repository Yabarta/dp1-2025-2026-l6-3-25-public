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
}
