package es.us.dp1.l6_3_24_25.Petris.player.repository;

import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.user.User;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

@Repository
public interface PlayerRepository extends CrudRepository<Player ,Integer>{
    List<Player> findAll();

    Player getByUser(User user);

    Player getById(Integer id);

    Player getByNickname(String nickname);

}
