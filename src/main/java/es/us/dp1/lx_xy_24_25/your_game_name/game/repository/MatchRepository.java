package es.us.dp1.lx_xy_24_25.your_game_name.game.repository;

import es.us.dp1.lx_xy_24_25.your_game_name.game.model.Match;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends CrudRepository<Match, Integer> {
    List<Match> findAll();

    List<Match> findByEndedAtNull();

    List<Match> findByStartedAtNull();

    Match findByCode(String code);
}
