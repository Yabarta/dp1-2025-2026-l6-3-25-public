package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;

@Service
public class MatchStatsBatchOrchestrator {
    private static final int SARCINA_THRESHOLD = 5;

    private final JobLauncher jobLauncher;
    private final @NonNull Job updatePlayerStatsJob;
    private final TemporaryMatchStatStore statStore;
    private final ApplicationEventPublisher eventPublisher;
    private final TaskExecutor taskExecutor;

    public MatchStatsBatchOrchestrator(JobLauncher jobLauncher,
                                       @Qualifier("updatePlayerStatsJob") @NonNull Job updatePlayerStatsJob,
                                       TemporaryMatchStatStore statStore,
                                       ApplicationEventPublisher eventPublisher) {
        this.jobLauncher = jobLauncher;
        this.updatePlayerStatsJob = updatePlayerStatsJob;
        this.statStore = statStore;
        this.eventPublisher = eventPublisher;
        this.taskExecutor = new SimpleAsyncTaskExecutor("match-stats-batch-");
    }

    public void triggerForMatch(@NonNull Match match) {
        List<MatchStatPayload> payloads = new ArrayList<>();
        addPayloadIfPresent(payloads, match, match.getPlayer1(), 1);
        addPayloadIfPresent(payloads, match, match.getPlayer2(), 2);

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            eventPublisher.publishEvent(new DeferredStatsEvent(List.copyOf(payloads)));
        } else {
            payloads.forEach(this::stageAndLaunchJob);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDeferredStats(DeferredStatsEvent event) {
        if (event == null || event.payloads().isEmpty()) {
            return;
        }
        Runnable dispatch = () -> {
            event.payloads().forEach(this::stageAndLaunchJob);
        };
        taskExecutor.execute(dispatch);
    }

    private void addPayloadIfPresent(List<MatchStatPayload> payloads, Match match, Player player, int playerIndex) {
        if (player == null || player.getId() == null) {
            return;
        }
        payloads.add(buildPayload(match, player, playerIndex));
    }

    private void stageAndLaunchJob(MatchStatPayload payload) {
        statStore.stage(payload);
        JobParameters params = new JobParametersBuilder()
            .addLong("matchId", Objects.requireNonNull(payload.matchId()))
            .addLong("playerId", Objects.requireNonNull(payload.playerId()))
            .addLong("launchedAt", System.currentTimeMillis())
            .toJobParameters();
        try {
            jobLauncher.run(updatePlayerStatsJob, params);
        } catch (JobExecutionException ex) {
            throw new RuntimeException("Unable to launch stats job for match " + payload.matchId() + " and player " + payload.playerId(), ex);
        } catch (Exception ex) {
            throw new RuntimeException("Unexpected error launching stats job for match " + payload.matchId() + " and player " + payload.playerId(), ex);
        }
    }

    private MatchStatPayload buildPayload(Match match, Player player, int playerIndex) {
        boolean playerWon = match.getWinner() != null && match.getWinner() == playerIndex;
        int sarcinesCreated = countSarcines(match.getBoardState(), playerIndex);

        return new MatchStatPayload(
            match.getId().longValue(),
            player.getId().longValue(),
            1,
            playerWon ? 1 : 0,
            sarcinesCreated
        );
    }

    private int countSarcines(List<PetriDish> dishes, int playerIndex) {
        if (dishes == null) {
            return 0;
        }
        int count = 0;
        for (PetriDish dish : dishes) {
            if (dish == null) {
                continue;
            }
            Integer value = playerIndex == 1 ? dish.getPlayer1Bacteria() : dish.getPlayer2Bacteria();
            if (value != null && value == SARCINA_THRESHOLD) {
                count++;
            }
        }
        return count;
    }

    private record DeferredStatsEvent(List<MatchStatPayload> payloads) { }
}
