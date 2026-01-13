package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import io.qameta.allure.Owner;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;

@Owner("DiegoVicenteCamara(RXW1249)")
class JobCompletionNotificationListenerTest {

    @Test
    void afterJob_logsCompletedAndNonCompleted() {
        JobCompletionNotificationListener listener = new JobCompletionNotificationListener();

        JobExecution completed = new JobExecution(1L);
        completed.setStatus(BatchStatus.COMPLETED);
        listener.afterJob(completed);

        JobExecution failed = new JobExecution(2L);
        failed.setStatus(BatchStatus.FAILED);
        listener.afterJob(failed);
    }
}
