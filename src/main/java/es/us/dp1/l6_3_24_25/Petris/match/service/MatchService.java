package es.us.dp1.l6_3_24_25.Petris.match.service;

import es.us.dp1.l6_3_24_25.Petris.exceptions.AccessDeniedException;
import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.MatchHelper;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.model.TurnType;
import es.us.dp1.l6_3_24_25.Petris.match.repository.MatchRepository;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.NonNull;

import es.us.dp1.l6_3_24_25.Petris.match.dto.LobbyDTO;
import es.us.dp1.l6_3_24_25.Petris.match.dto.MatchDTO;
import es.us.dp1.l6_3_24_25.Petris.match.dto.PetriDishDTO;
import es.us.dp1.l6_3_24_25.Petris.match.dto.PlayerSummaryDTO;

@Service
public class MatchService {
    private static final String CODE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int CODE_LENGTH = 4;

    private final MatchRepository matchRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public MatchService(final MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Transactional(readOnly = true)
    public List<Match> getAllMatches(){
        return matchRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Match getMatchById(Integer id){
        // TODO Only if public ? / Spectate
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

    @Transactional(rollbackFor = {AccessDeniedException.class})
    public Match createMatch(Player creator, boolean isPrivate) throws AccessDeniedException{
        if (creator.getIsCurrentlyInMatch()) {
            throw new AccessDeniedException("Already in a match");
        }

        Match initialMatch = MatchHelper.buildInitialMatch(creator, isPrivate);
        initialMatch.setCode(generateLobbyCode(isPrivate));

        return matchRepository.save(initialMatch);
    }

    @Transactional(rollbackFor = {AccessDeniedException.class})
    public Match joinMatch(Match match, Player playerToJoin, String code) throws AccessDeniedException{
        if (playerToJoin.getIsCurrentlyInMatch()) {
            throw new AccessDeniedException("Already in a match");
        }
        if(match.hasEnded()) {
            throw new AccessDeniedException("The match has already ended");
        } else if(match.hasStarted()) {
            throw new AccessDeniedException("The match has already started");
        }
        if(!match.isValidCode(code)) {
            throw new AccessDeniedException("Incorrect code for private match");
        }
        if (match.isFull()) {
            throw new AccessDeniedException("The match is already full");
        }

        match.setPlayer2(playerToJoin);

        return matchRepository.save(match);
    }

    @Transactional(rollbackFor = {AccessDeniedException.class})
    public Match leaveMatch(Match match, Player playerToLeave) {
        if(match.hasEnded()) {
            throw new AccessDeniedException("The match has already ended");
        } else if(match.hasStarted()) {
            throw new AccessDeniedException("The match has already started. Forfeit instead");
        }
        if (!match.hasPlayer(playerToLeave)) {
            throw new AccessDeniedException("Not in this match");
        } else if(match.hasCreator(playerToLeave)) {
            throw new AccessDeniedException("Unsupported. Delete match instead");
        }

        match.setPlayer2(null);

        return matchRepository.save(match);
    }

    @Transactional(rollbackFor = {AccessDeniedException.class})
    public Match startMatch(Match match) {
        if (!match.isFull()) {
            throw new AccessDeniedException("Two players are required to start the match");
        }
        if(match.hasEnded()) {
            throw new AccessDeniedException("The match has already ended");
        } else if(match.hasStarted()) {
            // This method can't be called appropriately (via MatchController) if the match has started
            throw new AccessDeniedException("Unsupported operation for started match");
        }

        match.setStartedAt(LocalDateTime.now());
        match.setTurn(0);

        return matchRepository.save(match);
    }

    @Transactional(rollbackFor = {IllegalArgumentException.class, AccessDeniedException.class})
    public Match nextTurn(Match matchToUpdate, List<PetriDish> newBoardState) throws IllegalArgumentException, AccessDeniedException {
        Integer currentTurn = matchToUpdate.getTurn();
        if (currentTurn >= MatchHelper.getTurnsNum()) {
            throw new IllegalArgumentException("No remaining turns to process");
        }
        if(matchToUpdate.getEndedAt() != null) {
            throw new AccessDeniedException("The match has already ended");
        }

        Match updatedMatch;
        TurnType currentTurnType = matchToUpdate.getTurnType();
        switch (currentTurnType) {
            case TurnType.P1_PROPAGATION:
                try {
                    updatedMatch = MatchHelper.propagation(matchToUpdate, newBoardState, 1);
                } catch(IllegalArgumentException e) {
                    throw e;
                }
                break;
            case TurnType.P2_PROPAGATION:
                try {
                    updatedMatch = MatchHelper.propagation(matchToUpdate, newBoardState, 2);
                } catch(IllegalArgumentException e) {
                    throw e;
                }
                break;
            case TurnType.BINARY_FISSION:
                updatedMatch = MatchHelper.binaryFission(matchToUpdate);
                break;
            case TurnType.CONTAMINATION:
                updatedMatch = MatchHelper.contamination(matchToUpdate);
                break;
            default:
                throw new IllegalStateException("Unsupported turn type: " + currentTurnType);
        }

        int nextTurnIndex = currentTurn + 1;
        updatedMatch.setTurn(nextTurnIndex);
        if (nextTurnIndex < MatchHelper.getTurnsNum()) {
            updatedMatch.setTurnType(MatchHelper.getTurnType(nextTurnIndex));
        } else {
            updatedMatch.setTurnType(null);
        }

        Integer winner = MatchHelper.getWinner(updatedMatch);
        if (winner != null) {
            updatedMatch.setEndedAt(LocalDateTime.now());
            updatedMatch.setWinner(winner);
        }
        return matchRepository.save(updatedMatch);
    }

    @Transactional(readOnly = true)
    public List<String> checkErrors(List<PetriDish> currentBoardState, List<PetriDish> newBoardState, int player) {
        return MatchHelper.getPropagationErrors(currentBoardState, newBoardState, player);
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

    public String generateLobbyCode(Boolean matchIsPrivate) {
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
