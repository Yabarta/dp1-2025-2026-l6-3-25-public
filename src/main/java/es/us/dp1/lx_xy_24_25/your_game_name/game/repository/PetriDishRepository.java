package es.us.dp1.lx_xy_24_25.your_game_name.game.repository;

import es.us.dp1.lx_xy_24_25.your_game_name.game.model.PetriDish;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetriDishRepository extends CrudRepository<PetriDish, Integer> {
    PetriDish findPetriDishByIndex(Integer index);
}
