package es.us.dp1.l6_3_24_25.Petris.match.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.model.TurnType;

public class MatchMethodUtil {

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
        if (currentBoardState.size() != MatchDataUtil.NUM_PETRI_DISHES || newBoardState.size() != MatchDataUtil.NUM_PETRI_DISHES) {
            errors.add("Board state must contain exactly " + MatchDataUtil.NUM_PETRI_DISHES + " dishes");
            return errors;
        }

        Set<Integer> movedBacteriaToPetriDishIndexes = new HashSet<>();
        Integer movedBacteriaFromDishIndex = null;
        int movedInBacteriaNum = 0;
        int movedOutBacteriaNum = 0;

        for (int petriDishIndex = 0; petriDishIndex < MatchDataUtil.NUM_PETRI_DISHES; petriDishIndex++) {
            PetriDish currentPd = currentBoardState.get(petriDishIndex);
            PetriDish newPd = newBoardState.get(petriDishIndex);
            if (currentPd == null || newPd == null) {
                errors.add("Invalid dish data at index: {" + petriDishIndex + "}");
                continue;
            }

            int currentP1Bacteria = currentPd.getPlayer1Bacteria();
            int currentP2Bacteria = currentPd.getPlayer2Bacteria();
            int newP1Bacteria = newPd.getPlayer1Bacteria();
            int newP2Bacteria = newPd.getPlayer2Bacteria();

            if (!isValidBacteriaNum(newP1Bacteria) || !isValidBacteriaNum(newP2Bacteria)) {
                errors.add("Bacteria count must stay between 0 and " + MatchDataUtil.MAX_BACTERIA_PER_PETRI_DISH + ": {" + petriDishIndex + "}");
                continue;
            }

            if (newP1Bacteria > 0 && newP1Bacteria == newP2Bacteria) {
                errors.add("Players can't have the same amount of bacteria on the same dish as another: {" + petriDishIndex + "}");
            }

            if ((player == 1 && newP2Bacteria != currentP2Bacteria) ||
                (player == 2 && newP1Bacteria != currentP1Bacteria)) {
                errors.add("Players can't modify the other player's bacteria: {" + petriDishIndex + "}");
            }

            int bacteriaDifferenceForPlayer = player == 1 ? newP1Bacteria - currentP1Bacteria : newP2Bacteria - currentP2Bacteria;
            int currentBacteriaForPlayer = player == 1 ? currentP1Bacteria : currentP2Bacteria;

            if (playerMovedBacteriaFromThisDish(bacteriaDifferenceForPlayer)) {

                if (movedBacteriaFromDishIndex != null) {
                    errors.add("Players can't move bacteria from more than one petri dish: {" + petriDishIndex + "," + movedBacteriaFromDishIndex + "}");
                }
                if (currentBacteriaForPlayer == MatchDataUtil.MAX_BACTERIA_PER_PETRI_DISH) {
                    errors.add("Sarcinas can't be moved: {" + petriDishIndex + "}");
                }

                movedBacteriaFromDishIndex = petriDishIndex;
                int amountMoved = Math.abs(bacteriaDifferenceForPlayer);
                movedOutBacteriaNum += amountMoved;

            } else if (playerMovedBacteriaToThisDish(bacteriaDifferenceForPlayer)) {

                movedBacteriaToPetriDishIndexes.add(petriDishIndex);
                movedInBacteriaNum += bacteriaDifferenceForPlayer;

            }
        }

        if (movedBacteriaFromDishIndex == null) {
            errors.add("Players must move at least one bacteria: {atLeastOne}");
        } else {
            Set<Integer> allowedTargets = MatchDataUtil.getPetriDishAdjacencies().get(movedBacteriaFromDishIndex);
            if (!allowedTargets.containsAll(movedBacteriaToPetriDishIndexes)) {
                errors.add("Players can only move bacteria to adyacent dishes: {adyacency}");
            }
        }
        if (movedOutBacteriaNum != movedInBacteriaNum) {
            errors.add("Inconsistency in the number of bacteria that moved: {inconsistency}");
        }

        return errors;
    }

    private static boolean isValidBacteriaNum(int bacteriaNum) {
        return bacteriaNum >= 0 && bacteriaNum <= MatchDataUtil.MAX_BACTERIA_PER_PETRI_DISH;
    }

    private static boolean playerMovedBacteriaFromThisDish(int bacteriaDifferenceForPlayer) {
        return bacteriaDifferenceForPlayer < 0;
    }

    private static boolean playerMovedBacteriaToThisDish(int bacteriaDifferenceForPlayer) {
        return bacteriaDifferenceForPlayer > 0;
    }

    public static Match binaryFission(Match matchToUpdate) {
        List<PetriDish> newBoardState = new ArrayList<>(matchToUpdate.getBoardState());
        for(Integer petriDishIndex = 0; petriDishIndex < MatchDataUtil.NUM_PETRI_DISHES; petriDishIndex++) {
            PetriDish newPd = newBoardState.get(petriDishIndex);
            if(newPd.getPlayer1Bacteria() > 0 && newPd.getPlayer1Bacteria() < MatchDataUtil.MAX_BACTERIA_PER_PETRI_DISH && newPd.getPlayer2Bacteria() == 0) {
                newPd.setPlayer1Bacteria(newPd.getPlayer1Bacteria() + 1);
            } else if(newPd.getPlayer2Bacteria() > 0 && newPd.getPlayer2Bacteria() < MatchDataUtil.MAX_BACTERIA_PER_PETRI_DISH && newPd.getPlayer1Bacteria() == 0) {
                newPd.setPlayer2Bacteria(newPd.getPlayer2Bacteria() + 1);
            }
        }
        return matchToUpdate;
    }

    public static Match contamination(Match matchToUpdate) {
        for(Integer petriDishIndex = 0; petriDishIndex < MatchDataUtil.NUM_PETRI_DISHES; petriDishIndex++) {
            PetriDish pd = matchToUpdate.getBoardState().get(petriDishIndex);
            if(pd.getPlayer1Bacteria() > pd.getPlayer2Bacteria()) {
                int newScore = Math.min(MatchDataUtil.MAX_SCORE, matchToUpdate.getPlayer1Score() + 1);
                matchToUpdate.setPlayer1Score(newScore);
            } else if(pd.getPlayer1Bacteria() < pd.getPlayer2Bacteria()) {
                int newScore = Math.min(MatchDataUtil.MAX_SCORE, matchToUpdate.getPlayer2Score() + 1);
                matchToUpdate.setPlayer2Score(newScore);
            }
        }
        return matchToUpdate;
    }


    public static Integer getWinner(Match match) {
        if (match.isPastLastTurn()) {
            int player1Score = match.getPlayer1Score();
            int player2Score = match.getPlayer2Score();
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

        int player1Score = match.getPlayer1Score();
        int player2Score = match.getPlayer2Score();
        if (player1Score >= MatchDataUtil.MAX_SCORE && player2Score >= MatchDataUtil.MAX_SCORE) {
            return tieBreak(match);
        }
        if (player1Score >= MatchDataUtil.MAX_SCORE) {
            return 2;
        }
        if (player2Score >= MatchDataUtil.MAX_SCORE) {
            return 1;
        }
        return null;
    }

    private static boolean hasPossibleMoves(List<PetriDish> boardState, int player) {
        for (int petriDishIndex = 0; petriDishIndex < MatchDataUtil.NUM_PETRI_DISHES; petriDishIndex++) {
            PetriDish pd = boardState.get(petriDishIndex);
            int bacteriaNumForPlayer = player == 1 ? pd.getPlayer1Bacteria() : pd.getPlayer2Bacteria();
            int bacteriaNumForOpponent = player == 1 ? pd.getPlayer2Bacteria() : pd.getPlayer1Bacteria();

            if (bacteriaNumForPlayer <= 0 || bacteriaNumForPlayer == MatchDataUtil.MAX_BACTERIA_PER_PETRI_DISH) {
                continue;
            }

            for (int bacteriaToMove = 1; bacteriaToMove <= MatchDataUtil.MAX_BACTERIA_PER_PETRI_DISH - 1; bacteriaToMove++) {
                if (bacteriaNumForOpponent > 0 && bacteriaNumForPlayer - bacteriaToMove == bacteriaNumForOpponent) {
                    continue;
                }

                Set<Integer> adjacencies = MatchDataUtil.getPetriDishAdjacencies().get(petriDishIndex);
                for (Integer target : adjacencies) {
                    PetriDish targetDish = boardState.get(target);
                    int targetBacteriaNumForPlayer = player == 1 ? targetDish.getPlayer1Bacteria() : targetDish.getPlayer2Bacteria();
                    int targetBacteriaNumForOpponent = player == 1 ? targetDish.getPlayer2Bacteria() : targetDish.getPlayer1Bacteria();

                    if (targetBacteriaNumForPlayer + bacteriaToMove > MatchDataUtil.MAX_BACTERIA_PER_PETRI_DISH) {
                        continue;
                    }
                    if (targetBacteriaNumForOpponent > 0 && targetBacteriaNumForOpponent == bacteriaToMove) {
                        continue;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private static Integer tieBreak(Match match) {
        Integer winner = null;
        int player1Tokens = 0;
        int player1Sarcinas = 0;
        int player2Tokens = 0;
        int player2Sarcinas = 0;
        for(Integer petriDishIndex = 0; petriDishIndex < MatchDataUtil.NUM_PETRI_DISHES; petriDishIndex++) {
            PetriDish pd = match.getBoardState().get(petriDishIndex);
            if(pd.getPlayer1Bacteria() != MatchDataUtil.MAX_BACTERIA_PER_PETRI_DISH) {
                player1Tokens += pd.getPlayer1Bacteria();
            } else {
                player1Tokens += 1;
                player1Sarcinas += 1;
            }
            if(pd.getPlayer2Bacteria() != MatchDataUtil.MAX_BACTERIA_PER_PETRI_DISH) {
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
