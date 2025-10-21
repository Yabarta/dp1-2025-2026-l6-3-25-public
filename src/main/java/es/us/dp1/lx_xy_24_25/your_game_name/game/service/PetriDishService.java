package es.us.dp1.lx_xy_24_25.your_game_name.game.service;

import es.us.dp1.lx_xy_24_25.your_game_name.game.model.PetriDish;
import es.us.dp1.lx_xy_24_25.your_game_name.game.repository.PetriDishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetriDishService {

    @Autowired
    private PetriDishRepository pdRepository;

    @Transactional(readOnly = true)
    public PetriDish getPetriDish(Integer index){
        return pdRepository.findPetriDishByIndex(index);
    }

    public PetriDish save(PetriDish pd){
        return pdRepository.save(pd);
    }
}
