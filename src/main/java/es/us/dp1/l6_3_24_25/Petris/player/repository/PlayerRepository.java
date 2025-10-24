package es.us.dp1.l6_3_24_25.Petris.player.repository;

import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

@Repository
public interface PlayerRepository extends CrudRepository<Player ,Integer>{
    List<Player> findAll();

    Player getById(Integer id);

    Player getByUsername(String username);

    
}
