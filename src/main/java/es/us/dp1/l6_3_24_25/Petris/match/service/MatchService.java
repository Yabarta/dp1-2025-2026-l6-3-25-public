package es.us.dp1.l6_3_24_25.Petris.match.service;

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

    public static final List<TurnType> turnTypes = List.of(
        TurnType.P1_PROPAGATION,
        TurnType.P2_PROPAGATION,
        TurnType.BINARY_FISSION,
        TurnType.P2_PROPAGATION,
        TurnType.P1_PROPAGATION,
        TurnType.BINARY_FISSION,
        TurnType.P1_PROPAGATION,
        TurnType.P2_PROPAGATION,
        TurnType.BINARY_FISSION,
        TurnType.CONTAMINATION,

        TurnType.P2_PROPAGATION,
        TurnType.P1_PROPAGATION,
        TurnType.BINARY_FISSION,
        TurnType.P1_PROPAGATION,
        TurnType.P2_PROPAGATION,
        TurnType.BINARY_FISSION,
        TurnType.P2_PROPAGATION,
        TurnType.P1_PROPAGATION,
        TurnType.BINARY_FISSION,
        TurnType.CONTAMINATION,

        TurnType.P1_PROPAGATION,
        TurnType.P2_PROPAGATION,
        TurnType.BINARY_FISSION,
        TurnType.P2_PROPAGATION,
        TurnType.P1_PROPAGATION,
        TurnType.BINARY_FISSION,
        TurnType.P1_PROPAGATION,
        TurnType.P2_PROPAGATION,
        TurnType.BINARY_FISSION,
        TurnType.CONTAMINATION,
        
        TurnType.P2_PROPAGATION,
        TurnType.P1_PROPAGATION,
        TurnType.BINARY_FISSION,
        TurnType.P1_PROPAGATION,
        TurnType.P2_PROPAGATION,
        TurnType.BINARY_FISSION,
        TurnType.P2_PROPAGATION,
        TurnType.P1_PROPAGATION,
        TurnType.BINARY_FISSION,
        TurnType.CONTAMINATION
    );

    public static int player1Dish = 2;
    public static int player2Dish = 4;

    public static final Map<Integer,Set<Integer>> petriDishAdjacencies = Map.of(
        0, Set.of(1, 2, 3),
        1, Set.of(0, 3, 4),
        2, Set.of(0, 3, 5),
        3, Set.of(0, 1, 2, 4, 5, 6),
        4, Set.of(1, 3, 6),
        5, Set.of(2, 3, 6),
        6, Set.of(3, 4, 5)
    );

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
        Integer turn = 0;
        match.setTurn(turn);
        match.setTurnType(turnTypes.get(turn));
        List<PetriDish> initialBoardState = new ArrayList<>();
        for(int i = 0; i < 7; i++) {
            PetriDish pd = new PetriDish();
            if(i == player1Dish) {
                pd.setPlayer1Bacteria(1);
            } else if(i == player2Dish) {
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
                updatedMatch = binaryFission(matchToUpdate);
                break;
            case TurnType.CONTAMINATION:
                updatedMatch = contamination(matchToUpdate);
        }

        Integer turn = matchToUpdate.getTurn() + 1;
        updatedMatch.setTurn(turn);
        updatedMatch.setTurnType(turnTypes.get(turn));

        Integer winner = getWinner(updatedMatch);
        if(getWinner(updatedMatch) != null) {
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

    private Match binaryFission(Match matchToUpdate) {
        List<PetriDish> newBoardState = new ArrayList<>(matchToUpdate.getBoardState());
        for(Integer i = 0; i < 7; i++) {
            PetriDish newPd = newBoardState.get(i);
            if(newPd.getPlayer1Bacteria() > 0 && newPd.getPlayer1Bacteria() < 5 && newPd.getPlayer2Bacteria() == 0) {
                newPd.setPlayer1Bacteria(newPd.getPlayer1Bacteria() + 1);
            } else  if(newPd.getPlayer2Bacteria() > 0 && newPd.getPlayer2Bacteria() < 5 && newPd.getPlayer1Bacteria() == 0) {
                newPd.setPlayer2Bacteria(newPd.getPlayer2Bacteria() + 1);
            }
        }
        return matchToUpdate;
    }

    private Match contamination(Match matchToUpdate) {
        for(Integer i = 0; i < 7; i++) {
            PetriDish pd = matchToUpdate.getBoardState().get(i);
            if(pd.getPlayer1Bacteria() > pd.getPlayer2Bacteria()) {
                matchToUpdate.setPlayer1Score(matchToUpdate.getPlayer1Score() + 1);
            } else if(pd.getPlayer1Bacteria() < pd.getPlayer2Bacteria()) {
                matchToUpdate.setPlayer2Score(matchToUpdate.getPlayer2Score() + 1);
            }
        }
        return matchToUpdate;
    }

    @Transactional(readOnly = true)
    public List<String> getPropagationErrors(List<PetriDish> currentBoardState, List<PetriDish> newBoardState, int player) {
        List<String> errors = new ArrayList<>();

        Set<Integer> movedBacteriaTo = new HashSet<>();
        Integer movedBacteriaFrom = null;
        Integer movedInBacteriaNum = 0;
        Integer movedOutBacteriaNum = 0;
        for(Integer i = 0; i < 7; i++) {
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
        if(!petriDishAdjacencies.get(movedBacteriaFrom).containsAll(movedBacteriaTo)) {
            errors.add("Players can only move bacteria to adyacent dishes: " + "{adyacency}");
        }

        return errors;
    }

    private Integer getWinner(Match match) {
        Integer winner = null;
        if(match.getTurn().equals(turnTypes.size() - 1)) {
            if(match.getPlayer1Score() < match.getPlayer2Score()) {
                winner = 1;
            } else if(match.getPlayer1Score() > match.getPlayer2Score()) {
                winner = 2;
            } else {
                winner = tieBreak(match);
            }
            return winner;
        }
        if(match.getTurnType().equals(TurnType.P1_PROPAGATION)) {
            if(!hasPossibleMoves(match.getBoardState(), 1)) {
                winner = 2;
                return winner;
            }
        }
        if(match.getTurnType().equals(TurnType.P2_PROPAGATION)) {
            if(!hasPossibleMoves(match.getBoardState(), 2)) {
                winner = 1;
                return winner;
            }
        }
        if(match.getPlayer1Score() == 9) {
            if(match.getPlayer2Score() == 9) {
                winner = tieBreak(match);
            } else {
                winner = 2;
            }
            return winner;
        } else if(match.getPlayer2Score() == 9) {
            winner = 1;
            return winner;
        }
        return winner;
    }

    private Boolean hasPossibleMoves(List<PetriDish> boardState, int player) {
        Boolean res = false;
        for(Integer i = 0; i < 7; i++) {
            PetriDish pd = boardState.get(i);
            int bacteria;
            if(player == 1) {
                bacteria = pd.getPlayer1Bacteria();
            } else if(player == 2) {
                bacteria = pd.getPlayer2Bacteria();
            } else {
                throw new IllegalArgumentException("player must be 1 or 2");
            }
            if(bacteria != 0 && bacteria != 5) {
                for(Integer bacteriaToMove = 1; bacteriaToMove <= bacteria; bacteriaToMove++) {
                    for(Integer target : petriDishAdjacencies.get(i)) {
                        if(player == 1) {
                            res = res || (boardState.get(target).getPlayer1Bacteria() != 5 &&
                                          boardState.get(target).getPlayer2Bacteria() != bacteriaToMove &&
                                          bacteria - bacteriaToMove != boardState.get(i).getPlayer2Bacteria());
                        } else {
                            res = res || (boardState.get(target).getPlayer2Bacteria() != 5 &&
                                          boardState.get(target).getPlayer1Bacteria() != bacteriaToMove &&
                                          bacteria - bacteriaToMove != boardState.get(i).getPlayer1Bacteria());
                        }
                    }
                }
            }
        }
        return res;
    }

    private Integer tieBreak(Match match) {
        Integer winner = null;
        int player1Tokens = 0;
        int player1Sarcinas = 0;
        int player2Tokens = 0;
        int player2Sarcinas = 0;
        for(Integer i = 0; i < 7; i++) {
            PetriDish pd = match.getBoardState().get(i);
            if(pd.getPlayer1Bacteria() != 5) {
                player1Tokens += pd.getPlayer1Bacteria();
            } else {
                player1Tokens += 1;
                player1Sarcinas += 1;
            }
            if(pd.getPlayer2Bacteria() != 5) {
                player2Tokens += pd.getPlayer2Bacteria();
            } else {
                player2Tokens += 1;
                player2Sarcinas += 1;
            }
        }
        if(player1Tokens < player2Tokens) {
            winner = 1;
        } else if(player1Tokens > player2Tokens) {
            winner = 2;
        } else if(player1Sarcinas < player2Sarcinas) {
            winner = 1;
        } else {
            winner = 2;
        }
        return winner;
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
