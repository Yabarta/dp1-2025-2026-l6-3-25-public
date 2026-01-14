package es.us.dp1.l6_3_24_25.Petris.friend;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipRepository extends JpaRepository<Friend, Integer> { //TODO jose tonto pon el crudRepository

    Optional<Friend> findFriendById(Integer id);

    // Encontrar amigos aceptados (complicado porque el user puede ser requester o receiver)
    @Query("SELECT f FROM Friend f WHERE (f.requester.nickname = :nickname OR f.receiver.nickname = :nickname) AND f.status = 1")
    List<Friend> findAcceptedFriendships(@Param("nickname") String nickname);

    @Query("SELECT f FROM Friend f WHERE (f.receiver.nickname = :nickname) AND f.status = 0")
    List<Friend> findRequestByPlayer(@Param("nickname") String nickname);

    @Query("SELECT f FROM Friend f WHERE (f.requester.nickname = :nickname) AND f.status = 0")
    List<Friend> findRequesterByPlayer(@Param("nickname") String nickname);

    @Query("SELECT f FROM Friend f WHERE ((f.requester.id = :idPlayer1 AND f.receiver.id = :idPlayer2) OR (f.requester.id = :idPlayer2 AND f.receiver.id = :idPlayer1)) AND f.status = 1")
    Optional<Friend> Player1IsFriendOfPlayer2(@Param("idPlayer1") Integer idPlayer1, @Param("idPlayer2") Integer idPlayer2);
}
