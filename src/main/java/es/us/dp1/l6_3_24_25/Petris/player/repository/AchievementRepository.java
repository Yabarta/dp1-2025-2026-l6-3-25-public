package es.us.dp1.l6_3_24_25.Petris.player.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import es.us.dp1.l6_3_24_25.Petris.player.model.Achievement;

@Repository
public interface AchievementRepository extends CrudRepository<Achievement, Integer> {

    List<Achievement> findAll();

    Optional<Achievement> findById(Integer id);

    Optional<Achievement> findByName(String name);
}
