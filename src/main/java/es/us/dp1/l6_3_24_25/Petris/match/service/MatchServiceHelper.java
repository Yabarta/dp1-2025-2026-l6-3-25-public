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
    private Integer MAX_MOVABLE_BACTERIA = 4;

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
                int newScore = Math.min(MAX_SCORE, matchToUpdate.getPlayer1Score() + 1);
                matchToUpdate.setPlayer1Score(newScore);
            } else if(pd.getPlayer1Bacteria() < pd.getPlayer2Bacteria()) {
                int newScore = Math.min(MAX_SCORE, matchToUpdate.getPlayer2Score() + 1);
                matchToUpdate.setPlayer2Score(newScore);
            }
        }
        return matchToUpdate;
    }


    public Integer getWinner(Match match) {
        if (match == null) {
            return null;
        }
        int currentTurn = match.getTurn() == null ? 0 : match.getTurn();
        List<TurnType> sequence = getTurnTypeList();
        if (currentTurn >= sequence.size()) {
            int player1Score = safeScore(match.getPlayer1Score());
            int player2Score = safeScore(match.getPlayer2Score());
            if (player1Score < player2Score) {
                return 1;
            } else if (player1Score > player2Score) {
                return 2;
            }
            return tieBreak(match);
        }

        TurnType turnType = match.getTurnType();
        if (turnType == TurnType.P1_PROPAGATION && !hasPossibleMoves(match.getBoardState(), 1)) {
            return 2;
        }
        if (turnType == TurnType.P2_PROPAGATION && !hasPossibleMoves(match.getBoardState(), 2)) {
            return 1;
        }

        int player1Score = safeScore(match.getPlayer1Score());
        int player2Score = safeScore(match.getPlayer2Score());
        if (player1Score >= MAX_SCORE && player2Score >= MAX_SCORE) {
            return tieBreak(match);
        }
        if (player1Score >= MAX_SCORE) {
            return 2;
        }
        if (player2Score >= MAX_SCORE) {
            return 1;
        }
        return null;
    }

    public Boolean hasPossibleMoves(List<PetriDish> boardState, int player) {
        if (boardState == null || boardState.size() < NUM_PETRI_DISHES) {
            return false;
        }
        for (int i = 0; i < NUM_PETRI_DISHES; i++) {
            PetriDish pd = boardState.get(i);
            if (pd == null) {
                continue;
            }
            int myBacteria = player == 1 ? safeCount(pd.getPlayer1Bacteria()) : safeCount(pd.getPlayer2Bacteria());
            int opponentBacteria = player == 1 ? safeCount(pd.getPlayer2Bacteria()) : safeCount(pd.getPlayer1Bacteria());

            if (myBacteria <= 0 || myBacteria == MAX_BACTERIA_PER_PETRI_DISH) {
                continue;
            }

            int maxMovable = Math.min(myBacteria, MAX_MOVABLE_BACTERIA);
            for (int amount = 1; amount <= maxMovable; amount++) {
                if (opponentBacteria > 0 && myBacteria - amount == opponentBacteria) {
                    continue;
                }
                Set<Integer> adjacencies = getPetriDishAdjacencies().get(i);
                if (adjacencies == null) {
                    continue;
                }
                for (Integer target : adjacencies) {
                    PetriDish targetDish = boardState.get(target);
                    if (targetDish == null) {
                        continue;
                    }
                    int targetMyCount = player == 1 ? safeCount(targetDish.getPlayer1Bacteria()) : safeCount(targetDish.getPlayer2Bacteria());
                    int targetOpponentCount = player == 1 ? safeCount(targetDish.getPlayer2Bacteria()) : safeCount(targetDish.getPlayer1Bacteria());

                    if (targetMyCount >= MAX_BACTERIA_PER_PETRI_DISH) {
                        continue;
                    }
                    if (targetMyCount + amount > MAX_BACTERIA_PER_PETRI_DISH) {
                        continue;
                    }
                    if (targetOpponentCount > 0 && targetOpponentCount == amount) {
                        continue;
                    }
                    return true;
                }
            }
        }
        return false;
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

    private int safeCount(Integer value) {
        return value == null ? 0 : value;
    }

    private int safeScore(Integer value) {
        return value == null ? 0 : value;
    }
}
