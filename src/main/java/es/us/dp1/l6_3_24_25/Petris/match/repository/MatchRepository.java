package es.us.dp1.l6_3_24_25.Petris.match.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;

@Repository
public interface MatchRepository extends CrudRepository<Match, Integer> {
    List<Match> findAll();

    List<Match> findByStartedAtNull();

    Optional<Match> findByCodeAndEndedAtNull(String code);

    List<Match> findByEndedAtNullAndStartedAtNotNull();

    @Query("SELECT m FROM Match m WHERE (m.player1.id = :id OR m.player2.id = :id) AND m.endedAt IS NULL")
    Optional<Match> findMatchWithPlayerByIdAndEndedAtNull(@Param("id") Integer id);
}
