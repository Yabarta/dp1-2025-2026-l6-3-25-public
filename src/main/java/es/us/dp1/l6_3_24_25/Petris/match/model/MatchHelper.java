package es.us.dp1.l6_3_24_25.Petris.match.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchHelper {
    private Match match;
    private List<PetriDish> boardState;
    private int player;
    public static final int NUM_PETRI_DISHES = 7;
    public static final int PLAYER_1_INTITIAL_BACTERIUM_INDEX = 2;
    public static final int PLAYER_2_INTITIAL_BACTERIUM_INDEX = 4;
    public static final Integer MAX_BACTERIA_PER_PETRI_DISH = 5;
    public static final Integer MAX_SCORE = 9;
    public static final Integer MAX_MOVABLE_BACTERIA = 4;

    private static final List<TurnType> turnTypeList = List.of(
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

    public static final TurnType getTurnType(int turn) {
        return turnTypeList.get(turn);
    }
    public static final int getTurnsNum() {
        return turnTypeList.size();
    }

    public static Map<Integer, Set<Integer>> getPetriDishAdjacencies() {
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

    public static Match buildInitialMatch(Player creator, Boolean isPrivate) {
        Match initialMatch = new Match();
        initialMatch.setCreator(creator);
        initialMatch.setPlayer1(creator);

        initialMatch.setCreatedAt(LocalDateTime.now());
        initialMatch.setStartedAt(null);
        initialMatch.setEndedAt(null);
        initialMatch.setPlayer1Score(0);
        initialMatch.setPlayer2Score(0);
        initialMatch.setWinner(null);

        Integer turn = 0;
        initialMatch.setTurn(turn);
        initialMatch.setTurnType(getTurnType(turn));
        List<PetriDish> initialBoardState = new ArrayList<>();
        for(int petriDishIndex = 0; petriDishIndex < NUM_PETRI_DISHES; petriDishIndex++) {
            PetriDish pd = new PetriDish();
            if(petriDishIndex == PLAYER_1_INTITIAL_BACTERIUM_INDEX) {
                pd.setPlayer1Bacteria(1);
            } else if(petriDishIndex == PLAYER_2_INTITIAL_BACTERIUM_INDEX) {
                pd.setPlayer2Bacteria(1);
            }
            initialBoardState.add(pd);
        }
        initialMatch.setBoardState(initialBoardState);
        return initialMatch;
    }


    public static Match propagation(Match matchToUpdate, List<PetriDish> newBoardState, int player) throws IllegalArgumentException{
        List<PetriDish> currentBoardState = matchToUpdate.getBoardState();
        List<String> errors = getPropagationErrors(currentBoardState, newBoardState, player);
        if(!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString());
        }
        matchToUpdate.setBoardState(newBoardState);
        return matchToUpdate;
    }

    public static List<String> getPropagationErrors(List<PetriDish> currentBoardState, List<PetriDish> newBoardState, int player) {
        List<String> errors = new ArrayList<>();
        if (currentBoardState == null || newBoardState == null) {
            errors.add("Board state is missing");
            return errors;
        }
        if (currentBoardState.size() != MatchHelper.NUM_PETRI_DISHES || newBoardState.size() != MatchHelper.NUM_PETRI_DISHES) {
            errors.add("Board state must contain exactly " + MatchHelper.NUM_PETRI_DISHES + " dishes");
            return errors;
        }

        Set<Integer> movedBacteriaTo = new HashSet<>();
        Integer movedBacteriaFrom = null;
        int movedInBacteriaNum = 0;
        int movedOutBacteriaNum = 0;

        for (int i = 0; i < MatchHelper.NUM_PETRI_DISHES; i++) {
            PetriDish currentPd = currentBoardState.get(i);
            PetriDish newPd = newBoardState.get(i);
            if (currentPd == null || newPd == null) {
                errors.add("Invalid dish data at index: {" + i + "}");
                continue;
            }

            int currentP1 = normalizeCount(currentPd.getPlayer1Bacteria());
            int currentP2 = normalizeCount(currentPd.getPlayer2Bacteria());
            int newP1 = normalizeCount(newPd.getPlayer1Bacteria());
            int newP2 = normalizeCount(newPd.getPlayer2Bacteria());

            if (!isValidCount(newP1) || !isValidCount(newP2)) {
                errors.add("Bacteria count must stay between 0 and " + MAX_BACTERIA_PER_PETRI_DISH + ": {" + i + "}");
            }

            if (currentP2 > 0 && newP1 == currentP2) {
                errors.add("Players can't have the same amount of bacteria on the same dish as another: {" + i + "}");
            }
            if (currentP1 > 0 && newP2 == currentP1) {
                errors.add("Players can't have the same amount of bacteria on the same dish as another: {" + i + "}");
            }

            if (player == 1 && newP2 != currentP2) {
                errors.add("Player 1 can't modify Player 2 bacteria: {" + i + "}");
            }
            if (player == 2 && newP1 != currentP1) {
                errors.add("Player 2 can't modify Player 1 bacteria: {" + i + "}");
            }

            int diffForPlayer = player == 1 ? newP1 - currentP1 : newP2 - currentP2;
            int opponentCount = player == 1 ? currentP2 : currentP1;

            if (diffForPlayer < 0) {
                int availableAtSource = player == 1 ? currentP1 : currentP2;
                if ((player == 1 && currentP1 == MAX_BACTERIA_PER_PETRI_DISH) || (player == 2 && currentP2 == MAX_BACTERIA_PER_PETRI_DISH)) {
                    errors.add("Sarcinas can't be moved: {" + i + "}");
                }
                if (movedBacteriaFrom != null && !movedBacteriaFrom.equals(i)) {
                    errors.add("Players can't move bacteria from more than one petri dish: {" + i + "}");
                }
                movedBacteriaFrom = i;
                int amountMoved = Math.abs(diffForPlayer);
                movedOutBacteriaNum += amountMoved;
                if (amountMoved > availableAtSource) {
                    errors.add("Players can't move more bacteria than available on the origin dish: {" + i + "}");
                }
                if (amountMoved > MAX_MOVABLE_BACTERIA) {
                    errors.add("Players can't move more than " + MAX_MOVABLE_BACTERIA + " bacteria per turn: {" + i + "}");
                }
                if (opponentCount > 0 && availableAtSource - amountMoved == opponentCount) {
                    errors.add("Players can't leave the same amount of bacteria as the opponent on the origin dish: {" + i + "}");
                }
            } else if (diffForPlayer > 0) {
                movedBacteriaTo.add(i);
                movedInBacteriaNum += diffForPlayer;
                if (player == 1 && newP1 > MAX_BACTERIA_PER_PETRI_DISH || player == 2 && newP2 > MAX_BACTERIA_PER_PETRI_DISH) {
                    errors.add("Players can't exceed the maximum number of bacteria per dish: {" + i + "}");
                }
                if (opponentCount > 0 && opponentCount == diffForPlayer) {
                    errors.add("Players can't move the same amount of bacteria as the opponent has on the target dish: {" + i + "}");
                }
            }
        }

        if (movedBacteriaFrom == null) {
            errors.add("Players must move at least one bacteria: {atLeastOne}");
        }
        if (movedOutBacteriaNum != movedInBacteriaNum) {
            errors.add("Inconsistency in the number of bacteria that moved: {inconsistency}");
        }
        if (movedOutBacteriaNum > MAX_MOVABLE_BACTERIA) {
            errors.add("Players can't move more than " + MAX_MOVABLE_BACTERIA + " bacteria per turn: {tooMany}");
        }
        if (movedBacteriaFrom != null) {
            Set<Integer> allowedTargets = MatchHelper.getPetriDishAdjacencies().get(movedBacteriaFrom);
            if (allowedTargets == null || !allowedTargets.containsAll(movedBacteriaTo)) {
                errors.add("Players can only move bacteria to adyacent dishes: {adyacency}");
            }
        }

        return errors;
    }

    private static int normalizeCount(Integer value) {
        return value == null ? 0 : value;
    }

    private static boolean isValidCount(int value) {
        return value >= 0 && value <= MAX_BACTERIA_PER_PETRI_DISH;
    }

    public static Match binaryFission(Match matchToUpdate) {
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

    public static Match contamination(Match matchToUpdate) {
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


    public static Integer getWinner(Match match) {
        if (match == null) {
            return null;
        }
        int currentTurn = match.getTurn() == null ? 0 : match.getTurn();
        if (currentTurn == getTurnsNum()) {
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

    public static Boolean hasPossibleMoves(List<PetriDish> boardState, int player) {
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

    public static Integer tieBreak(Match match) {
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

    private static int safeCount(Integer value) {
        return value == null ? 0 : value;
    }

    private static int safeScore(Integer value) {
        return value == null ? 0 : value;
    }
}
