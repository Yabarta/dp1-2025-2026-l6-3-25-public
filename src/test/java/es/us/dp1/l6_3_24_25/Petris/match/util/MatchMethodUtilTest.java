package es.us.dp1.l6_3_24_25.Petris.match.util;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;

import static generatedAssertions.org.assertj.Assertions.assertThat;

@Epic("Game")
@Feature("Utility for the handling of matches")
public class MatchMethodUtilTest {

    @Test
    @DisplayName("Should return error if board state missing")
    @Description("Test that if propagation is attempted without providing a boardState, IllegalArgumentException is thrown")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    void testPropagationNegative() {

        List<PetriDish> currentBoardState = null;
        Match matchToUpdate = new Match();
        matchToUpdate.setBoardState(currentBoardState);
        List<PetriDish> newBoardState = null;

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> MatchMethodUtil.propagation(matchToUpdate, newBoardState, 1))
            .withMessageContaining("Board state is missing");
    }

    @Test
    @DisplayName("Should return error if board state of wrong size")
    @Description("Test that if propagation is attempted providing a boardState of wrong size, IllegalArgumentException is thrown")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    void testPropagationNegative2() {

        List<PetriDish> currentBoardState = List.of();
        Match matchToUpdate = new Match();
        matchToUpdate.setBoardState(currentBoardState);
        List<PetriDish> newBoardState = List.of();

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> MatchMethodUtil.propagation(matchToUpdate, newBoardState, 1))
            .withMessageContaining("Board state must contain exactly " + MatchDataUtil.NUM_PETRI_DISHES + " dishes");
    }

    @ParameterizedTest
    @DisplayName("Should return error if board state contains wrong Petri dish data")
    @Description("Test that if propagation is attempted providing a boardState with wrong petriDish data, IllegalArgumentException is thrown")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testPropagationNegative3WithValueSource(int petriDishIndex) {

        List<PetriDish> currentBoardState = new ArrayList<>();
            currentBoardState.add(PetriDish.of(1,1));
            currentBoardState.add(null);
            currentBoardState.add(PetriDish.of(1,1));
            currentBoardState.add(null);
            currentBoardState.add(PetriDish.of(1,1));
            currentBoardState.add(null);
            currentBoardState.add(PetriDish.of(1,1));
        Match matchToUpdate = new Match();
        matchToUpdate.setBoardState(currentBoardState);
        List<PetriDish> newBoardState = new ArrayList<>();
            newBoardState.add(null);
            newBoardState.add(PetriDish.of(1,1));
            newBoardState.add(null);
            newBoardState.add(PetriDish.of(1,1));
            newBoardState.add(null);
            newBoardState.add(PetriDish.of(1,1));
            newBoardState.add(null);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> MatchMethodUtil.propagation(matchToUpdate, newBoardState, 1))
            .withMessageContaining("Invalid dish data at index: {" + petriDishIndex + "}");
    }

    @ParameterizedTest
    @DisplayName("Should return error if new board state contains wrong bacteria amounts")
    @Description("Test that if propagation is attempted providing a boardState that contains wrong playerXBacteria, IllegalArgumentException is thrown")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testPropagationNegative4WithValueSource(int petriDishIndex) {

        List<PetriDish> currentBoardState = List.of(
            PetriDish.of(1,1),
            PetriDish.of(1,1),
            PetriDish.of(1,1),
            PetriDish.of(1,1),
            PetriDish.of(1,1),
            PetriDish.of(1,1),
            PetriDish.of(1,1));
        Match matchToUpdate = new Match();
        matchToUpdate.setBoardState(currentBoardState);
        List<PetriDish> newBoardState = List.of(
            PetriDish.of(6,1),
            PetriDish.of(1,-1),
            PetriDish.of(7,1),
            PetriDish.of(1,-2),
            PetriDish.of(8,1),
            PetriDish.of(1,-3),
            PetriDish.of(9,1));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> MatchMethodUtil.propagation(matchToUpdate, newBoardState, 1))
            .withMessageContaining("Bacteria count must stay between 0 and " + MatchDataUtil.MAX_BACTERIA_PER_PETRI_DISH + ": {" + petriDishIndex + "}");
    }

    @ParameterizedTest
    @DisplayName("Should return error if same bacteria amount on dish")
    @Description("Test that if newBoardState has the same amounts for player1Bacteria and player2Bacteria, IllegalArgumentException is thrown")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testPropagationNegative5WithValueSource(int petriDishIndex) {

        List<PetriDish> currentBoardState = List.of(
            PetriDish.of(0,1),
            PetriDish.of(0,1),
            PetriDish.of(0,1),
            PetriDish.of(0,1),
            PetriDish.of(0,1),
            PetriDish.of(0,1),
            PetriDish.of(0,1));
        Match matchToUpdate = new Match();
        matchToUpdate.setBoardState(currentBoardState);
        List<PetriDish> newBoardState = List.of(
            PetriDish.of(1,1),
            PetriDish.of(1,1),
            PetriDish.of(1,1),
            PetriDish.of(1,1),
            PetriDish.of(1,1),
            PetriDish.of(1,1),
            PetriDish.of(1,1));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> MatchMethodUtil.propagation(matchToUpdate, newBoardState, 1))
            .withMessageContaining("Players can't have the same amount of bacteria on the same dish as another: {" + petriDishIndex + "}");
    }

    @Test
    @DisplayName("Should return error if player attempts to move bacteria not their own")
    @Description("Test that if playerX provides newBoardState that changes PlayerNotXBacteria, IllegalArgumentException is thrown")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    void testPropagationNegative6() {

        List<PetriDish> currentBoardState = List.of(
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0));
        Match matchToUpdate = new Match();
        matchToUpdate.setBoardState(currentBoardState);
        List<PetriDish> newBoardState = List.of(
            PetriDish.of(0,1),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> MatchMethodUtil.propagation(matchToUpdate, newBoardState, 1))
            .withMessageContaining("Players can't modify the other player's bacteria: {0}");
    }

    @Test
    @DisplayName("Should return error if player attempts to move bacteria from several dishes")
    @Description("Test that if playerX provides newBoardState that subtracts from PlayerXBacteria of several PetriDish, IllegalArgumentException is thrown")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    void testPropagationNegative7() {

        List<PetriDish> currentBoardState = List.of(
            PetriDish.of(1,0),
            PetriDish.of(1,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0));
        Match matchToUpdate = new Match();
        matchToUpdate.setBoardState(currentBoardState);
        List<PetriDish> newBoardState = List.of(
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(2,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> MatchMethodUtil.propagation(matchToUpdate, newBoardState, 1))
            .withMessageContaining("Players can't move bacteria from more than one petri dish: {1,0}");
    }

    @Test
    @DisplayName("Should return error if player attempts to move sarcinas (5 bacteria)")
    @Description("Test that if playerX provides newBoardState that subtracts from PlayerXBacteria == 5 (a sarcina), IllegalArgumentException is thrown")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    void testPropagationNegative8() {

        List<PetriDish> currentBoardState = List.of(
            PetriDish.of(5,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0));
        Match matchToUpdate = new Match();
        matchToUpdate.setBoardState(currentBoardState);
        List<PetriDish> newBoardState = List.of(
            PetriDish.of(4,0),
            PetriDish.of(1,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> MatchMethodUtil.propagation(matchToUpdate, newBoardState, 1))
            .withMessageContaining("Sarcinas can't be moved: {0}");
    }

    @Test
    @DisplayName("Should return error if player moves no bacteria")
    @Description("Test that if playerX provides newBoardState that doesn't change PlayerXBacteria of any PetriDish, IllegalArgumentException is thrown")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    void testPropagationNegative9() {

        List<PetriDish> currentBoardState = List.of(
            PetriDish.of(1,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0));
        Match matchToUpdate = new Match();
        matchToUpdate.setBoardState(currentBoardState);
        List<PetriDish> newBoardState = currentBoardState;

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> MatchMethodUtil.propagation(matchToUpdate, newBoardState, 1))
            .withMessageContaining("Players must move at least one bacteria: {atLeastOne}");
    }

    @Test
    @DisplayName("Should return error if player adds or removes bacteria from the board")
    @Description("Test that if playerX provides newBoardState that subtracts a different amount from PlayerXBacteria than it adds to other PetriDish, IllegalArgumentException is thrown")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    void testPropagationNegative10() {

        List<PetriDish> currentBoardState = List.of(
            PetriDish.of(4,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0));
        Match matchToUpdate = new Match();
        matchToUpdate.setBoardState(currentBoardState);
        List<PetriDish> newBoardState = List.of(
            PetriDish.of(2,0),
            PetriDish.of(1,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> MatchMethodUtil.propagation(matchToUpdate, newBoardState, 1))
            .withMessageContaining("Inconsistency in the number of bacteria that moved: {inconsistency}");
    }

    @Test
    @DisplayName("Should return error if player moves bacteria to non-adyacent dishes")
    @Description("Test that if playerX provides newBoardState that subtracts PlayerXBacteria then adds the amount to non-adyacent PetriDish, IllegalArgumentException is thrown")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    void testPropagationNegative11() {

        List<PetriDish> currentBoardState = List.of(
            PetriDish.of(4,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0));
        Match matchToUpdate = new Match();
        matchToUpdate.setBoardState(currentBoardState);
        List<PetriDish> newBoardState = List.of(
            PetriDish.of(1,0),
            PetriDish.of(1,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(1,0),
            PetriDish.of(1,0));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> MatchMethodUtil.propagation(matchToUpdate, newBoardState, 1))
            .withMessageContaining("Players can only move bacteria to adyacent dishes: {adyacency}");
    }

    @Test
    @DisplayName("Should change board state into new one if valid propagation")
    @Description("Test that if newBoardState corresponds to a valid propagation from currentBoardState, the match with newBoardState as boardState is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    void testPropagationPositive() {

        List<PetriDish> currentBoardState = List.of(
            PetriDish.of(4,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0));
        Match matchToUpdate = new Match();
        matchToUpdate.setBoardState(currentBoardState);
        List<PetriDish> newBoardState = List.of(
            PetriDish.of(1,0),
            PetriDish.of(2,0),
            PetriDish.of(1,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(0,0));

        assertThat(MatchMethodUtil.propagation(matchToUpdate, newBoardState, 1)).hasBoardState(newBoardState);
    }

    @Test
    @DisplayName("Should add bacteria to dish with only one player's bacteria in binary fission")
    @Description("Test that if a PetriDish only contains bacteria from one player during binary fission, the amount increases by one (unless already at 5: sarcina)")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    void testBinaryFissionPositive() {

        List<PetriDish> currentBoardState = List.of(
            PetriDish.of(1,0),
            PetriDish.of(0,2),
            PetriDish.of(0,3),
            PetriDish.of(4,0),
            PetriDish.of(5,0),
            PetriDish.of(1,1),
            PetriDish.of(0,0));
        Match matchToUpdate = new Match();
        matchToUpdate.setBoardState(currentBoardState);
        List<PetriDish> newBoardState = List.of(
            PetriDish.of(2,0),
            PetriDish.of(0,3),
            PetriDish.of(0,4),
            PetriDish.of(5,0),
            PetriDish.of(5,0),
            PetriDish.of(1,1),
            PetriDish.of(0,0));

        assertThat(MatchMethodUtil.binaryFission(matchToUpdate)).hasBoardState(newBoardState);
    }

    @Test
    @DisplayName("Should add score in contamination for more bacteria than opponent in Petri dish")
    @Description("Test that for every PetriDish that contains more bacteria from one player than from the other during contamination, the score of that player increases by one (unless already at MAX)")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    void testContaminationPositive() {

        List<PetriDish> currentBoardState = List.of(
            PetriDish.of(1,0),
            PetriDish.of(0,2),
            PetriDish.of(3,3),
            PetriDish.of(4,0),
            PetriDish.of(4,5),
            PetriDish.of(1,2),
            PetriDish.of(0,1));
        int player1Score = 0;
        int player2Score = 8;
        Match matchToUpdate = new Match();
        matchToUpdate.setPlayer1Score(player1Score);
        matchToUpdate.setPlayer2Score(player2Score);
        matchToUpdate.setBoardState(currentBoardState);
        
        Match result = MatchMethodUtil.contamination(matchToUpdate);
        assertThat(result).hasPlayer1Score(player1Score + 2);
        assertThat(result).hasPlayer2Score(MatchDataUtil.MAX_SCORE);
    }

    @Test
    @DisplayName("Should return null if winner not decided")
    @Description("Test that if no win condition is fulfilled, null is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    void testGetWinnerPositive() {

        List<PetriDish> currentBoardState = List.of(
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(1,0),
            PetriDish.of(0,0),
            PetriDish.of(0,1),
            PetriDish.of(0,0),
            PetriDish.of(0,0));
        int turn = 0;
        int player1Score = 0;
        int player2Score = 0;
        Match matchToUpdate = new Match();
        matchToUpdate.setTurn(turn);
        matchToUpdate.setTurnType(MatchDataUtil.getTurnType(turn));
        matchToUpdate.setPlayer1Score(player1Score);
        matchToUpdate.setPlayer2Score(player2Score);
        matchToUpdate.setBoardState(currentBoardState);

        org.assertj.core.api.Assertions.assertThat(MatchMethodUtil.getWinner(matchToUpdate)).isNull();
    }

    @Test
    @DisplayName("Should return number of player with less score when past last turn")
    @Description("Test that if turn > MAX, the number of the player with less score is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    void testGetWinnerPositive2() {

        List<PetriDish> currentBoardState = List.of(
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(1,0),
            PetriDish.of(0,0),
            PetriDish.of(0,1),
            PetriDish.of(0,0),
            PetriDish.of(0,0));
        int turn = MatchDataUtil.getTurnsNum();
        int player1Score = 0;
        int player2Score = 1;
        Match matchToUpdate = new Match();
        matchToUpdate.setTurn(turn);
        matchToUpdate.setPlayer1Score(player1Score);
        matchToUpdate.setPlayer2Score(player2Score);
        matchToUpdate.setBoardState(currentBoardState);

        org.assertj.core.api.Assertions.assertThat(MatchMethodUtil.getWinner(matchToUpdate)).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return number of player with less tokens if same score when past last turn")
    @Description("Test that if turn > MAX, the number of the player with less tokens is returned when both players have the same score")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    void testGetWinnerPositive3() {

        List<PetriDish> currentBoardState = List.of(
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(3,0),
            PetriDish.of(0,0),
            PetriDish.of(0,1),
            PetriDish.of(0,5),
            PetriDish.of(0,0));
        int turn = MatchDataUtil.getTurnsNum();
        int player1Score = 1;
        int player2Score = 1;
        Match matchToUpdate = new Match();
        matchToUpdate.setTurn(turn);
        matchToUpdate.setPlayer1Score(player1Score);
        matchToUpdate.setPlayer2Score(player2Score);
        matchToUpdate.setBoardState(currentBoardState);

        org.assertj.core.api.Assertions.assertThat(MatchMethodUtil.getWinner(matchToUpdate)).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return number of player with less sarcinas if same score and tokens when past last turn")
    @Description("Test that if turn > MAX, the number of the player with less sarcinas is returned when both players have the same score and tokens")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    void testGetWinnerPositive4() {

        List<PetriDish> currentBoardState = List.of(
            PetriDish.of(0,0),
            PetriDish.of(5,0),
            PetriDish.of(3,0),
            PetriDish.of(0,1),
            PetriDish.of(0,1),
            PetriDish.of(0,5),
            PetriDish.of(0,5));
        int turn = MatchDataUtil.getTurnsNum();
        int player1Score = 1;
        int player2Score = 1;
        Match matchToUpdate = new Match();
        matchToUpdate.setTurn(turn);
        matchToUpdate.setPlayer1Score(player1Score);
        matchToUpdate.setPlayer2Score(player2Score);
        matchToUpdate.setBoardState(currentBoardState);

        org.assertj.core.api.Assertions.assertThat(MatchMethodUtil.getWinner(matchToUpdate)).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return number of other player if no possible propagation")
    @Description("Test that if a player can't make a valid propagation, the number of the other player is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    void testGetWinnerPositive5() {

        List<PetriDish> currentBoardState = List.of(
            PetriDish.of(5,0),
            PetriDish.of(5,0),
            PetriDish.of(5,0),
            PetriDish.of(5,2),
            PetriDish.of(5,2),
            PetriDish.of(5,2),
            PetriDish.of(2,1));
        int turn = 0;
        int player1Score = 1;
        int player2Score = 1;
        Match matchToUpdate = new Match();
        matchToUpdate.setTurn(turn);
        matchToUpdate.setTurnType(MatchDataUtil.getTurnType(turn));
        matchToUpdate.setPlayer1Score(player1Score);
        matchToUpdate.setPlayer2Score(player2Score);
        matchToUpdate.setBoardState(currentBoardState);

        org.assertj.core.api.Assertions.assertThat(MatchMethodUtil.getWinner(matchToUpdate)).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return number of other player if max score reached")
    @Description("Test that if a player reaches the maximum score, the number of the other player is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    void testGetWinnerPositive6() {

        List<PetriDish> currentBoardState = List.of(
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(1,0),
            PetriDish.of(0,0),
            PetriDish.of(0,1),
            PetriDish.of(0,0),
            PetriDish.of(0,0));
        int turn = 0;
        int player1Score = 9;
        int player2Score = 0;
        Match matchToUpdate = new Match();
        matchToUpdate.setTurn(turn);
        matchToUpdate.setTurnType(MatchDataUtil.getTurnType(turn));
        matchToUpdate.setPlayer1Score(player1Score);
        matchToUpdate.setPlayer2Score(player2Score);
        matchToUpdate.setBoardState(currentBoardState);

        org.assertj.core.api.Assertions.assertThat(MatchMethodUtil.getWinner(matchToUpdate)).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return number of player with less tokens if max score reached by both players")
    @Description("Test that if both players reach the maximum score the same turn, the number of the player with less tokens is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    void testGetWinnerPositive7() {

        List<PetriDish> currentBoardState = List.of(
            PetriDish.of(0,0),
            PetriDish.of(0,0),
            PetriDish.of(3,0),
            PetriDish.of(0,0),
            PetriDish.of(0,1),
            PetriDish.of(0,5),
            PetriDish.of(0,0));
        int turn = 0;
        int player1Score = 9;
        int player2Score = 9;
        Match matchToUpdate = new Match();
        matchToUpdate.setTurn(turn);
        matchToUpdate.setTurnType(MatchDataUtil.getTurnType(turn));
        matchToUpdate.setPlayer1Score(player1Score);
        matchToUpdate.setPlayer2Score(player2Score);
        matchToUpdate.setBoardState(currentBoardState);

        org.assertj.core.api.Assertions.assertThat(MatchMethodUtil.getWinner(matchToUpdate)).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return number of player with less sarcinas if max score reached by both players and same score")
    @Description("Test that if both players reach the maximum score the same turn with the same amount of tokens, the number of the player with less sarcinas is returned")
    @Owner("josbardel1(WHS7046)")
    @Story("Play game")
    void testGetWinnerPositive8() {

        List<PetriDish> currentBoardState = List.of(
            PetriDish.of(0,0),
            PetriDish.of(5,0),
            PetriDish.of(3,0),
            PetriDish.of(0,1),
            PetriDish.of(0,1),
            PetriDish.of(0,5),
            PetriDish.of(0,5));
        int turn = 0;
        int player1Score = 9;
        int player2Score = 9;
        Match matchToUpdate = new Match();
        matchToUpdate.setTurn(turn);
        matchToUpdate.setTurnType(MatchDataUtil.getTurnType(turn));
        matchToUpdate.setPlayer1Score(player1Score);
        matchToUpdate.setPlayer2Score(player2Score);
        matchToUpdate.setBoardState(currentBoardState);

        org.assertj.core.api.Assertions.assertThat(MatchMethodUtil.getWinner(matchToUpdate)).isEqualTo(1);
    }

}
