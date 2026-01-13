package es.us.dp1.l6_3_24_25.Petris.player.service;

import java.util.*;
import java.util.stream.IntStream;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;

import es.us.dp1.l6_3_24_25.Petris.player.model.PlayerRanking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.repository.PlayerRepository;

@Service
public class RankingService {

    @Autowired
    private PlayerRepository playerRepository;

    @Transactional(readOnly = true)
    public List<PlayerRanking> getGlobalRanking() {
        List<Player> players = playerRepository.findAll();
        List<PlayerRanking> ranking = new ArrayList<>();
        for (Player player : players) {
            PlayerRanking playerRanking = new PlayerRanking();
            Double score = player.getStatistics().getScore();
            if (score != null) {
                playerRanking.setNickname(player.getNickname());
                playerRanking.setPartidasJugadas(player.getStatistics().getGamesPlayed());
                playerRanking.setPartidasGanadas(player.getStatistics().getGamesWon());
                playerRanking.setSarcinasCreadas(player.getStatistics().getSarcinasCreated());
                playerRanking.setScore(score);
                playerRanking.setProfilePicture(player.getProfilePicture());
                ranking.add(playerRanking);
            }
        }

        ranking.sort(Comparator.comparing(PlayerRanking::getScore)
            .reversed()
            .thenComparing(PlayerRanking::getSarcinasCreadas, Comparator.reverseOrder()));

        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).setRankingPosition(i + 1);
        }

        return ranking;
    }
}
