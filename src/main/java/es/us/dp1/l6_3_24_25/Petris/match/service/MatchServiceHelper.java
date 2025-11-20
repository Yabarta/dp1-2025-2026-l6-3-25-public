package es.us.dp1.l6_3_24_25.Petris.match.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.model.TurnType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchServiceHelper {
    private Match match;
    private List<PetriDish> boardState;
    private int player;
    private List<TurnType> turnTypeList;
    private Map<Integer, Set<Integer>> petriDishAdjacencies;
    private Integer NUM_PETRI_DISHES = 7;
    private Integer MAX_BACTERIA_PER_PETRI_DISH = 5;
    private Integer MAX_SCORE = 9;

    public List<TurnType> getTurnTypeList() {
        return List.of(
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
    }

    public Map<Integer, Set<Integer>> getPetriDishAdjacencies() {
        return Map.of(
            0, Set.of(1, 2, 3),
            1, Set.of(0, 3, 4),
            2, Set.of(0, 3, 5),
            3, Set.of(0, 1, 2, 4, 5, 6),
            4, Set.of(1, 3, 6),
            5, Set.of(2, 3, 6),
            6, Set.of(3, 4, 5)
        );
    }

    public MatchServiceHelper() {
    }

    public MatchServiceHelper(Match m, List<PetriDish> boardState, int player) {
        this.match = m;
        this.boardState = boardState;
        this.player = player;
    }

    public Match binaryFission(Match matchToUpdate) {
        List<PetriDish> newBoardState = new ArrayList<>(matchToUpdate.getBoardState());
        for(Integer i = 0; i < NUM_PETRI_DISHES; i++) {
            PetriDish newPd = newBoardState.get(i);
            if(newPd.getPlayer1Bacteria() > 0 && newPd.getPlayer1Bacteria() < MAX_BACTERIA_PER_PETRI_DISH && newPd.getPlayer2Bacteria() == 0) {
                newPd.setPlayer1Bacteria(newPd.getPlayer1Bacteria() + 1);
            } else  if(newPd.getPlayer2Bacteria() > 0 && newPd.getPlayer2Bacteria() < MAX_BACTERIA_PER_PETRI_DISH && newPd.getPlayer1Bacteria() == 0) {
                newPd.setPlayer2Bacteria(newPd.getPlayer2Bacteria() + 1);
            }
        }
        return matchToUpdate;
    }

    public Match contamination(Match matchToUpdate) {
        for(Integer i = 0; i < NUM_PETRI_DISHES; i++) {
            PetriDish pd = matchToUpdate.getBoardState().get(i);
            if(pd.getPlayer1Bacteria() > pd.getPlayer2Bacteria()) {
                matchToUpdate.setPlayer1Score(matchToUpdate.getPlayer1Score() + 1);
            } else if(pd.getPlayer1Bacteria() < pd.getPlayer2Bacteria()) {
                matchToUpdate.setPlayer2Score(matchToUpdate.getPlayer2Score() + 1);
            }
        }
        return matchToUpdate;
    }


    public Integer getWinner(Match match) {
        Integer winner = null;
        if(match.getTurn().equals(getTurnTypeList().size() - 1)) {
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
        if(match.getPlayer1Score() == MAX_SCORE) {
            if(match.getPlayer2Score() == MAX_SCORE) {
                winner = tieBreak(match);
            } else {
                winner = 2;
            }
            return winner;
        } else if(match.getPlayer2Score() == MAX_SCORE) {
            winner = 1;
            return winner;
        }
        return winner;
    }

    public Boolean hasPossibleMoves(List<PetriDish> boardState, int player) {
        Boolean res = false;
        for(Integer i = 0; i < NUM_PETRI_DISHES; i++) {
            PetriDish pd = boardState.get(i);
            int bacteria;
            if(player == 1) {
                bacteria = pd.getPlayer1Bacteria();
            } else if(player == 2) {
                bacteria = pd.getPlayer2Bacteria();
            } else {
                throw new IllegalArgumentException("player must be 1 or 2");
            }
            if(bacteria != 0 && bacteria != MAX_BACTERIA_PER_PETRI_DISH) {
                for(Integer bacteriaToMove = 1; bacteriaToMove <= bacteria; bacteriaToMove++) {
                    for(Integer target : getPetriDishAdjacencies().get(i)) {
                        if(player == 1) {
                            res = res || (boardState.get(target).getPlayer1Bacteria() != MAX_BACTERIA_PER_PETRI_DISH &&
                                boardState.get(target).getPlayer2Bacteria() != bacteriaToMove &&
                                bacteria - bacteriaToMove != boardState.get(i).getPlayer2Bacteria());
                        } else {
                            res = res || (boardState.get(target).getPlayer2Bacteria() != MAX_BACTERIA_PER_PETRI_DISH &&
                                boardState.get(target).getPlayer1Bacteria() != bacteriaToMove &&
                                bacteria - bacteriaToMove != boardState.get(i).getPlayer1Bacteria());
                        }
                    }
                }
            }
        }
        return res;
    }

    public Integer tieBreak(Match match) {
        Integer winner = null;
        int player1Tokens = 0;
        int player1Sarcinas = 0;
        int player2Tokens = 0;
        int player2Sarcinas = 0;
        for(Integer i = 0; i < NUM_PETRI_DISHES; i++) {
            PetriDish pd = match.getBoardState().get(i);
            if(pd.getPlayer1Bacteria() != MAX_BACTERIA_PER_PETRI_DISH) {
                player1Tokens += pd.getPlayer1Bacteria();
            } else {
                player1Tokens += 1;
                player1Sarcinas += 1;
            }
            if(pd.getPlayer2Bacteria() != MAX_BACTERIA_PER_PETRI_DISH) {
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
}
