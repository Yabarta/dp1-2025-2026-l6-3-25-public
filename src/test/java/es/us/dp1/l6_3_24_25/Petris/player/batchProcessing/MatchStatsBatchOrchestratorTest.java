package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import es.us.dp1.l6_3_24_25.Petris.match.model.PetriDish;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.SyncTaskExecutor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Owner("DiegoVicenteCamara(RXW1249)")
@ExtendWith(MockitoExtension.class)
class MatchStatsBatchOrchestratorTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job job;

    @Mock
    private TemporaryMatchStatStore statStore;

    @Mock
    private ApplicationEventPublisher publisher;

    private MatchStatsBatchOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        orchestrator = new MatchStatsBatchOrchestrator(jobLauncher, job, statStore, publisher);
    }

    @Test
    void triggerForMatch_stagesPayloadsAndLaunchesJobs() throws Exception {
        when(jobLauncher.run(any(), any(JobParameters.class))).thenReturn(new JobExecution(1L));

        Player p1 = new Player();
        p1.setId(1);
        Player p2 = new Player();
        p2.setId(2);

        PetriDish d1 = PetriDish.of(5, 2); // player1 sarcina
        PetriDish d2 = PetriDish.of(3, 4);

        Match match = new Match();
        match.setId(5);
        match.setWinner(1);
        match.setStartedAt(LocalDateTime.now().minusSeconds(30));
        match.setEndedAt(LocalDateTime.now());
        match.setBoardState(List.of(d1, d2));
        match.setPlayer1(p1);
        match.setPlayer2(p2);
        match.setCreator(p1);

        orchestrator.triggerForMatch(match);

        ArgumentCaptor<MatchStatPayload> payloadCaptor = ArgumentCaptor.forClass(MatchStatPayload.class);
        verify(statStore, times(2)).stage(payloadCaptor.capture());

        List<MatchStatPayload> payloads = payloadCaptor.getAllValues();
        assertEquals(2, payloads.size());

        MatchStatPayload p1Payload = payloads.stream().filter(p -> p.playerId() == 1L).findFirst().orElseThrow();
        assertEquals(1, p1Payload.gamesPlayedDelta());
        assertEquals(1, p1Payload.gamesWonDelta());
        assertEquals(1, p1Payload.sarcinasCreatedDelta());
        assertEquals(match.getDuration().intValue(), p1Payload.timePlayedDelta());
        assertEquals(8, p1Payload.bacteriasCreatedDelta());

        MatchStatPayload p2Payload = payloads.stream().filter(p -> p.playerId() == 2L).findFirst().orElseThrow();
        assertEquals(1, p2Payload.gamesPlayedDelta());
        assertEquals(0, p2Payload.gamesWonDelta());
        assertEquals(0, p2Payload.sarcinasCreatedDelta());
        assertEquals(match.getDuration().intValue(), p2Payload.timePlayedDelta());
        assertEquals(6, p2Payload.bacteriasCreatedDelta());

        verify(jobLauncher, times(2)).run(eq(job), any(JobParameters.class));
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void triggerForMatch_skipsNullPlayersAndHandlesNullBoard() throws Exception {
        when(jobLauncher.run(any(), any(JobParameters.class))).thenReturn(new JobExecution(1L));

        Player p1 = new Player();
        p1.setId(1);

        Match match = new Match();
        match.setId(6);
        match.setWinner(null);
        match.setStartedAt(LocalDateTime.now().minusSeconds(10));
        match.setEndedAt(LocalDateTime.now());
        match.setBoardState(null); // exercise null handling paths
        match.setPlayer1(p1);
        match.setPlayer2(null); // only one player present
        match.setCreator(p1);

        orchestrator.triggerForMatch(match);

        ArgumentCaptor<MatchStatPayload> payloadCaptor = ArgumentCaptor.forClass(MatchStatPayload.class);
        verify(statStore).stage(payloadCaptor.capture());
        MatchStatPayload payload = payloadCaptor.getValue();

        assertEquals(0, payload.sarcinasCreatedDelta());
        assertEquals(0, payload.bacteriasCreatedDelta());
        verify(jobLauncher).run(eq(job), any(JobParameters.class));
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void handleDeferredStats_runsOnExecutor() throws Exception {
        when(jobLauncher.run(any(), any(JobParameters.class))).thenReturn(new JobExecution(1L));

        MatchStatPayload payload = new MatchStatPayload(7L, 3L, 1, 1, 1, 10, 10);
        Object event = buildDeferredEvent(List.of(payload));
        injectSyncExecutor();

        invokeHandleDeferred(event);

        verify(statStore).stage(payload);
        verify(jobLauncher).run(eq(job), any(JobParameters.class));
    }

    private Object buildDeferredEvent(List<MatchStatPayload> payloads) throws Exception {
        Class<?> eventClass = Class.forName(MatchStatsBatchOrchestrator.class.getName() + "$DeferredStatsEvent");
        Constructor<?> ctor = eventClass.getDeclaredConstructors()[0];
        ctor.setAccessible(true);
        return ctor.newInstance(payloads);
    }

    private void invokeHandleDeferred(Object event) throws Exception {
        Class<?> eventClass = event.getClass();
        orchestrator.getClass().getMethod("handleDeferredStats", eventClass).invoke(orchestrator, event);
    }

    private void injectSyncExecutor() throws Exception {
        Field field = MatchStatsBatchOrchestrator.class.getDeclaredField("taskExecutor");
        field.setAccessible(true);
        field.set(orchestrator, new SyncTaskExecutor());
    }
}
