package es.us.dp1.l6_3_24_25.Petris.player.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import es.us.dp1.l6_3_24_25.Petris.player.model.Statistics;

@Repository
public interface StatisticsRepository extends CrudRepository<Statistics, Integer> {

    List<Statistics> findAll();

    Optional<Statistics> findById(Integer id);
}
