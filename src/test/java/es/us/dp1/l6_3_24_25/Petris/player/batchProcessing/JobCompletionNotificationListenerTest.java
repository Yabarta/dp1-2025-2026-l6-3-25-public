package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;

@Epic("Match statistics batch")
@Feature("Batch listener")
class JobCompletionNotificationListenerTest {

    @Test
    @Story("Job completion notification")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("afterJob maneja COMPLETED y FAILED")
    @Description("Verifies that afterJob handles both COMPLETED and FAILED statuses without errors.")
    @Owner("DiegoVicenteCamara(RXW1249)")
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
