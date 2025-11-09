package es.us.dp1.l6_3_24_25.Petris.match.repository;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends CrudRepository<Match, Integer> {
    List<Match> findAll();

    List<Match> findByStartedAtNull();

    Optional<Match> findByCode(String code);

    List<Match> findByEndedAtNullAndStartedAtNotNull();
}
