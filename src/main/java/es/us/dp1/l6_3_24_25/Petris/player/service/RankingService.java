package es.us.dp1.l6_3_24_25.Petris.player.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;

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
    public Double getScore(Integer playerId) {
        Player p = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player", "id", playerId));

        if (p.getStatistics() == null)
            return null;

        Integer gp = p.getStatistics().getGamesPlayed();
        Integer gw = p.getStatistics().getGamesWon();
        if (gp == null || gw == null || gp < 10)
            return null;

        double winPercent = ((double) gw / (double) gp) * 100.0;
        return winPercent + 20.0 * Math.log10((double) gp);
    }

    @Transactional(readOnly = true)
    public List<Player> getGlobalRanking() {
        List<Player> players = playerRepository.findAll();
        List<Player> ranking = new ArrayList<>();
        Map<Integer, Double> scores = new HashMap<>();
        for (Player p : players) {
            if (p == null)
                continue;
            Double sc = getScore(p.getId());
            if (sc == null)
                continue; // not eligible or no data
            scores.put(p.getId(), sc);
            ranking.add(p);
        }

        ranking.sort((a, b) -> {
            double sa = scores.get(a.getId());
            double sb = scores.get(b.getId());
            int byScore = Double.compare(sb, sa);
            if (byScore != 0) return byScore;
            // tie-breaker: less sarcinasCreated wins
            Integer as = (a.getStatistics() != null && a.getStatistics().getSarcinasCreated() != null)
                ? a.getStatistics().getSarcinasCreated() : 0;
            Integer bs = (b.getStatistics() != null && b.getStatistics().getSarcinasCreated() != null)
                ? b.getStatistics().getSarcinasCreated() : 0;
            return Integer.compare(as, bs);
        });
        return ranking;
    }
}
