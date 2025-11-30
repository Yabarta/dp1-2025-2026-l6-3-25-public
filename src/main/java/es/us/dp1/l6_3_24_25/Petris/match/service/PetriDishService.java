package es.us.dp1.l6_3_24_25.Petris.match.service;

import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.repository.PetriDishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetriDishService {

    @Autowired
    private PetriDishRepository pdRepository;

    @Transactional
    public PetriDish save(PetriDish pd){
        return pdRepository.save(pd);
    }

    public void delete(Integer id){
        pdRepository.deleteById(id);
    }
}