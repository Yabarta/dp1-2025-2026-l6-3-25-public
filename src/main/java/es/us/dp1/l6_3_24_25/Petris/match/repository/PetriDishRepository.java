package es.us.dp1.l6_3_24_25.Petris.match.repository;

import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetriDishRepository extends CrudRepository<PetriDish, Integer> {
    
}
