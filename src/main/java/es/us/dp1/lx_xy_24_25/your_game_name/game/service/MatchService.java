package es.us.dp1.lx_xy_24_25.your_game_name.game.service;

import es.us.dp1.lx_xy_24_25.your_game_name.game.model.Match;
import es.us.dp1.lx_xy_24_25.your_game_name.game.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    @Transactional(readOnly = true)
    public List<Match> getAllMatches(){
        return matchRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Match getMatchById(Integer id){
        return matchRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Match getMatchByCode(String code){
        return matchRepository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public List<Match> getCurrentMatches(){
        return matchRepository.findByEndedAtNull();
    }

    @Transactional(readOnly = true)
    public List<Match> getNotStartedMatches(){
        return matchRepository.findByStartedAtNull();
    }

    @Transactional
    public void save(Match match){
        matchRepository.save(match);
    }

    @Transactional
    public void delete(Integer id){
        matchRepository.deleteById(id);
    }
}
