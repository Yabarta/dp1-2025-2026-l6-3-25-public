package es.us.dp1.l6_3_24_25.Petris.match.service;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.model.TurnType;
import es.us.dp1.l6_3_24_25.Petris.match.repository.MatchRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    private MatchServiceHelper matchServiceHelper;

    @Transactional(readOnly = true)
    public List<Match> getAllMatches(){
        return matchRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Match getMatchById(Integer id){
        return matchRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Match", "Id", id));
    }

    @Transactional(readOnly = true)
    public Match getMatchByCode(String code){
        return matchRepository.findByCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Match", "Code", code));
    }

    @Transactional(readOnly = true)
    public List<Match> getCurrentMatches(){
        return matchRepository.findByEndedAtNullAndStartedAtNotNull();
    }

    @Transactional(readOnly = true)
    public List<Match> getNotStartedMatches(){
        return matchRepository.findByStartedAtNull();
    }

    @Transactional
    public Match createMatch(Match match){
        match.setCreatedAt(LocalDateTime.now());
        match.setStartedAt(null);
        match.setEndedAt(null);
        match.setPlayer1Score(0);
        match.setPlayer2Score(0);
        match.setWinner(null);
        int turn = 0;
        match.setTurn(turn);
        match.setTurnType(matchServiceHelper.getTurnTypeList().get(turn));
        List<PetriDish> initialBoardState = new ArrayList<>();
        for(int i = 0; i < 7; i++) {
            PetriDish pd = new PetriDish();
            if(i == 2) { // posicion inicial del jugador 1
                pd.setPlayer1Bacteria(1);
            } else if(i == 4) { // posicion inicial del jugador 2
                pd.setPlayer2Bacteria(1);
            }
            initialBoardState.add(pd);
        }
        match.setBoardState(initialBoardState);
        return matchRepository.save(match);
    }

    @Transactional
    public Match joinMatch(Match match) {
        match.setStartedAt(LocalDateTime.now());
        return matchRepository.save(match);
    }

    @Transactional
    public Match nextTurn(Match matchToUpdate, Optional<List<PetriDish>> newBoardState) throws IllegalArgumentException {
        Match updatedMatch = null;
        switch(matchToUpdate.getTurnType()) {
            case TurnType.P1_PROPAGATION:
                if(newBoardState.isPresent()) {
                    updatedMatch = propagation(matchToUpdate, newBoardState.get(), 1);
                } else {
                    throw new IllegalArgumentException("New board state not provided");
                }
                break;
            case TurnType.P2_PROPAGATION:
                if(newBoardState.isPresent()) {
                    updatedMatch = propagation(matchToUpdate, newBoardState.get(), 2);
                } else {
                    throw new IllegalArgumentException("New board state not provided");
                }
                break;
            case TurnType.BINARY_FISSION:
                updatedMatch = matchServiceHelper.binaryFission(matchToUpdate);
                break;
            case TurnType.CONTAMINATION:
                updatedMatch = matchServiceHelper.contamination(matchToUpdate);
        }

        int turn = matchToUpdate.getTurn() + 1;
        updatedMatch.setTurn(turn);
        updatedMatch.setTurnType(matchServiceHelper.getTurnTypeList().get(turn));

        Integer winner = matchServiceHelper.getWinner(updatedMatch);
        if(matchServiceHelper.getWinner(updatedMatch) != null) {
            updatedMatch.setEndedAt(LocalDateTime.now());
            updatedMatch.setWinner(winner);
        }
        return matchRepository.save(updatedMatch);
    }

    private Match propagation(Match matchToUpdate, List<PetriDish> newBoardState, int player) throws IllegalArgumentException{
        List<PetriDish> currentBoardState = matchToUpdate.getBoardState();
        List<String> errors = getPropagationErrors(currentBoardState, newBoardState, player);
        if(!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString());
        }
        matchToUpdate.setBoardState(newBoardState);
        return matchToUpdate;
    }

    @Transactional(readOnly = true)
    public List<String> getPropagationErrors(List<PetriDish> currentBoardState, List<PetriDish> newBoardState, int player) {
        List<String> errors = new ArrayList<>();

        Set<Integer> movedBacteriaTo = new HashSet<>();
        Integer movedBacteriaFrom = null;
        Integer movedInBacteriaNum = 0;
        int movedOutBacteriaNum = 0;
        for(int i = 0; i < 7; i++) {
            PetriDish currentPd = currentBoardState.get(i);
            PetriDish newPd = newBoardState.get(i);

            if(newPd.getPlayer1Bacteria().equals(currentPd.getPlayer2Bacteria())) {
                errors.add("Players can't have the same amount of bacteria on the same dish as another: " + "{" + i + "}");
            }

            int diffP1 = newPd.getPlayer1Bacteria() - currentPd.getPlayer1Bacteria();
            if(diffP1 != 0 && player != 1) {
                errors.add("Players can only move their own bacteria: " + "{" + i + "}");
            }
            if(diffP1 < 0){
                if(movedBacteriaFrom != null) {
                    errors.add("Players can't move bacteria from more than one petri dish: " + "{" + i + "}");
                }
                if(currentPd.getPlayer1Bacteria() == 5) {
                    errors.add("Sarcinas can't be moved: " + "{" + i + "}");
                }
                movedBacteriaFrom = i;
                movedOutBacteriaNum = -diffP1;
            } else if(diffP1 > 0) {
                movedBacteriaTo.add(i);
                movedInBacteriaNum += diffP1;
            }

            int diffP2 = newPd.getPlayer2Bacteria() - currentPd.getPlayer2Bacteria();
            if(diffP2 != 0 && player != 2) {
                errors.add("Players can only move their bacteria: " + "{" + i + "}");
            }
            if(diffP2 < 0){
                if(movedBacteriaFrom != null) {
                    errors.add("Players can't move bacteria from more than one petri dish: " + "{" + i + "}");
                }
                if(currentPd.getPlayer2Bacteria() == 5) {
                    errors.add("Sarcinas can't be moved: " + "{" + i + "}");
                }
                movedBacteriaFrom = i;
                movedOutBacteriaNum = -diffP2;
            } else if(diffP2 > 0) {
                movedBacteriaTo.add(i);
                movedInBacteriaNum += diffP2;
            }
        }

        if(movedBacteriaFrom == null) {
            errors.add("Players must move at least one bacteria: " + "{atLeastOne}");
        }
        if(!movedInBacteriaNum.equals(movedOutBacteriaNum)) {
            errors.add("Inconsistency in the number of bacteria that moved: " + "{inconsistency}");
        }
        if(!matchServiceHelper.getPetriDishAdjacencies().get(movedBacteriaFrom).containsAll(movedBacteriaTo)) {
            errors.add("Players can only move bacteria to adyacent dishes: " + "{adyacency}");
        }

        return errors;
    }

    @Transactional
    public Match forceEndMatch(Match match) {
        match.setEndedAt(LocalDateTime.now());
        return matchRepository.save(match);
    }

    @Transactional
    public void delete(Integer id){
        matchRepository.deleteById(id);
    }
}
