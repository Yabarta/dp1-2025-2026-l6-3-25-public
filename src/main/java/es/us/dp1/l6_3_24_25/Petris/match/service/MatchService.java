package es.us.dp1.l6_3_24_25.Petris.match.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.match.dto.LobbyDTO;
import es.us.dp1.l6_3_24_25.Petris.match.dto.MatchDTO;
import es.us.dp1.l6_3_24_25.Petris.match.dto.PetriDishDTO;
import es.us.dp1.l6_3_24_25.Petris.match.dto.PlayerSummaryDTO;
import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.model.TurnType;
import es.us.dp1.l6_3_24_25.Petris.match.repository.MatchRepository;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;

@Service
public class MatchService {
    private static final String CODE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int CODE_LENGTH = 4;

    private final MatchRepository matchRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private MatchServiceHelper matchServiceHelper;

    public MatchService(final MatchRepository matchRepository,
                        final ObjectProvider<MatchServiceHelper> helperProvider) {
        this.matchRepository = matchRepository;
        this.matchServiceHelper = helperProvider.getIfAvailable();
        if (this.matchServiceHelper == null) {
            this.matchServiceHelper = new MatchServiceHelper(null, new ArrayList<>(), 1);
        }
    }


    @Transactional(readOnly = true)
    public List<Match> getAllMatches(){
        return matchRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Match getMatchById(@NonNull Integer id){
        return matchRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Match", "Id", id));
    }

    @Transactional(readOnly = true)
    public Match getMatchByCode(@NonNull String code){
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
    public Match createMatch(@NonNull Match match){
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
    public Match joinMatch(@NonNull Match match) {
        return matchRepository.save(match);
    }

    @Transactional
    public Match startMatch(@NonNull Match match) {
        match.setStartedAt(LocalDateTime.now());
        return matchRepository.save(match);
    }

    @Transactional
    public Optional<Match> leaveMatch(@NonNull Match match, @NonNull Player player) {
        boolean changed = false;
        if (player.equals(match.getPlayer1())) {
            match.setPlayer1(null);
            changed = true;
        } else if (player.equals(match.getPlayer2())) {
            match.setPlayer2(null);
            changed = true;
        }

        if (!changed) {
            return Optional.of(match);
        }

        if (player.equals(match.getCreator())) {
            match.setCreator(null);
        }

        if (match.getPlayer1() == null && match.getPlayer2() != null) {
            match.setPlayer1(match.getPlayer2());
            match.setPlayer2(null);
            if (match.getCreator() == null) {
                match.setCreator(match.getPlayer1());
            }
        } else if (match.getPlayer1() == null && match.getPlayer2() == null) {
            matchRepository.delete(match);
            return Optional.empty();
        } else if (match.getCreator() == null) {
            match.setCreator(match.getPlayer1());
        }

        Match saved = matchRepository.save(match);
        return Optional.of(saved);
    }

    @Transactional
    public Match nextTurn(@NonNull Match matchToUpdate, Optional<List<PetriDish>> newBoardState) throws IllegalArgumentException {
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

        if (updatedMatch == null) {
            throw new IllegalStateException("Unsupported turn type: " + matchToUpdate.getTurnType());
        }

        Integer turn = matchToUpdate.getTurn() + 1;

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

            if(currentPd.getPlayer2Bacteria() != 0 && newPd.getPlayer1Bacteria().equals(currentPd.getPlayer2Bacteria())) {
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
    public Match forceEndMatch(@NonNull Match match) {
        match.setEndedAt(LocalDateTime.now());
        return matchRepository.save(match);
    }

    @Transactional
    public void delete(@NonNull Integer id){
        matchRepository.deleteById(id);
    }

    public LobbyDTO toLobbyDTO(@NonNull Match match) {
        LobbyDTO dto = new LobbyDTO();
        dto.setId(match.getId());
        dto.setCode(match.getCode());
        dto.setPrivate(match.getCode() != null);
        dto.setCreatorId(match.getCreator() != null ? match.getCreator().getId() : null);
        dto.setCreatedAt(match.getCreatedAt());
        dto.setStartedAt(match.getStartedAt());
        dto.setPlayers(buildPlayerList(match));
        return dto;
    }

    public MatchDTO toMatchDTO(@NonNull Match match) {
        MatchDTO dto = new MatchDTO();
        dto.setId(match.getId());
        dto.setCode(match.getCode());
        dto.setCreatedAt(match.getCreatedAt());
        dto.setStartedAt(match.getStartedAt());
        dto.setEndedAt(match.getEndedAt());
        dto.setTurn(match.getTurn());
        dto.setTurnType(match.getTurnType());
        dto.setPlayer1Score(match.getPlayer1Score());
        dto.setPlayer2Score(match.getPlayer2Score());
        dto.setWinner(match.getWinner());
        dto.setPlayer1(toPlayerSummary(match.getPlayer1()));
        dto.setPlayer2(toPlayerSummary(match.getPlayer2()));
        List<PetriDishDTO> board = new ArrayList<>();
        List<PetriDish> dishes = match.getBoardState();
        if (dishes != null) {
            for (int i = 0; i < dishes.size(); i++) {
                PetriDish dish = dishes.get(i);
                PetriDishDTO dishDTO = new PetriDishDTO();
                dishDTO.setIndex(i);
                dishDTO.setPlayer1Bacteria(dish.getPlayer1Bacteria());
                dishDTO.setPlayer2Bacteria(dish.getPlayer2Bacteria());
                board.add(dishDTO);
            }
        }
        dto.setBoard(board);
        return dto;
    }

    public String generateLobbyCode() {
        StringBuilder builder = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = secureRandom.nextInt(CODE_ALPHABET.length());
            builder.append(CODE_ALPHABET.charAt(index));
        }
        return builder.toString();
    }

    private List<PlayerSummaryDTO> buildPlayerList(Match match) {
        List<PlayerSummaryDTO> players = new ArrayList<>();
        if (match.getPlayer1() != null) {
            players.add(toPlayerSummary(match.getPlayer1()));
        }
        if (match.getPlayer2() != null) {
            players.add(toPlayerSummary(match.getPlayer2()));
        }
        return players;
    }

    private PlayerSummaryDTO toPlayerSummary(Player player) {
        if (player == null) {
            return null;
        }
        PlayerSummaryDTO dto = new PlayerSummaryDTO();
        dto.setId(player.getId());
        dto.setNickname(player.getNickname());
        dto.setUsername(player.getUser() != null ? player.getUser().getUsername() : null);
        return dto;
    }
}
