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

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

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
            throw new AccessDeniedException("The match has already started. Concede instead");
        }
        if (!match.hasPlayer(playerToLeave)) {
            throw new AccessDeniedException("Not in this match");
        } else if(match.hasCreator(playerToLeave)) {
            Player currentPlayer2 = match.getPlayer2();
            match.setCreator(currentPlayer2);
            match.setPlayer1(currentPlayer2);
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

        if(matchToUpdate.hasEnded()) {
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
    public List<String> checkErrors(Match matchToCheck, List<PetriDish> newBoardState, Player player) throws AccessDeniedException {
        int playerNum;
        if(matchToCheck.hasPlayer1(player)) {
            playerNum = 1;
        } else if(matchToCheck.hasPlayer2(player)) {
            playerNum = 2;
        } else {
            throw new AccessDeniedException("Not in this match");
        }

        if(matchToCheck.hasEnded()) {
            throw new AccessDeniedException("The match has already ended");
        }
        if(!matchToCheck.isTurnOf(player)) {
            throw new AccessDeniedException("Can only check for errors in your propagation turns");
        }

        return MatchHelper.getPropagationErrors(matchToCheck.getBoardState(), newBoardState, playerNum);
    }

    @Transactional(rollbackFor = {AccessDeniedException.class})
    public Match forceEndMatch(Match matchToUpdate, Player playerToConcede) throws AccessDeniedException {
        int playerNum;
        if(matchToUpdate.hasPlayer1(playerToConcede)) {
            playerNum = 1;
        } else if(matchToUpdate.hasPlayer2(playerToConcede)) {
            playerNum = 2;
        } else {
            throw new AccessDeniedException("Not in this match");
        }

        if(matchToUpdate.hasEnded()) {
            throw new AccessDeniedException("The match has already ended");
        }
        if(!matchToUpdate.isInPropagationTurn()) {
            throw new AccessDeniedException("Can only concede in propagation turns");
        }

        matchToUpdate.setWinner(playerNum);
        matchToUpdate.setEndedAt(LocalDateTime.now());

        return matchRepository.save(matchToUpdate);
    }

    @Transactional(rollbackFor = {AccessDeniedException.class})
    public void delete(Integer id) throws AccessDeniedException {
        matchRepository.deleteById(id);
    }

}
