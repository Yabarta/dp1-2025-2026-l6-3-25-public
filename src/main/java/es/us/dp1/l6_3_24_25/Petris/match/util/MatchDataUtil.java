package es.us.dp1.l6_3_24_25.Petris.match.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.model.TurnType;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchDataUtil {
    public static final int PLAYER_1_WINS = 1;
    public static final int PLAYER_2_WINS = 2;
    public static final int NUM_PETRI_DISHES = 7;
    public static final int PLAYER_1_INTITIAL_BACTERIUM_INDEX = 2;
    public static final int PLAYER_2_INTITIAL_BACTERIUM_INDEX = 4;
    public static final int MAX_BACTERIA_PER_PETRI_DISH = 5;
    public static final int MAX_SCORE = 9;

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

        initialMatch.setCode(generateLobbyCode(isPrivate));

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

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String CODE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int CODE_LENGTH = 4;
    private static String generateLobbyCode(Boolean matchIsPrivate) {
        String code = null;
        if (matchIsPrivate) {
            StringBuilder builder = new StringBuilder(CODE_LENGTH);
            for (int i = 0; i < CODE_LENGTH; i++) {
                int index = secureRandom.nextInt(CODE_ALPHABET.length());
                builder.append(CODE_ALPHABET.charAt(index));
            }
            code = builder.toString();
        }
        return code;
    }
}
