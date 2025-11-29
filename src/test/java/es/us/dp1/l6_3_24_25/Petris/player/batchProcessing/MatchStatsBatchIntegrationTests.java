package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.match.repository.MatchRepository;
import es.us.dp1.l6_3_24_25.Petris.match.service.MatchService;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.model.Statistics;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Epic("Match statistics batch")
@Feature("Automatic player stats updates")
@Owner("match-batch-team")
class MatchStatsBatchIntegrationTests {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private MatchStatsBatchOrchestrator matchStatsBatchOrchestrator;

    @Autowired
    private MatchService matchService;

    private TransactionTemplate requiresNewTemplate;

    @Autowired
    void configureTransactionTemplate(@NonNull PlatformTransactionManager transactionManager) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.requiresNewTemplate = template;
    }

    @Test
    @DisplayName("Match finalizado actualiza estadísticas de ambos jugadores")
    @Story("Finished matches trigger stats updates")
    @Description("Verifies that a completed match publishes payloads and increments all player statistics accordingly.")
    @Severity(SeverityLevel.CRITICAL)
    void shouldUpdateStatsForFinishedMatch() {
        Match finishedMatch = matchRepository.findById(1).orElseThrow();
        assertThat(finishedMatch.getEndedAt()).as("sample match must be finished").isNotNull();

        int player1Sarcines = 2;
        int player2Sarcines = 1;
        Map<String, Integer> player1Initial = loadStatsSnapshot(finishedMatch.getPlayer1().getId());
        Map<String, Integer> player2Initial = loadStatsSnapshot(finishedMatch.getPlayer2().getId());

        requiresNewTemplate.executeWithoutResult(status -> {
            Integer matchId = Objects.requireNonNull(finishedMatch.getId());
            Match managedMatch = matchRepository.findById(matchId).orElseThrow();
            managedMatch.setBoardState(buildBoardState(player1Sarcines, player2Sarcines));
            matchStatsBatchOrchestrator.triggerForMatch(Objects.requireNonNull(managedMatch));
        });

        Map<String, Integer> player1Updated = awaitStatsIncrement(
            finishedMatch.getPlayer1().getId(),
            player1Initial,
            StatProcessor.GAMES_PLAYED
        );
        Map<String, Integer> player2Updated = awaitStatsIncrement(
            finishedMatch.getPlayer2().getId(),
            player2Initial,
            StatProcessor.GAMES_PLAYED
        );

        assertThat(player1Updated.getOrDefault(StatProcessor.GAMES_PLAYED, 0))
            .isEqualTo(player1Initial.getOrDefault(StatProcessor.GAMES_PLAYED, 0) + 1);
        assertThat(player1Updated.getOrDefault(StatProcessor.GAMES_WON, 0))
            .isEqualTo(player1Initial.getOrDefault(StatProcessor.GAMES_WON, 0) + 1);
        assertThat(player1Updated.getOrDefault(StatProcessor.SARCINES_CREATED, 0))
            .isEqualTo(player1Initial.getOrDefault(StatProcessor.SARCINES_CREATED, 0) + player1Sarcines);

        assertThat(player2Updated.getOrDefault(StatProcessor.GAMES_PLAYED, 0))
            .isEqualTo(player2Initial.getOrDefault(StatProcessor.GAMES_PLAYED, 0) + 1);
        assertThat(player2Updated.getOrDefault(StatProcessor.GAMES_WON, 0))
            .isEqualTo(player2Initial.getOrDefault(StatProcessor.GAMES_WON, 0));
        assertThat(player2Updated.getOrDefault(StatProcessor.SARCINES_CREATED, 0))
            .isEqualTo(player2Initial.getOrDefault(StatProcessor.SARCINES_CREATED, 0) + player2Sarcines);
    }

    @Test
    @DisplayName("Abandonar una partida también dispara el batch de estadísticas")
    @Story("Leaving a match still records stats")
    @Description("Ensures the batch job runs when a player abandons an ongoing match and stats change for both players.")
    @Severity(SeverityLevel.CRITICAL)
    void shouldUpdateStatsWhenPlayerLeavesMatch() {
        Match ongoingMatch = matchRepository.findById(10).orElseThrow();
        assertThat(ongoingMatch.getStartedAt()).isNotNull();
        assertThat(ongoingMatch.getEndedAt()).isNull();

        Player player1 = ongoingMatch.getPlayer1();
        Player player2 = ongoingMatch.getPlayer2();
        assertThat(player1).isNotNull();
        assertThat(player2).isNotNull();

        int player1Sarcines = 1;
        int player2Sarcines = 3;

        requiresNewTemplate.executeWithoutResult(status -> {
            ongoingMatch.setBoardState(buildBoardState(player1Sarcines, player2Sarcines));
            matchRepository.save(ongoingMatch);
        });

        Map<String, Integer> player1Initial = loadStatsSnapshot(player1.getId());
        Map<String, Integer> player2Initial = loadStatsSnapshot(player2.getId());

        Integer matchId = Objects.requireNonNull(ongoingMatch.getId());
        Match matchSnapshot = matchRepository.findById(matchId).orElseThrow();
        matchService.leaveMatch(Objects.requireNonNull(matchSnapshot), player1);

        Map<String, Integer> player1Updated = awaitStatsIncrement(player1.getId(), player1Initial, StatProcessor.GAMES_PLAYED);
        Map<String, Integer> player2Updated = awaitStatsIncrement(player2.getId(), player2Initial, StatProcessor.GAMES_PLAYED);

        assertThat(player1Updated.getOrDefault(StatProcessor.GAMES_PLAYED, 0))
            .isEqualTo(player1Initial.getOrDefault(StatProcessor.GAMES_PLAYED, 0) + 1);
        assertThat(player1Updated.getOrDefault(StatProcessor.GAMES_WON, 0))
            .isEqualTo(player1Initial.getOrDefault(StatProcessor.GAMES_WON, 0));
        assertThat(player1Updated.getOrDefault(StatProcessor.SARCINES_CREATED, 0))
            .isEqualTo(player1Initial.getOrDefault(StatProcessor.SARCINES_CREATED, 0) + player1Sarcines);

        assertThat(player2Updated.getOrDefault(StatProcessor.GAMES_PLAYED, 0))
            .isEqualTo(player2Initial.getOrDefault(StatProcessor.GAMES_PLAYED, 0) + 1);
        assertThat(player2Updated.getOrDefault(StatProcessor.GAMES_WON, 0))
            .isEqualTo(player2Initial.getOrDefault(StatProcessor.GAMES_WON, 0) + 1);
        assertThat(player2Updated.getOrDefault(StatProcessor.SARCINES_CREATED, 0))
            .isEqualTo(player2Initial.getOrDefault(StatProcessor.SARCINES_CREATED, 0) + player2Sarcines);
    }

    @Test
    @DisplayName("Forzar el fin de partida persiste las estadísticas")
    @Story("Forced match ending updates stats")
    @Description("Covers the admin force-end action to guarantee stats are persisted even when the match doesn't finish naturally.")
    @Severity(SeverityLevel.CRITICAL)
    void shouldUpdateStatsWhenMatchForceEnded() {
        Match ongoingMatch = matchRepository.findById(10).orElseThrow();
        assertThat(ongoingMatch.getStartedAt()).isNotNull();
        assertThat(ongoingMatch.getEndedAt()).isNull();

        Player player1 = ongoingMatch.getPlayer1();
        Player player2 = ongoingMatch.getPlayer2();
        assertThat(player1).isNotNull();
        assertThat(player2).isNotNull();

        int player1Sarcines = 3;
        int player2Sarcines = 2;

        requiresNewTemplate.executeWithoutResult(status -> {
            ongoingMatch.setBoardState(buildBoardState(player1Sarcines, player2Sarcines));
            matchRepository.save(ongoingMatch);
        });

        Map<String, Integer> player1Initial = loadStatsSnapshot(player1.getId());
        Map<String, Integer> player2Initial = loadStatsSnapshot(player2.getId());

        Match forceEndRequest = matchRepository.findById(Objects.requireNonNull(ongoingMatch.getId())).orElseThrow();
        forceEndRequest.setWinner(1);
        matchService.forceEndMatch(forceEndRequest);

        Map<String, Integer> player1Updated = awaitStatsIncrement(player1.getId(), player1Initial, StatProcessor.GAMES_PLAYED);
        Map<String, Integer> player2Updated = awaitStatsIncrement(player2.getId(), player2Initial, StatProcessor.GAMES_PLAYED);

        assertThat(player1Updated.getOrDefault(StatProcessor.GAMES_PLAYED, 0))
            .isEqualTo(player1Initial.getOrDefault(StatProcessor.GAMES_PLAYED, 0) + 1);
        assertThat(player1Updated.getOrDefault(StatProcessor.GAMES_WON, 0))
            .isEqualTo(player1Initial.getOrDefault(StatProcessor.GAMES_WON, 0) + 1);
        assertThat(player1Updated.getOrDefault(StatProcessor.SARCINES_CREATED, 0))
            .isEqualTo(player1Initial.getOrDefault(StatProcessor.SARCINES_CREATED, 0) + player1Sarcines);

        assertThat(player2Updated.getOrDefault(StatProcessor.GAMES_PLAYED, 0))
            .isEqualTo(player2Initial.getOrDefault(StatProcessor.GAMES_PLAYED, 0) + 1);
        assertThat(player2Updated.getOrDefault(StatProcessor.GAMES_WON, 0))
            .isEqualTo(player2Initial.getOrDefault(StatProcessor.GAMES_WON, 0));
        assertThat(player2Updated.getOrDefault(StatProcessor.SARCINES_CREATED, 0))
            .isEqualTo(player2Initial.getOrDefault(StatProcessor.SARCINES_CREATED, 0) + player2Sarcines);
    }

    private Map<String, Integer> statsAsMap(List<Statistics> stats) {
        Map<String, Integer> result = new HashMap<>();
        if (stats == null) {
            return result;
        }
        for (Statistics statistic : stats) {
            result.put(statistic.getName(), statistic.getValor());
        }
        return result;
    }

    private Map<String, Integer> loadStatsSnapshot(Integer playerId) {
        return requiresNewTemplate.execute(status -> {
            Player player = playerService.getPlayerById(playerId);
            return statsAsMap(player.getStatistics());
        });
    }

    private Map<String, Integer> awaitStatsIncrement(Integer playerId,
                                                     Map<String, Integer> baseline,
                                                     String statKey) {
        int expected = baseline.getOrDefault(statKey, 0) + 1;
        long deadline = System.currentTimeMillis() + 5_000;
        Map<String, Integer> current = baseline;
        while (System.currentTimeMillis() < deadline) {
            current = loadStatsSnapshot(playerId);
            if (current.getOrDefault(statKey, 0) >= expected) {
                return current;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return current;
            }
        }
        return current;
    }

    private List<PetriDish> buildBoardState(int player1Sarcines, int player2Sarcines) {
        List<PetriDish> dishes = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            PetriDish dish = new PetriDish();
            dish.setPlayer1Bacteria(i < player1Sarcines ? 5 : 0);
            dish.setPlayer2Bacteria(i >= 3 && i < 3 + player2Sarcines ? 5 : 0);
            dishes.add(dish);
        }
        return dishes;
    }
}
