package es.us.dp1.l6_3_24_25.Petris.match.repository;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends CrudRepository<Match, Integer> {
    List<Match> findAll();

    List<Match> findByStartedAtNull();

    Match findByCode(String code);

    List<Match> findByEndedAtNullAndStartedAtNotNull();
}
