package es.us.dp1.l6_3_24_25.Petris.player.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.user.User;

@Repository
public interface PlayerRepository extends CrudRepository<Player ,Integer>{
    List<Player> findAll();

    Optional<Player> getByUser(User user);

    Optional<Player> findById(Integer id);
    
    Optional<Player> getByNickname(String nickname);

    
    @Query("SELECT p FROM Player p JOIN p.user u WHERE u.username = ?1")
    Optional<Player> getByUsername(String username);

}
