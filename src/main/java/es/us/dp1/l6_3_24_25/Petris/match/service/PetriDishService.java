package es.us.dp1.l6_3_24_25.Petris.match.service;

import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.repository.PetriDishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PetriDishService {

    @Autowired
    private PetriDishRepository pdRepository;

    @Transactional(readOnly = true)
    public PetriDish getPetriDishByIndex(Integer index){
        return pdRepository.findPetriDishByIndex(index);
    }

    @Transactional(readOnly = true)
    public PetriDish getPetriDishById(Integer id){
        return pdRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<PetriDish> getAllPetriDishes(){
        return (List<PetriDish>) pdRepository.findAll();
    }

    public PetriDish save(PetriDish pd){
        return pdRepository.save(pd);
    }

    public void delete(Integer id){
        pdRepository.deleteById(id);
    }
}